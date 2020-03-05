package ch.epfl.moocprog;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.random.UniformDistribution;
import ch.epfl.moocprog.utils.Time;

public final class Anthill extends Positionable {

	private double foodQuantity;
	private Uid anthillId;
	private double workerProb;
	private Time counter;
	
	public Anthill(ToricPosition pos,double foodQuantity) {
		super(pos);
		this.foodQuantity = foodQuantity;
		workerProb = Context.getConfig().getDouble(Config.ANTHILL_WORKER_PROB_DEFAULT);
		anthillId = Uid.createUid();
		counter = Time.ZERO;
	}
	public Anthill(ToricPosition pos) {
		this(pos,0.);
	}
	//methode respensable de la mise a jour de la fourmiliere
	public void update(AnthillEnvironmentView env, Time dt) {
		if(env == null || dt == null)
			throw new IllegalArgumentException();
		
		final Time anthill_spawn_delay = Context.getConfig().getTime(Config.ANTHILL_SPAWN_DELAY);
		counter = counter.plus(dt);
		
		while(counter.compareTo(anthill_spawn_delay) >= 0) {
			counter = counter.minus(anthill_spawn_delay);
			
			if(UniformDistribution.getValue(0, 1) > workerProb) {
				//si la valeur aleatoire est superieur a workePron la fourmiliere genere une fourmie ouvriere
				env.addAnt(new AntSoldier(getPosition(), getAnthillId()));
			}
			else {
				//sinon une fourmie soldat
				env.addAnt(new AntWorker(getPosition(), getAnthillId()));
			}
		}
	}
	public void dropFood(double toDrop) {
		if(toDrop < 0)
			throw new IllegalArgumentException();
		
		foodQuantity += toDrop;
	}
	public double getFoodQuantity(){
		return foodQuantity;
	}
	public Uid getAnthillId() {
		return anthillId;
	}
	
	@Override
	public String toString() {
		String s = super.toString() + "\n";
		s += "Quantity : " + getFoodQuantity();
		return s;
	}
}

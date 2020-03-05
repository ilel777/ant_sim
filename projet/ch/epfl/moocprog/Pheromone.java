package ch.epfl.moocprog;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.utils.Time;

public final class Pheromone extends Positionable {
	
	private double quantity;
	public Pheromone(ToricPosition pos, double quantity) {
		super(pos);
		this.quantity = quantity;
	}

	public double getQuantity(){
		return quantity;
	}
	
	public boolean isNegligible() {
		if(getQuantity() < Context.getConfig().getDouble(Config.PHEROMONE_THRESHOLD))
			return true;
		
		return false;
	}
	
	public void update(Time dt) {
		if(!isNegligible()) {
			quantity -= (dt.toSeconds()*Context.getConfig().getDouble(Config.PHEROMONE_EVAPORATION_RATE));
			if(getQuantity() < 0)
				quantity = 0;
		}
	}
}

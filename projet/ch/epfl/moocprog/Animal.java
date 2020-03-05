package ch.epfl.moocprog;


import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.random.UniformDistribution;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Utils;
import ch.epfl.moocprog.utils.Vec2d;

public abstract class Animal extends Positionable{

	private double angle;
	private int hitpoints;
	private Time lifespan;
	//private RotationProbability rotation_probability;
	private Time rotationDelay;
	
	public Animal(ToricPosition pos,int hitpoints, Time lifespan){
		super(pos);
		this.hitpoints = hitpoints;
		this.lifespan = lifespan;
		angle = UniformDistribution.getValue(0., 2*Math.PI);
		//rotation_probability = computeRotationProbs();
		rotationDelay = Time.ZERO;
	}
	
	public final double getDirection(){
		return angle;
	}
	//getters
	public final int getHitpoints() {
		return hitpoints;
	}
	public final Time getLifespan() {
		return lifespan;
	}
	@Override
	public String toString() {
		String s = super.toString() + " \n";
		s += "Speed : " + getSpeed() +" \n";
		s += "HitPoints : " + getHitpoints() + " \n";
		s += String.format("LifeSpan : %.6f", getLifespan().toSeconds());
		return s;
	}
	
	final public void setDirection(double angle){
		this.angle = angle;
	}
	final public boolean isDead(){
		if(getHitpoints() <= 0 || getLifespan().toSeconds() <= 0)
			return true;
		
		return false;
	}
	//methode respensable de la mise a jour de l'etat de l'animal au cours du temps
	final public void update(AnimalEnvironmentView env, Time dt){
		double lifespan_decrease_factor = Context.getConfig().getDouble(Config.ANIMAL_LIFESPAN_DECREASE_FACTOR);
		lifespan = lifespan.minus(dt.times(lifespan_decrease_factor));
		if(lifespan.compareTo(Time.ZERO) > 0){
			this.specificBehaviorDispatch(env, dt);
		}
	}
	//methode respensable d'appliquer une rotation aleatoire a l'animal
	private void rotate(){
		
//		System.out.println("before :");
//		System.out.println(this);
//		double ang = getDirection();
		setDirection((getDirection() + Utils.pickValue(computeRotationProbs().getAngles(), computeRotationProbs().getProbabilities()))%(Math.PI*2));
//		System.out.println("after :");
//		System.out.println(this);
//		
//		if(getDirection() != ang){
//			System.out.println("=================================================================");
//			System.out.println("Bingo : ");
//			System.out.println(ang);
//			System.out.println(getDirection());
//			System.out.println("=================================================================");
//		}
	}
	//methode gerant le mouvement de l'animal
	/*protected final void move(Time dt){
		if(dt == null)
			throw new IllegalArgumentException();
		Time animal_next_rotation_delay = Context.getConfig().getTime(Config.ANIMAL_NEXT_ROTATION_DELAY);
		Time period = Time.ZERO;
		//l'animal doit continuer de bouger tant que dt n'a pas ecoule
		while(dt.toSeconds() > 0){
			period = period.plus(rotationDelay);
			//tant que delay de rotation est different de zero alors il faut continuer a bouger sans faire de rotation
			if(rotationDelay.toSeconds() > 0.){
				if(dt.compareTo(rotationDelay) >= 0){
					period = period.plus(rotationDelay);
					setPosition(getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(period.toSeconds()*getSpeed())));
					dt = dt.minus(period);
					rotationDelay = Time.ZERO;	//on remets le delay a zero quand on a chaque animal_next_rotation_delay ecoule
					rotate();
				}
				else{
					setPosition(getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(dt.toSeconds()*getSpeed())));
					rotationDelay = rotationDelay.minus(dt);//on decrimente le delay apres chaque mouvement par le temps ecoule durant ce mouvement (on fait de meme pour dt)
					dt = dt.minus(dt);
				}
			}
		/*	else if(dt.compareTo(animal_next_rotation_delay) >= 0){
				//rotate();//si le delay esat a zero ca veut dire qu'une rotation est requise
				rotationDelay = animal_next_rotation_delay;
				setPosition(getPosition().add(Vec2d.fromAngle(angle).scalarProduct(dt.toSeconds()*getSpeed())));
				dt = dt.minus(rotationDelay);
				rotationDelay = Time.ZERO;
			}
			else{
				//rotate();//si le delay esat a zero ca veut dire qu'une rotation est requise
				rotationDelay = dt;
				setPosition(getPosition().add(Vec2d.fromAngle(angle).scalarProduct(dt.toSeconds()*getSpeed())));
				dt = dt.minus(rotationDelay);
				rotationDelay = animal_next_rotation_delay.minus(rotationDelay);//si le temps dt ecoule avant l'arrive de la prochaine rotation en stocke le temps manquant dans rotationDelay
																				//tant que ce temps n'a pas ecoule on fait pas de rotation
			}
			else{
				rotationDelay = rotationDelay.plus(dt);
				while(rotationDelay.compareTo(animal_next_rotation_delay) >= 0){
					setPosition(getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(animal_next_rotation_delay.toSeconds()*getSpeed())));
					rotationDelay = rotationDelay.minus(animal_next_rotation_delay);
					dt = dt.minus(animal_next_rotation_delay);
					rotate();
				}
				setPosition(getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(rotationDelay.toSeconds()*getSpeed())));
				dt = dt.minus(rotationDelay);
				rotationDelay = animal_next_rotation_delay.minus(rotationDelay);
			}
			
			//if(rotationDelay == Time.ZERO)
				//rotate();//si le delay esat a zero ca veut dire qu'une rotation est requise
		}
		
	}*/
	protected final void move(Time dt){
		if(dt == null)
			throw new IllegalArgumentException();
		
		Time animal_next_rotation_delay = Context.getConfig().getTime(Config.ANIMAL_NEXT_ROTATION_DELAY);
		
		while(dt.compareTo(Time.ZERO) > 0){
			Time period = animal_next_rotation_delay.minus(rotationDelay);
			if(dt.compareTo(period) >= 0){
				setPosition(getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(period.toSeconds()*getSpeed())));
				rotate();
				dt = dt.minus(period);
				rotationDelay = Time.ZERO;
			}
			else{
				setPosition(getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(dt.toSeconds()*getSpeed())));
				rotationDelay = rotationDelay.plus(dt);
				dt = Time.ZERO;
			}
		}
	}
	//genere une instance de la classe RotationProbabiliy
	protected RotationProbability computeRotationProbs(){
		
		double[] angles = { -180, -100, -55, -25, -10, 0, 10, 25, 55, 100, 180 };
		double[] probabilities = { 0.0000, 0.0000, 0.0005, 0.0010, 0.0050, 0.9870, 0.0050, 0.0010, 0.0005, 0.0000, 0.0000 };

		//double [] angles = { 90,-90};
		//double [] probabilities = {50,50};
		
		for(int i = 0; i < angles.length; i++)
			angles[i] = Math.toRadians(angles[i]);
		
		return new RotationProbability(angles, probabilities);
	}
	abstract public void accept(AnimalVisitor visitor, RenderingMedia s);
	abstract public double getSpeed();
	abstract void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt);
}

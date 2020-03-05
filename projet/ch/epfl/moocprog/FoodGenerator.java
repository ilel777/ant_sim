package ch.epfl.moocprog;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.random.NormalDistribution;
import ch.epfl.moocprog.random.UniformDistribution;
import ch.epfl.moocprog.utils.Time;

public final class FoodGenerator {
	
	private Time compteur;
	
	public FoodGenerator() {
		compteur = Time.ZERO;
	}
	
	public void update(FoodGeneratorEnvironmentView env, Time dt){
		//on stocke les parametre recu par la methode getConfig() dans des variable local constante
		final Time food_generator_delay = Context.getConfig().getTime(Config.FOOD_GENERATOR_DELAY);
		final double new_food_quatity_min = Context.getConfig().getDouble(Config.NEW_FOOD_QUANTITY_MIN);
		final double new_food_quatity_max = Context.getConfig().getDouble(Config.NEW_FOOD_QUANTITY_MAX);
		final int world_heigth = Context.getConfig().getInt(Config.WORLD_HEIGHT); 
		final int world_width =  Context.getConfig().getInt(Config.WORLD_WIDTH);
		
		//on mis a jour le compteur
		if(dt == null || env == null)
			throw new IllegalArgumentException();
		
		compteur = compteur.plus(dt);
		
		//puis on genere dans l'environment tant de nouriture qu'il y a de cycle(food_generator_delay) en compteur
		while(compteur.compareTo(food_generator_delay) >= 0){
			compteur = compteur.minus(food_generator_delay);	//decremente le compteur d'un cycle
			
			//on genere une position aleatoirement
			double x = NormalDistribution.getValue(world_width/2., world_width*world_width/16.);
			double y = NormalDistribution.getValue(world_heigth/2., world_heigth*world_heigth/16.);
			ToricPosition pos = new ToricPosition(x, y);
			//on genere une quantite aleatoire de nouriture
			double quantity = UniformDistribution.getValue(new_food_quatity_min, new_food_quatity_max);
			
			//on genere la nouriture
			env.addFood(new Food(pos, quantity));
		}
	}
}

package ch.epfl.moocprog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.gfx.EnvironmentRenderer;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Utils;

public final class Environment implements FoodGeneratorEnvironmentView, AnimalEnvironmentView, AnthillEnvironmentView,
		AntWorkerEnvironmentView {

	private FoodGenerator food_gen;
	private List<Food> foods;
	private List<Animal> animals;
	private List<Anthill> anthills;
	private List<Pheromone> pheromones;

	// constructeurs
	public Environment() {
		food_gen = new FoodGenerator();
		foods = new LinkedList<>();
		animals = new LinkedList<>();
		anthills = new LinkedList<>();
		pheromones = new LinkedList<>();
	}

	// public methodes

	// implementation de l'interface FoodGeneratorEnvironmentView
	@Override
	public void addFood(Food f) {
		if (f == null) {
			throw new IllegalArgumentException();
		} else {
			foods.add(f);
		}
	}

	// methode retournant la liste des quantites de la nouriture stocke dans foods
	public List<Double> getFoodQuantities() {
		List<Double> quantities_list = new ArrayList<>();

		for (Food food : foods) {
			quantities_list.add(food.getQuantity());
		}

		return quantities_list;
	}

	// methode retournant la list des positions des animaux dans l'environment
	public List<ToricPosition> getAnimalsPosition() {
		List<ToricPosition> positions = new ArrayList<>();
		for (Animal animal : animals)
			positions.add(animal.getPosition());

		return positions;
	}

	// methode respensable de la mise a jour de l'environment
	public void update(Time dt) {
		// on genere de la nouriture
		food_gen.update(this, dt);

		// on met les pheromones a jour
		Iterator<Pheromone> pheromoneIterator = pheromones.iterator();
		while (pheromoneIterator.hasNext()) {
			Pheromone pheromone = pheromoneIterator.next();
			if (pheromone.getQuantity() == 0 || pheromone.isNegligible())
				// on suprime les pheromones avec quantite null
				pheromoneIterator.remove();
			else
				pheromone.update(dt);
		}
		// on met les fourmiliere a jour
		for (Anthill anthill : anthills)
			anthill.update(this, dt);
		// on met a jour chaque animal contenu dans cet environment
		Iterator<Animal> iterator = animals.iterator();
		while (iterator.hasNext()) {
			Animal animal = iterator.next();
			if (animal.isDead())
				iterator.remove();// on suprime de la liste tout les animaux morts
			else {
				animal.update(this, dt);// les animaux en vie sont mis a jour
			}
		}
		foods.removeIf(food -> food.getQuantity() <= 0);
	}

	// methode destinee a proceder au rendu graphique des constituants de
	// l'environnement
	public void renderEntities(EnvironmentRenderer environmentRenderer) {
		anthills.forEach(environmentRenderer::renderAnthill);
		foods.forEach(environmentRenderer::renderFood);
		animals.forEach(environmentRenderer::renderAnimal);
	}

	public void addAnthill(Anthill anthill) {
		if (anthill == null)
			throw new IllegalArgumentException();

		anthills.add(anthill);
	}

	public void addAnimal(Animal animal) {
		if (animal == null)
			throw new IllegalArgumentException();

		animals.add(animal);
	}

	public int getWidth() {
		return Context.getConfig().getInt(Config.WORLD_WIDTH);
	}

	public int getHeight() {
		return Context.getConfig().getInt(Config.WORLD_HEIGHT);
	}

	@Override
	public void addAnt(Ant ant) {
		if (ant == null)
			throw new IllegalArgumentException();

		addAnimal(ant);
	}

	@Override
	public Food getClosestFoodForAnt(AntWorker antWorker) {

		if (foods == null || antWorker == null)
			throw new IllegalArgumentException();
		if (foods.isEmpty())
			return null;

		double max_perception_distance = Context.getConfig().getDouble(Config.ANT_MAX_PERCEPTION_DISTANCE);
		Food closestFood = Utils.closestFromPoint(antWorker, foods);

		if (antWorker.getPosition().toricDistance(closestFood.getPosition()) > max_perception_distance)
			return null;

		return closestFood;
	}

	@Override
	public boolean dropFood(AntWorker antWorker) {
		if (anthills == null || antWorker == null)
			throw new IllegalArgumentException();

		if (anthills.isEmpty())
			return false;

		double max_perception_distance = Context.getConfig().getDouble(Config.ANT_MAX_PERCEPTION_DISTANCE);
		Anthill closestAnthill = Utils.closestFromPoint(antWorker, anthills);

		if (antWorker.getPosition().toricDistance(closestAnthill.getPosition()) <= max_perception_distance)
			if (antWorker.getAnthillId() == closestAnthill.getAnthillId()) {
				if (antWorker.getFoodQuantity() != 0) {
					closestAnthill.dropFood(antWorker.getFoodQuantity());
					return true;
				}
			}

		return false;
	}

	@Override
	public void selectSpecificBehaviorDispatch(AntWorker antWorker, Time dt) {
		if (antWorker == null)
			throw new IllegalArgumentException();

		antWorker.seekForFood(this, dt);
	}

	@Override
	public void selectSpecificBehaviorDispatch(AntSoldier antSoldier, Time dt) {
		if (antSoldier == null)
			throw new IllegalArgumentException();

		antSoldier.seekForEnemies(this, dt);
	}

	@Override
	public void addPheromone(Pheromone pheromone) {
		if (pheromone == null)
			throw new IllegalArgumentException();

		pheromones.add(pheromone);
	}

	public List<Double> getPheromonesQuantities() {
		List<Double> quantities = new ArrayList<>();

		for (Pheromone pheromone : pheromones)
			quantities.add(pheromone.getQuantity());

		return quantities;
	}

	@Override
	public double[] getPheromoneQuantitiesPerIntervalForAnt(ToricPosition position, double directionAngleRad,
			double[] angles) {
		if(position == null || angles == null || angles.length == 0)
			throw new IllegalArgumentException();
		
		double[] quantities = new double[angles.length];
		for (double angle : angles)
			angle = Math.toRadians(angle);

		for (Pheromone pheromone : pheromones) {
			if (!pheromone.isNegligible()) {
				if (position.toricDistance(pheromone.getPosition()) <= Context.getConfig()
						.getDouble(Config.ANT_SMELL_MAX_DISTANCE)) {
					// double beta =
					// closestAngleFrom(position.toricVector(pheromone.getPosition()).angle(),
					// directionAngleRad);

					double beta = position.toricVector(pheromone.getPosition()).angle() - directionAngleRad;
					int j = 0;
					double closestAngle = closestAngleFrom(angles[0], beta);
					for (int i = 0; i < angles.length; i++) {
						if (closestAngleFrom(angles[i], beta) < closestAngle) {
							closestAngle = closestAngleFrom(angles[i], beta);
							j = i;
						}
					}

					quantities[j] += pheromone.getQuantity();
				}
			}
		}
		return quantities;
	}

	private static double normalizedAngle(double angle) {
		angle %= 2 * Math.PI;
		while (angle < 0.)
			angle += 2 * Math.PI;

		return angle;
	}

	private static double closestAngleFrom(double angle, double target) {
		double diff = normalizedAngle(angle - target);

		return ((diff < (2 * Math.PI - diff)) ? diff : 2 * Math.PI - diff);
	}
}

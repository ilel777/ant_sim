package ch.epfl.moocprog;

public interface AntEnvironmentView extends AnimalEnvironmentView {

	void addPheromone(Pheromone pheromone);

	double[] getPheromoneQuantitiesPerIntervalForAnt(ToricPosition position, double directionAngleRad, double[] angles);
}

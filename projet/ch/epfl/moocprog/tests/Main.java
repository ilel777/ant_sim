package ch.epfl.moocprog.tests;

import ch.epfl.moocprog.app.ApplicationInitializer;
import static ch.epfl.moocprog.config.Config.PHEROMONE_THRESHOLD;

import ch.epfl.moocprog.AntWorker;
import ch.epfl.moocprog.Environment;
import ch.epfl.moocprog.Pheromone;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.config.ImmutableConfigManager;
import java.io.File;
import java.util.Arrays;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.WORLD_HEIGHT;
import static ch.epfl.moocprog.config.Config.WORLD_WIDTH;

import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Vec2d;
import ch.epfl.moocprog.ToricPosition;
import ch.epfl.moocprog.Uid;
import ch.epfl.moocprog.Positionable;

public class Main {

	public static void main(String[] args) {
		ApplicationInitializer.initializeApplication(new ImmutableConfigManager(new File("res/app.cfg")));
		final int width = getConfig().getInt(WORLD_WIDTH);
		final int height = getConfig().getInt(WORLD_HEIGHT);

		ToricPosition tp1 = new ToricPosition();
		ToricPosition tp2 = new ToricPosition(1.2, 2.3);
		ToricPosition tp3 = new ToricPosition(new Vec2d(4.5, 6.7));
		ToricPosition tp4 = tp3.add(tp2);
		ToricPosition tp5 = new ToricPosition(width, height);
		ToricPosition tp6 = new ToricPosition(width / 2, height / 2);
		ToricPosition tp7 = tp4.add(tp6.add(new Vec2d(width / 2, height / 2)));
		ToricPosition tp8 = new ToricPosition(3, 4);
		Vec2d v1 = tp2.toricVector(tp3);

		System.out.println("Some tests for ToricPosition");
		System.out.println("Default toric position : " + tp1);
		System.out.println("tp2 : " + tp2);
		System.out.println("tp3 : " + tp3);
		System.out.println("tp4 (tp2 + tp3) : " + tp4);
		System.out.println("Toric vector between tp2 and tp3 : " + v1);
		System.out.println("World dimension (clamped) : " + tp5);
		System.out.println("Half world dimension : " + tp6);
		System.out.println("tp3 + 2 * half world dimension = " + tp7);
		System.out.println("Length of vector (3, 4) : " + tp1.toricDistance(tp8));

		Positionable p1 = new Positionable();
		Positionable p2 = new Positionable(tp4);

		System.out.println();
		System.out.println("Some tests for Positionable");
		System.out.println("Default position : " + p1.getPosition());
		System.out.println("Initialized at tp4 : " + p2.getPosition());
		// quelques tests pour l'étape 10
		System.out.println();
		double minQty = getConfig().getDouble(PHEROMONE_THRESHOLD);
		Pheromone pher1 = new Pheromone(new ToricPosition(10., 10.), minQty);
		System.out.print("Pheromone pher1 created with quantity PHEROMONE_THRESHOLD = ");
		System.out.println(minQty);
		System.out.println("the position of the pheromone is :" + pher1.getPosition());
		System.out.println(
				"getQuantity() correctly returns the value " + minQty + " : " + (pher1.getQuantity() == minQty));
		System.out.print("the quantity of the pheromone is negligible : ");
		System.out.println(pher1.isNegligible());
		Environment env = new Environment();
		env.addPheromone(pher1);
		env.update(Time.fromSeconds(1.));
		System.out.print("After one step of evaporation (dt = 1 sec), ");
		System.out.print(" the quantity of pher1 is ");
		System.out.println(pher1.getQuantity() + "\n");
		double offset = minQty / 5.;
		Pheromone pher2 = new Pheromone(new ToricPosition(20., 20.),
				getConfig().getDouble(PHEROMONE_THRESHOLD) - offset);
		System.out.println("Pheromone created with quantity PHEROMONE_THRESHOLD - " + offset);
		System.out.println("the position of the pheromone is :" + pher2.getPosition());
		System.out.print("the quantity of the pheromone is negligible : ");
		System.out.println(pher2.isNegligible() + "\n");
		env.addPheromone(pher2);

		System.out.print("The quantities of pheromone in the environment are: ");
		System.out.println(env.getPheromonesQuantities());
		env.update(Time.fromSeconds(1.));
		// toute les quantités deviennent négligeables et doivent être supprimées
		System.out.print("After one update of the environment, ");
		System.out.print("the quantities of pheromone in the environment are: ");
		System.out.println(env.getPheromonesQuantities() + "\n");
		System.out.println("Finding pheromones around a given position : ");
		ToricPosition antPosition = new ToricPosition(100., 100.);
		Pheromone pher3 = new Pheromone(new ToricPosition(105., 105.), 1.0);
		Pheromone pher4 = new Pheromone(new ToricPosition(95., 95.), 2.0);
		// cette quantité est trop éloignée (ne doit pas être perçue) :
		Pheromone pher5 = new Pheromone(new ToricPosition(500., 500.), 4.0);
		env.addPheromone(pher3);
		env.addPheromone(pher4);
		env.addPheromone(pher5);
		System.out.print("The quantities of pheromone in the environment are: ");
		System.out.println(env.getPheromonesQuantities());
		double[] pheromonesAroundPosition = env.getPheromoneQuantitiesPerIntervalForAnt(antPosition, 0.,
				new double[] { -180, -100, -55, -25, -10, 0, 10, 25, 55, 100, 180 });
		System.out.println(Arrays.toString(pheromonesAroundPosition) + "\n");
		System.out.print("After enough time, no pheromones should be left : ");
		env.update(Time.fromSeconds(30.));
		System.out.println(env.getPheromonesQuantities());
		
//		AntWorker antWorker = new AntWorker(new ToricPosition(), Uid.createUid());
//		env.addAnimal(antWorker);
//		env.update(Time.fromSeconds(0.5));
//		antWorker.spreadPheromones(env);
//		System.out.println("The antworker moved. Distance : "
//		+ new ToricPosition().toricDistance(antWorker.getPosition()));
//		double density = getConfig().getDouble(Config.ANT_PHEROMONE_DENSITY);
//		double energy = getConfig().getDouble(Config.ANT_PHEROMONE_ENERGY);
//		System.out.println("The density of pheromone is : "
//		+ density + " and the energy : " + energy);
//		System.out.println("The antworker spreads pheromones");
//		System.out.println("The quantities of pheromone in the environment are now: ");
//		System.out.println(env.getPheromonesQuantities());
	}
}

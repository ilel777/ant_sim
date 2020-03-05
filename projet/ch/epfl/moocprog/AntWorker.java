package ch.epfl.moocprog;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Vec2d;

public final class AntWorker extends Ant {

	private double foodQuantity;

	public AntWorker(ToricPosition pos, Uid anthillId) {
		super(pos, Context.getConfig().getInt(Config.ANT_WORKER_HP),
				Context.getConfig().getTime(Config.ANT_WORKER_LIFESPAN), anthillId);
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public double getSpeed() {
		return Context.getConfig().getDouble(Config.ANT_WORKER_SPEED);
	}

	public double getFoodQuantity() {
		return foodQuantity;
	}

	@Override
	public String toString() {
		String s = super.toString() + "\n";
		s += "Quantity : " + getFoodQuantity();
		return s;
	}

	protected void seekForFood(AntWorkerEnvironmentView env, Time dt) {
		if (env == null || dt == null)
			throw new IllegalArgumentException();

		final double ant_max_food = Context.getConfig().getDouble(Config.ANT_MAX_FOOD);

		Food closestFood = env.getClosestFoodForAnt(this);
		if (getFoodQuantity() == 0.) {

			if (closestFood != null) {

				foodQuantity = closestFood.takeQuantity(ant_max_food);

				setDirection((getDirection() + Math.PI) % (2 * Math.PI));
				seekForFood(env, dt);

			} else {
				// si la fourmie n'a aucune source de nouriture en vue alors elle fait le
				// mouvement par defaut
				setPosition(
						getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(dt.toSeconds() * getSpeed())));
				dt = Time.ZERO;

			}
		} else {
			if (env.dropFood(this)) {
				// si la fourmie est assez proche de la fourmilere elle pose alors la nouriture
				// et fait un demit tour
				foodQuantity = 0.;
				setDirection((getDirection() + Math.PI) % (2 * Math.PI));
			} else {
				setPosition(
						getPosition().add(Vec2d.fromAngle(getDirection()).scalarProduct(dt.toSeconds() * getSpeed())));
				dt = Time.ZERO;
			}
		}
	}

	@Override
	void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) {
		if (env == null || dt == null)
			throw new IllegalArgumentException();

		env.selectSpecificBehaviorDispatch(this, dt);
	}
}

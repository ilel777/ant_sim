package ch.epfl.moocprog;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Vec2d;

public abstract class Ant extends Animal {
	
	private Uid anthillId;
	private ToricPosition currentPos, lastPos;
	
	public Ant(ToricPosition pos, int hitPoits, Time lifeSpan, Uid anthillId) {
		super(pos, hitPoits, lifeSpan);
		this.anthillId = anthillId;
		lastPos = pos;
	}

	public final Uid getAnthillId() {
		return anthillId;
	}
	
	private void spreadPheromones(AntEnvironmentView env) {
		currentPos = getPosition();
		double distance = lastPos.toricDistance(currentPos);
		Vec2d step = lastPos.toricVector(currentPos).scalarProduct(1/(distance*Context.getConfig().getDouble(Config.ANT_PHEROMONE_DENSITY)));

		while(distance > 0) {
			env.addPheromone(new Pheromone(lastPos, Context.getConfig().getDouble(Config.ANT_PHEROMONE_ENERGY)));

			lastPos = lastPos.add(step);
			distance -= step.length();
		}
	}
}

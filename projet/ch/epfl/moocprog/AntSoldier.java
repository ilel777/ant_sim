package ch.epfl.moocprog;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.utils.Time;

public final class AntSoldier extends Ant {

	public AntSoldier(ToricPosition pos, Uid anthillId) {
		super(pos,Context.getConfig().getInt(Config.ANT_SOLDIER_HP),Context.getConfig().getTime(Config.ANT_SOLDIER_LIFESPAN),anthillId);
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public double getSpeed() {
		return Context.getConfig().getDouble(Config.ANT_SOLDIER_SPEED);
	}

	protected void seekForEnemies(AntEnvironmentView env, Time dt) {
		if(env == null || dt == null)
			throw new IllegalArgumentException();
		
		move(dt);
	}

	@Override
	void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) {
		if(env == null || dt == null)
			throw new IllegalArgumentException();
		
		env.selectSpecificBehaviorDispatch(this, dt);
	}
}

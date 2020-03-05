package ch.epfl.moocprog;

import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.utils.Time;

public final class Termite extends Animal {

	public Termite(ToricPosition pos) {
		super(pos,Context.getConfig().getInt(Config.TERMITE_HP), Context.getConfig().getTime(Config.TERMITE_LIFESPAN));
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public double getSpeed() {
		return Context.getConfig().getDouble(Config.TERMITE_SPEED);	
	}

	@Override
	void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) {
		move(dt);
	}

}

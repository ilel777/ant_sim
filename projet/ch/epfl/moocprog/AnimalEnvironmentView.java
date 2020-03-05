package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;

public interface AnimalEnvironmentView {
	void selectSpecificBehaviorDispatch(AntWorker antWorker, Time dt);
	void selectSpecificBehaviorDispatch(AntSoldier antSoldier, Time dt);
}

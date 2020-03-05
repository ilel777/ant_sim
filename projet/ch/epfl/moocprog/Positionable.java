package ch.epfl.moocprog;

public class Positionable {

	private ToricPosition position;
	
	//Constructeurs
	public Positionable(){
		position = new ToricPosition();
	}
	public Positionable(ToricPosition pos){
		this.position = pos;
	}
	
	//getter
	public ToricPosition getPosition() {
		return position;
	}
	
	//protected setter
	protected final void setPosition(ToricPosition pos){
		position = pos;
	}
	
	@Override
	public String toString() {
		return getPosition().toString();
	}
}

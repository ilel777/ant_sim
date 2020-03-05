package ch.epfl.moocprog;

public final class Food extends Positionable{

	//la nouriture est caracterisee par sa position et sa quatite
	private double quantity;
	
	//Constructeurs
	public Food(ToricPosition position, double quantity){
		super(position);
		if(quantity < 0)
			quantity = 0;
		
		this.quantity = quantity;
	}
	
	//getters
	public double getQuantity() {
		return quantity;
	}
	//public methodes
	public double takeQuantity(double value){
		if(value < 0)
			throw new IllegalArgumentException();
		else
		{
			if(value <= getQuantity()){
				quantity -= value;
				return value;
			}
			else
			{
				value = quantity;
				quantity = 0;
				return value;
			}
		}
	}
	
	
	@Override
	public String toString() {
		String s = super.toString() + "\n";
		s += String.format("Quantity : %.2f", getQuantity());
		
		return s;
	}
}

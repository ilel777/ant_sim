package ch.epfl.moocprog;

public class RotationProbability {

	private double[] angles;
	private double[] probabilities;
	
	//Constructeurs
	public RotationProbability(double[] angles, double[] probabilities) {
		if(angles == null || probabilities == null)
			throw new IllegalArgumentException();
		else if(angles.length != probabilities.length)
			throw new IllegalArgumentException();
		
		
		this.angles = angles.clone();
		this.probabilities = probabilities.clone();
	}

	//getters
	public double[] getAngles() {
		return angles.clone();
	}
	public double[] getProbabilities() {
		return probabilities.clone();
	}
	
	//methodes
//	public double  computeRotationProbs(){
//		return Utils.pickValue(getAngles(), getProbabilities());
//	}
}

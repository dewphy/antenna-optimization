package algorithm;

public interface Algorithm {
	
	public float[] getBestPosition();
	public float getBestFitness();
	
	public float[] getBestFitnesses();
	public float[][] getBestPositions();
	boolean isOptimumFound();
	public int getNumberOfEvaluations();
}

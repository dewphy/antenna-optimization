
public interface Algorithm {
	
	public boolean isOptimumFound();
	public float[] getBestPosition();
	public float getBestFitness();
	
	public float[] getBestFitnesses();
	public float[][] getBestPositions();

}

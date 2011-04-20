

public interface FitnessEvaluator {	

	public void load();
	public float evaluate(float[] position);
	public void print();
	public int getNumberOfEvaluations();

	public float[] getUpperBound();
	public float[] getLowerBound();

	public float getBestFitness();

	public float[][] getBestPosition();
	public int getPositionLength();
	
	public boolean isOptimumFound();
}

package benchmark;

public interface FitnessEvaluator {	

	public void load();

	public float evaluate(float[] position);
	public float evaluate(int[] position);

	public void print();
	public int getNumberOfEvaluations();

	public float[] getUpperBound();
	public float[] getLowerBound();

	public float getBestFitness();

	public float[][] getBestPosition();
	public int getPositionLength();

	public boolean isOptimumFound();
	
	public int[] pickRandomPosition();
	public int[] pickBestNeighbor(int[] position);
}

package benchmark;

public interface FitnessEvaluator {	

	public void load();

	public float evaluate(float[] position);
	public float evaluate(int[] position);
	public float[] evaluate(float[][] positions);
	public float[] evaluate(int[][] positions);

	public boolean isOptimumFound();
	public boolean shouldTerminate();
	public int getNumberOfEvaluations();

	public float[] getUpperBound();
	public float[] getLowerBound();

	public float[] convertPositionToFloat(int[] position);
	public int getPositionLength();

	public int[] pickRandomPosition();
	public int[] pickAnyNeighbor(int[] position);
	public int[] pickBestNeighbor(int[] position);

	public float[] getBestPosition();
	public float[][] getBestPositions();
	public float getBestFitness();
	public float[] getBestFitnesses();

	public int[] getBestIntPosition();

	public void print();
	public void print(int[] position);

	int[] getMaxIndexes();


}

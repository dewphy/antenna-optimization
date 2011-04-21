package benchmark;

public interface FitnessEvaluator {
	String PATH_BENCH1="data/directivity-b1.txt";
	String PATH_BENCH2="data/directivity-b2.txt";
	String PATH_BENCH3="data.txt";
	String PATH_BENCH4="data.txt";
	
	float THETA_STEP= new Float(0.5f);
	float LENGTH_STEP=new Float(0.02f);
	float DISTANCE_STEP=new Float(0.08f);
	float BETA_STEP=new Float(0.03f);
	
	float UPPER_THETA_1=90f;
	float UPPER_THETA_2=180f;
	float LOWER_THETA=0f;
	float UPPER_LENGTH=3f;
	float LOWER_LENGTH=0.5f;
	float UPPER_DISTANCE=15f;
	float LOWER_DISTANCE=5f;
	float LOWER_BETA=0f;  
	float UPPER_BETA=30f;
	float LOWER_ALPHA=0f;
	float UPPER_ALPHA=30f;

	public void load();

	public float evaluate(float[] position);
	public float evaluate(int[] position);

	public void print();
	public int getNumberOfEvaluations();
	
	public int getPositionLength();
	
	public float[] getUpperBounds();
	public float[] getLowerBounds();
	public float[] getSteps();

	public float[] pickBestNeighbor(float[] position);

	int[] getMaxIndexes();

	public int[] pickRandomPosition();
	public int[] pickBestNeighbor(int[] position);

	public int[] pickAnyNeighbor(int[] position);
	
}

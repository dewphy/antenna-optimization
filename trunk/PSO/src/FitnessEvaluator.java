
public interface FitnessEvaluator {
	public void load(int benchmarkNumber);
	public float evaluate(float x1, float x2);
	public void print();
	public void getNumberOfEvaluation();
}

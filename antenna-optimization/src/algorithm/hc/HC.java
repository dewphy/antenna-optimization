package algorithm.hc;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;
import algorithm.Algorithm;

public class HC implements Algorithm {

	private int numberOfEvaluations;
	private int[] position;

	private final int maxNumberOfEvaluations;
	private final FitnessEvaluator evaluator;

	public HC(int benchmarkId, int maxNumberOfEvaluations) {
		this.maxNumberOfEvaluations = maxNumberOfEvaluations;

		// Initialize evaluator.
		evaluator = new FitnessEvaluatorImpl(benchmarkId);
		evaluator.load();

		// Pick a random position.
		position = evaluator.pickRandomPosition();
		System.out.println("position[0]: " + position[0] + ", position[1]: " + position[1] + ", fitness: " + evaluator.evaluate(position));

		System.out.println("Hill Climber started: ");
		for (numberOfEvaluations = 0; !shouldTerminate(); numberOfEvaluations++) { 
			int[] newPosition = evaluator.pickBestNeighbor(position);
//			int[] newPosition = evaluator.pickAnyNeighbor(position);
//			if (evaluator.evaluate(newPosition) >= evaluator.evaluate(position)) {
//				position = newPosition;
//			}
			
//			System.out.println("position[0]: " + position[0] + ", position[1]: " + position[1] + ", fitness: " + evaluator.evaluate(position));

			if (evaluator.evaluate(newPosition) == evaluator.evaluate(position)) {
				break;
			}
			position = newPosition;
		}
		System.out.println("Fitness: " + evaluator.evaluate(position));
	}

	private boolean shouldTerminate() {
		return (numberOfEvaluations >= maxNumberOfEvaluations);
	}

	@Override
	public float[] getBestPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getBestFitness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float[] getBestFitnesses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[][] getBestPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOptimumFound() {
		return evaluator.evaluate(position) == 3.2063f;
//		if (position[0] == 70 && position[1] == 103) {
//			return true;
//		} else if (position[0] == 71 && position[1] == 103) {
//			return true;
//		} else {
//			return false;
//		}
	}

	public int getNumberOfEvaluations() {
		// TODO Auto-generated method stub
		return 0;
	}

}

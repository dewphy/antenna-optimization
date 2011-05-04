package algorithm.hc;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;
import algorithm.Algorithm;

public class GHC implements Algorithm {

	private int[] position;
	private final FitnessEvaluator evaluator;

	public GHC(int benchmarkId, int maxNumberOfEvaluations) {

		// Initialize evaluator.
		evaluator = new FitnessEvaluatorImpl(benchmarkId, maxNumberOfEvaluations, true);
		evaluator.load();

		// Pick a random position.
		position = evaluator.pickRandomPosition();
		evaluator.print(position);

		for (int numberOfEvaluation = 1; !evaluator.shouldTerminate(); numberOfEvaluation++) {
			int[] newPosition = evaluator.pickBestNeighbor(position);

			// Update position.
			position = newPosition;

			// Stop if algorithm is stuck.
			if (numberOfEvaluation > 11000) {
				break;
			}
//			System.out.println("numberOfEvaluation: " + numberOfEvaluation);
		}
		evaluator.print(position);
	}

	@Override
	public boolean isOptimumFound() {
		return evaluator.isOptimumFound();
	}
	
	public int getNumberOfEvaluations() {
		return evaluator.getNumberOfEvaluations();
	}

	@Override
	public float[] getBestPosition() {
		return evaluator.convertPositionToFloat(position);
	}

	@Override
	public float getBestFitness() {
		return evaluator.evaluateFloat(getBestPosition());
	}
	
	@Override
	public float[][] getBestPositions() {
		return evaluator.getBestPositions();
	}

	@Override
	public float[] getBestFitnesses() {
		return evaluator.getBestFitnesses();
	}

}

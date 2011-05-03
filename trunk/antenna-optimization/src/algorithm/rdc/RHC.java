package algorithm.rdc;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;
import algorithm.Algorithm;

public class RHC implements Algorithm {

	private int[] position;
	private final FitnessEvaluator evaluator;

	public RHC(int benchmarkId, int maxNumberOfEvaluations) {

		// Initialize evaluator.
		evaluator = new FitnessEvaluatorImpl(benchmarkId, maxNumberOfEvaluations, true);
		evaluator.load();

		// Pick a random position.
		position = evaluator.pickRandomPosition();
		evaluator.print(position);

		for (int numberOfEvaluations = 1; !evaluator.shouldTerminate(); numberOfEvaluations++) {
			int[] newPosition = evaluator.pickAnyNeighbor(position);

			// Update position.
			if (evaluator.evaluate(newPosition) >= evaluator.evaluate(position)) {
				position = newPosition;
			}
			
			// Stop if algorithm is stuck.
			if (numberOfEvaluations > 11000) {
				break;
			}
		}
		evaluator.print(position);
	}

	@Override
	public boolean isOptimumFound() {
		return evaluator.isOptimumFound();
	}
	
	@Override
	public int getNumberOfEvaluations() {
		return evaluator.getNumberOfEvaluations();
	}

	@Override
	public float[] getBestPosition() {
		return evaluator.convertPositionToFloat(position);
	}

	@Override
	public float getBestFitness() {
		return evaluator.evaluate(getBestPosition());
	}
	
	@Override
	public float[][] getBestPositions() {
		return evaluator.getBestPositions();
	}

	@Override
	public float[] getBestFitnesses() {
		return evaluator.evaluate(getBestPositions());
	}
}

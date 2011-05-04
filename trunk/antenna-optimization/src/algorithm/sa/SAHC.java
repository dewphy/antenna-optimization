package algorithm.sa;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;
import algorithm.Algorithm;

public class SAHC implements Algorithm {

	private int[] position;

	private float t = 10;
	private float cooling = 0.90f;

	private final FitnessEvaluator evaluator;

	public SAHC(int benchmarkId, int maxNumberOfEvaluations) {
		System.out.println("Simulated Annealing: ");
		
		// Initialize evaluator.
		evaluator = new FitnessEvaluatorImpl(benchmarkId, maxNumberOfEvaluations, true);
		evaluator.load();

		// Pick a random position.
		position = evaluator.pickRandomPosition();
		evaluator.print(position);

		for (int numberOfEvaluations = 0; !evaluator.shouldTerminate(); numberOfEvaluations++) { 
			int[] newPosition = evaluator.pickAnyNeighbor(position);
			
			float currentFitness = evaluator.evaluate(position);
			float newFitness = evaluator.evaluate(newPosition);
			
			if (newFitness >= currentFitness) {
				position = newPosition;
			} else if (Math.exp(-(currentFitness-newFitness)/t) > Math.random()) {
				position = newPosition;
			}
			
			//  cooling schedule.
			t *= cooling;
			
			// Stop if algorithm is stuck.
			if (numberOfEvaluations > 11000) {
				break;
			}
		}
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
		return evaluator.evaluateFloat(getBestPosition());
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

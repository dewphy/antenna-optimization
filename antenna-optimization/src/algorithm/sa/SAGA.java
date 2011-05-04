package algorithm.sa;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;
import algorithm.Algorithm;

public class SAGA implements Algorithm {

	private int[] position;

	private float t = 10;
	private float cooling = 0.90f;
	
	private final float pMutation = 0.15f;

	private final FitnessEvaluator evaluator;

	public SAGA(int benchmarkId, int maxNumberOfEvaluations) {
		System.out.println("Simulated Annealing: ");
		
		// Initialize evaluator.
		evaluator = new FitnessEvaluatorImpl(benchmarkId, maxNumberOfEvaluations, true);
		evaluator.load();

		// Pick a random position.
		position = evaluator.pickRandomPosition();

		for (int numberOfEvaluations = 0; !evaluator.shouldTerminate(); numberOfEvaluations++) { 
			int[] newPosition = mutate();
			
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
		evaluator.print(position);
	}

	private int[] mutate() {
		int[] newPosition = new int[evaluator.getPositionLength()];
		for (int k = 0; k < newPosition.length; k++) {
			newPosition[k] = mutateInt(position[k], evaluator.getMaxIndexes()[k]);
		}
		return newPosition;
	}
	
	public int mutateInt(int integer, int maxInteger) {
		int newInteger;
		do {
			newInteger = integer;
			for (int m = 1; m <= maxInteger; m <<= 1) {
				if (Math.random() < pMutation) {
					newInteger ^= m;
				}
			}
		} while(newInteger >= maxInteger || Math.abs(newInteger-integer) > maxInteger);
		return newInteger;
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
		return evaluator.getBestFitnesses();
	}
}

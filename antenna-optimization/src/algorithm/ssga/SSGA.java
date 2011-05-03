package algorithm.ssga;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;
import algorithm.Algorithm;

public class SSGA implements Algorithm{

	private int generation;

	private final float pMutation = 0.15f;
	private final float pCrossover = 0.80f;

	private final int nPopulation = 40;
	private final int nParents = 2;
	private final int nOffsprings = 2;
	private final int nAlleles;

	private final int[][] population;
	private final int[][] parents;
	private final int[][] offsprings;

	private final FitnessEvaluator evaluator;

	/*
	 * Run a Simple Genetic Algorithm (SGA).
	 */
	public SSGA(int benchmarkId, int maxNumberOfEvaluations) {

		// Initialize evaluator.
		evaluator = new FitnessEvaluatorImpl(benchmarkId, maxNumberOfEvaluations, true);
		evaluator.load();

		// Initialize population, parents, and offsprings.
		nAlleles = evaluator.getPositionLength();
		population = new int[nPopulation][nAlleles];
		parents = new int[nParents][nAlleles];
		offsprings = new int[nOffsprings][nAlleles];
		initPopulation();
		evaluator.evaluate(population);

		for (generation = 0; !evaluator.shouldTerminate(); generation++) {
			//			printStat();

			selectParents();
			recombinateWithCrossover();
			mutate();
			selectSurvivals();
			if (generation >= 11000) {
				break;
			}
		}
		printStat();
	}

	/*
	 * Initialize the population with random individuals.
	 */
	private void initPopulation() {
		for (int i = 0; i < nPopulation; i++) {
			population[i] = evaluator.pickRandomPosition();
		}
	}

	/*
	 * Select parents using fitness based selection.
	 */
	private void selectParents() {

		float[] cumFitness = computeCumulativeFitness();
		float sumFitness = cumFitness[nPopulation-1];

		// Roulette Wheel.
		for (int i = 0; i < nParents; i++) {
			float r = (float) (Math.random() * sumFitness);
			int j;
			for (j = 0; r > cumFitness[j] && j < nPopulation -1; j++);
			for (int k = 0; k < nAlleles; k++) {
				parents[i][k] = population[j][k];
			}
		}
	}

	/*
	 * Compute cumulative fitness.
	 */
	private float[] computeCumulativeFitness() {
		float[] cumFitness = new float[nPopulation];
		cumFitness[0] = evaluator.evaluate(population[0]);
		for (int i = 1; i < nPopulation; i++) {
			cumFitness[i] = cumFitness[i-1] + evaluator.evaluate(population[i]);
		}
		return cumFitness;
	}

	/*
	 * Recombinate parents with 1-point crossover.
	 */
	private void recombinateWithCrossover() {
		for (int i = 0; i < nParents; i+=2) {
			if (Math.random() < pCrossover) {
				int crossoverPoint = 1 + (int)(Math.random()*(nAlleles-1));
				for (int k = 0; k < crossoverPoint; k++) {
					offsprings[i][k] = parents[i][k];
					offsprings[i+1][k] = parents[i+1][k];
				}
				for (int k = crossoverPoint; k < nAlleles; k++) {
					offsprings[i][k] = parents[i+1][k];
					offsprings[i+1][k] = parents[i][k];
				}
			} else {
				for (int k = 0; k < nAlleles; k++) {
					offsprings[i][k] = parents[i][k];
					offsprings[i+1][k] = parents[i+1][k];
				}
			}
		}
	}

	/*
	 * Mutate the offsprings with 1-point arithmetic mutation.
	 */
	private void mutate() {
		for (int i = 0; i < nOffsprings; i++) {
			for (int k = 0; k < nAlleles; k++) {
				offsprings[i][k] = mutateInt(offsprings[i][k], evaluator.getMaxIndexes()[k]);
			}
		}
	}

	public int mutateInt(int integer, int maxInteger) {
		int newInteger;
		float diff;
		do {
			newInteger = integer;
			for (int m = 1; m <= maxInteger; m <<= 1) {
				if (Math.random() < pMutation) {
					newInteger ^= m;
				}
			}
		} while(newInteger >= maxInteger || Math.abs(newInteger-integer) > 0.1*maxInteger);
		return newInteger;
	}

	/*
	 * Selects the population for the next generation by adding the best offspring.
	 */
	private void selectSurvivals() {

		// Tournament selection.
		int bestOffspringIndex = evaluator.evaluate(offsprings[0]) > evaluator.evaluate(offsprings[1]) ? 0 : 1;

		// Replace a random parent.
		int i = (int) (Math.random()*nPopulation);
		for (int k = 0; k < nAlleles; k++) {
			population[i][k] = offsprings[bestOffspringIndex][k];
		}
		
		// Elitism.
		population[0] = evaluator.getBestIntPosition();
	}

	/*
	 * Prints statistic of current generation.
	 */
	public void printStat() {
		System.out.println("Evaluation #: " + evaluator.getNumberOfEvaluations());
		System.out.println("   Generation #" + String.valueOf(generation));
		printElite();
		System.out.println("   Best fitness: " + evaluator.getBestFitness());
		printMeanFitness();
	}

	/*
	 * Prints elite fitness and genotype of whole population.
	 */
	public void printElite() {
		int[] elite = population[0];

		for (int i = 1; i < nPopulation; i++) {
			if (evaluator.evaluate(population[i]) > evaluator.evaluate(elite)) {
				elite = population[i];
			}
		}
		System.out.println("   Elite fitness: " + evaluator.evaluate(elite));
	}

	/*
	 * Prints mean fitness of current generation.
	 */
	public void printMeanFitness() {
		float sumFitness = 0f;
		for (int i = 0; i< nPopulation; i++) {
			sumFitness += evaluator.evaluate(population[i]);
		}
		System.out.println("   Mean fitness: " + sumFitness/nPopulation);
	}

	@Override
	public float getBestFitness() {
		return evaluator.getBestFitness();
	}

	@Override
	public float[] getBestPosition() {
		return evaluator.getBestPosition();
	}

	@Override
	public float[] getBestFitnesses() {
		return evaluator.getBestFitnesses();
	}

	@Override
	public float[][] getBestPositions() {
		return evaluator.getBestPositions();
	}

	@Override
	public boolean isOptimumFound() {
		return evaluator.isOptimumFound();
	}

	@Override
	public int getNumberOfEvaluations() {
		return evaluator.getNumberOfEvaluations();
	}
}

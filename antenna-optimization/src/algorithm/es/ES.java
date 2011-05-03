package algorithm.es;

import java.util.HashSet;
import java.util.Set;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;
import algorithm.Algorithm;

public class ES implements Algorithm {

	private int generation;

	private final float pCrossover = 0.50f;

	private final int nPopulation = 40;
	private final int nParents = 40;
	private final int nOffsprings = 40;
	private final int nAlleles;

	private int[][] population;
	private final int[][] parents;
	private final int[][] offsprings;

	private final FitnessEvaluator evaluator;

	/*
	 * Run a Simple Genetic Algorithm (SGA).
	 */
	public ES(int benchmarkId, int maxNumberOfEvaluations) {

		// Initialize evaluator.
		evaluator = new FitnessEvaluatorImpl(benchmarkId, maxNumberOfEvaluations, true);
		evaluator.load();

		// Initialize population, parents, and offsprings.
		nAlleles = evaluator.getPositionLength() + 1;
		population = new int[nPopulation][nAlleles];
		parents = new int[nParents][nAlleles];
		offsprings = new int[nOffsprings][nAlleles];
		initPopulation();

		// Evaluate population.
		for (int i = 0; i < nPopulation; i++) {
			evaluate(population[i]);
		}

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
	
	private float evaluate(int[] individual) {
		int[] position = new int[evaluator.getPositionLength()];
		for (int k = 0; k < position.length; k++) {
			position[k] = individual[k];
		}
		return evaluator.evaluate(position);
	}

	/*
	 * Initialize the population with random individuals.
	 */
	private void initPopulation() {
		for (int i = 0; i < nPopulation; i++) {
			
			// Random position.
			int[] position = evaluator.pickRandomPosition();
			for (int k = 0; k < position.length; k++) {
				population[i][k] = position[k];
			}
			
			// Random mutation rate.
			int pMutation = (int)(90*Math.random());
			population[i][nAlleles-1] = pMutation;
		}
	}

	/*
	 * Select parents randomly.
	 */
	private void selectParents() {
		for (int i = 0; i < nParents; i++) {
			int j = (int)(Math.random() * nPopulation);
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
		cumFitness[0] = evaluate(population[0]);
		for (int i = 1; i < nPopulation; i++) {
			cumFitness[i] = cumFitness[i-1] + evaluate(population[i]);
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
			int pMutation = offsprings[i][nAlleles-1];
			for (int k = 0; k < nAlleles-1; k++) {
				offsprings[i][k] = mutateInt(offsprings[i][k], evaluator.getMaxIndexes()[k], ((float)pMutation)/100);
			}
			offsprings[i][nAlleles-1] = mutateInt(offsprings[i][nAlleles-1], 90, ((float)pMutation)/100);
		}
	}
	
	public int mutateInt(int integer, int maxInteger, float pMutation) {
		int newInteger;
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
	 * Selects the population for the next generation with a generational selection.
	 */
	private void selectSurvivalsGenerational() {
		for (int i = 0; i < nPopulation; i++) {
			for (int k = 0; k < nAlleles; k++) {
				population[i][k] = offsprings[i][k];
			}
		}
		
		// Elitism.
		int[] elitePosition = evaluator.getBestIntPosition();
		for (int k = 0; k < elitePosition.length; k++) {
			population[0][k] = elitePosition[k];
		}
		population[0][nAlleles - 1] = 15;
	}

	/*
	 * Selects population based on rank (lambda+mu).
	 */
	private void selectSurvivals() {
		Set<Integer> offspringSet = new HashSet<Integer>();
		Set<Integer> parentSet = new HashSet<Integer>();

		int[][] newPopulation = new int[nPopulation][nAlleles];
		for (int i = 0; i < nPopulation; i++) {

			int[] bestIndividual = null;
			float bestFitness = 0;
			int bestIndex = -1;
			boolean amongOffsprings = true;

			// Find best offspring.
			for (int j = 0; j < nOffsprings; j++) {
				if (!offspringSet.contains(j) && evaluate(offsprings[j]) > bestFitness) {
					bestIndividual = copyIndividual(offsprings[j]);
					bestFitness = evaluate(bestIndividual);
					bestIndex = j;
				}
			}
			for (int j = 0; j < nPopulation; j++) {
				if (!parentSet.contains(j) && evaluate(population[j]) > bestFitness) {
					bestIndividual = copyIndividual(population[j]);
					bestFitness = evaluate(bestIndividual);
					bestIndex = j;
					amongOffsprings = false;
				}
			}
			
			// Select best offspring.
			if (bestIndividual == null) {
				new RuntimeException("Error: Ranking failed!");
			}
			newPopulation[i] = bestIndividual;
			
			// Remove offspring.
			if (amongOffsprings) {
				offspringSet.add(bestIndex);
			} else {
				parentSet.add(bestIndex);
			}
			
		}
		
		population = newPopulation;
	}
	
	private int[] copyIndividual(int[] individual) {
		int[] newIndividual = new int[nAlleles];
		for (int k = 0; k < nAlleles; k++) {
			newIndividual[k] = individual[k];
		}
		return newIndividual;
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
			if (evaluate(population[i]) > evaluate(elite)) {
				elite = population[i];
			}
		}
		System.out.println("   Elite fitness: " + evaluate(elite));
	}
	
	/*
	 * Prints mean fitness of current generation.
	 */
	public void printMeanFitness() {
		float sumFitness = 0f;
		for (int i = 0; i< nPopulation; i++) {
			sumFitness += evaluate(population[i]);
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import benchmark.FitnessEvaluator;
import benchmark.FitnessEvaluatorImpl;

import algorithm.Algorithm;
import algorithm.aco.ACO;
import algorithm.es.ES;
import algorithm.gp.GP;
import algorithm.hc.GHC;
import algorithm.pso.PSO;
import algorithm.rdc.RHC;
import algorithm.sa.SAGA;
import algorithm.sa.SAHC;
import algorithm.sga.SGA;
import algorithm.ssga.SSGA;


public class Main {
	
	public static final String RESULT_DIR = "results/";
	
	public static void main(String[] args) throws IOException {
		System.out.println("BEGIN");
		
		int maxNumberOfEvaluations = 10000;
		int nRuns = 20;
		int nSuccesses = 0;
		int sumHitTime = 0;

//		for (int b = 1; b <= 2; b++) {
//			System.out.println("b: " + b);
		int b = 4;
			for (int i = 0; i < nRuns; i++) {
//				Algorithm sa = new ES(b, maxNumberOfEvaluations);
//				Algorithm sa = new PSO(b, maxNumberOfEvaluations, true, "Hybrid");
//				Algorithm sa = new ACO(b, maxNumberOfEvaluations, true, true, true);
				Algorithm sa = new GP(b, maxNumberOfEvaluations, true);

				FileWriter fstream = new FileWriter(RESULT_DIR + "B" + b + "//" + (i+1) + ".txt");
				BufferedWriter out = new BufferedWriter(fstream);
				
				float[] bestFitnesses = sa.getBestFitnesses();
				float[][] bestPosition = sa.getBestPositions();
				System.out.println("Size(bestPosition) = "+ bestPosition.length);
				System.out.println("Size(bestFitnesses) = "+ bestFitnesses.length);
				for (int j = 0; j < bestFitnesses.length; j++) {
					out.write(bestFitnesses[j] + " " + bestPosition[j][0] + " " + bestPosition[j][1] + "\n");
				}
				out.close();
				
				if (sa.isOptimumFound()) {
					
					System.out.println("Success: #Evaluations" + sa.getNumberOfEvaluations());
					nSuccesses++;
					sumHitTime += sa.getNumberOfEvaluations();
				} else {
					System.out.println("Failure: #Evaluations" + sa.getNumberOfEvaluations());
				}
			}
			System.out.println("\n-------------\n");
			System.out.println("RESULT SUMMARY");
			System.out.println("  #Runs: " + nRuns);
			System.out.println("  #Successes: " + nSuccesses);
			System.out.println("  Success rate: " + (100*nSuccesses)/nRuns + "%");
			System.out.println("  Sum Hit Time: " + sumHitTime);
			if (nSuccesses != 0) {
				System.out.println("  Mean Hit Time: " + sumHitTime/nSuccesses);
			}
//		}

		System.out.println("END");
	}
}

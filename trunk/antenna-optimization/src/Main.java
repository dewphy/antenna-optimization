import algorithm.hc.HC;


public class Main {
	
	public static void main(String[] args) {
		System.out.println("BEGIN");
		
		int benchmarkId = 1;
		int maxNumberOfEvaluations = 1000;
		int nRuns = 100;
		int nSuccesses = 0;
		int sumHitTime = 0;
		
		for (int i = 0; i < nRuns; i++) {
			HC hc = new HC(benchmarkId, maxNumberOfEvaluations);
			if (hc.isOptimumFound()) {
				nSuccesses++;
				sumHitTime += hc.getNumberOfEvaluations();
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

		System.out.println("END");
	}
}
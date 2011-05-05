package algorithm.aco;
import java.util.*;
import java.lang.*;

import benchmark.Constants;
import benchmark.FitnessEvaluatorImpl;

import algorithm.Algorithm;


public class ACO implements Algorithm {
	
	private Ant[] colony;
	
	private FitnessEvaluatorImpl fitnessValues;
	private Ant bestAnt;
	private Ant[] eliteAnts=new Ant[5];
	private float[][] pheromoneTrails;
	
	
	private int benchmarkNumber;
	
	private List<Ant> bestAnts=new ArrayList<Ant>();
	
	private boolean localSearchOn;
	private boolean recencyMemoryOn;
	private boolean[][] memory;
	
	private int maxEvaluations;
	
	final int NUMBER_OF_ANTS=30;
	final float EVAP_RATE=0.9f;
	final float THRESHOLD=0.1f;
	private float[] lowerBound;
	private float[] upperBound;
	


	public ACO(int benchNumber, int maxEvaluations, boolean discrete, boolean recencyUse, boolean localSearch){
		this.maxEvaluations=maxEvaluations;
		localSearchOn=localSearch;
		recencyMemoryOn=recencyUse;
		benchmarkNumber=benchNumber;
		
		

		fitnessValues=new FitnessEvaluatorImpl(benchmarkNumber,maxEvaluations,discrete);

		lowerBound=fitnessValues.getLowerBound();
		upperBound=fitnessValues.getUpperBound();
		
		colony = new Ant[NUMBER_OF_ANTS];
		bestAnt=new Ant(benchmarkNumber);
		
		
		
		for (int i=0; i<bestAnt.length();i++){
			bestAnt.setSolution(0,i);
		}
		bestAnt.setCurrentFitness(0);
		
		for(int i=0; i<NUMBER_OF_ANTS; i++){
			colony[i]=new Ant(benchmarkNumber);
		}
		
		pheromoneTrails=new float[4][colony[0].length()];
		initPheromoneTrails();
		initSolutions();
		calculateFitness();
		updateBestAnt();
		initEliteAnts();
		
		
		 while (!this.isOptimumFound() && this.getNumberOfEvaluations()<maxEvaluations){
			
			 optimize();
			 
		}
		 System.out.println(this.getBestAnt().toString());
	}
	
	public Ant getBestAnt(){
		return bestAnt.copy();
	}
	
	public float[] getBestPosition(){
		return bestAnt.getArrayWithPhenValues();
	}
	
	public float getBestFitness(){
		return bestAnt.getCurrentFitness();
	}
	
	public float[] getBestFitnesses(){
//		float[] temp=new float[bestAnts.size()];
//		for (int i=0; i<bestAnts.size();i++){
//			temp[i]=bestAnts.get(i).getCurrentFitness();
//		}
//		return temp;
		return fitnessValues.getBestFitnesses();
	}
	public FitnessEvaluatorImpl getFitnessValues(){
		return fitnessValues;
	}
	public float[][] getBestPositions(){
//		float[][] temp=new float[bestAnts.size()][fitnessValues.getPositionLength()];
//		
//		for (int i=0; i<bestAnts.size();i++){
//			temp[i]=bestAnts.get(i).getArrayWithPhenValues();
//		}
//		return temp;
		return fitnessValues.getBestPositions();
	}
	
	public void initPheromoneTrails(){
		memory=new boolean[4][colony[0].length()];
		for (int k=0; k<4; k++){
			for (int j=0; j<pheromoneTrails[k].length; j++){
			
					pheromoneTrails[k][j]=0.25f;
					memory[k][j]=false;
						
			}
		}
		
		pheromoneTrails[0][0]=0.5f;
		pheromoneTrails[1][0]=0.5f;
		pheromoneTrails[2][0]=0f;
		pheromoneTrails[3][0]=0f;
	}
	
	public void initSolutions(){
		Random gen=new Random();
		for (int k=0; k<colony.length; k++){
			for (int l=0; l<colony[k].length(); l++){
				colony[k].setSolution(gen.nextInt(2),l);
				//System.out.print(colony[k].getSolution()[l]);
			}
			//System.out.print("\nSolution " + k+ ": ");
		}
	}
	
	public void updatePheromoneTrails(){
	
	float fitSum=0;
	
	for (int k=0; k<colony.length; k++){
		fitSum=colony[k].getCurrentFitness()+fitSum;
	}	
	
	for (int k=0; k<colony.length; k++){
		
		for (int l=0; l<4; l++){
			for (int j=0; j<colony[k].length(); j++){
				
					pheromoneTrails[l][j]=pheromoneTrails[l][j]*(1-EVAP_RATE);
				
					if (pheromoneTrails[l][j]<0.0005){
					pheromoneTrails[l][j]=0.0005f;
					
					}
				}
			}
			
		if (fitSum!=0){
			
			for (int i=0; i<colony[k].length();i++){
					int a=0;
					
					if (i!=0) //to account for the transition from initial state 
					{
					 a=colony[k].getSolution()[i-1];
					}
					
					int b=colony[k].getSolution()[i];
					
					int trans=(int) (a+b*Math.pow(2,b));
					
					pheromoneTrails[trans][i]=pheromoneTrails[trans][i]+colony[k].getCurrentFitness()/fitSum;
					memory[trans][i]=true;
			} 
			
		}
	}
//		if (fitSum!=0){
//			for (int k=0; k<eliteAnts.length; k++){
//
//			
//				for (int i=0; i<eliteAnts[k].length();i++){
//					int a=0;
//					
//					if (i!=0) //to account for the transition from initial state 
//					{
//					 a=eliteAnts[k].getSolution()[i-1];
//					}
//					
//					int b=eliteAnts[k].getSolution()[i];
//					
//					int trans=(int) (a+b*Math.pow(2,b));
//					
//					pheromoneTrails[trans][i]=pheromoneTrails[trans][i]+eliteAnts[k].getCurrentFitness()/fitSum;
//					
//				} 
//			
//			}
//		}
}

	
	
	public void generateSolutions(){
	
		for(int i=0; i<NUMBER_OF_ANTS; i++){
		
		for (int j=0; j<colony[i].length();j++){
			float accumSum=0;
			
			
//			calculateProbability
			Random generator=new Random();
			float probability=generator.nextFloat();
			float recencyProbability=generator.nextFloat();
			
			int previous=0;
			if (j!=0){previous=colony[i].getSolution()[j-1];}
			
			int count=0;
			int index=0;
			
			if (recencyProbability<= Constants.RECENCY_SELECTION_RATE && recencyMemoryOn){
				
				for (int k=0; k<2; k++){
					index=(int)(previous+k*Math.pow(2,k));
					if (!memory[index][j]) {colony[i].setSolution(index%2,j); count++; }
				}
			}
			
			if (count!=1){
			//float lower=0;
			//float upper=pheromoneTrails[0][j]/accumSum;
			//boolean notFound=true;
//			if (probabilityBest<=THRESHOLD){
//				float max=0;
//				int maxIndex=0;
//				boolean flag=false;
//				for (int k=0; k<2; k++){
//					index=(int)(previous+k*Math.pow(2,k));
//					if (pheromoneTrails[index][j]>max){
//						max=pheromoneTrails[index][j];
//						maxIndex=index;
//						flag=true;
//					}else if (pheromoneTrails[index][j]==max){
//						int choice=generator.nextInt(2);
//						maxIndex=(int) (previous+choice*Math.pow(2,choice));
//					}
//				}
//				
//					colony[i].setSolution(maxIndex%2,j);
//				
//			}
			
			for (int k=0; k<2; k++){
				index=(int)(previous+k*Math.pow(2,k));
				accumSum+=pheromoneTrails[index][j];
			}
			if (probability<=pheromoneTrails[index][j]/accumSum){
					
					colony[i].setSolution(index%2,j);
					//System.out.println("Sol. component: "+ j+" Ant number: "+ i+ " Solution: "+ colony[i].getSolution()[j]);
			}
			else {
				colony[i].setSolution((index+1)%2,j);
					//System.out.println("Random #: "+probability +"  Lower: "+ lower+" Upper: "+ upper);
			}
			}
			}
//			Random generator=new Random();
//			float probability=generator.nextFloat();
//			float recencyProbability=generator.nextFloat();
//			int count=0;
//			int index=0;
//			
//			if (recencyProbability<= Constants.RECENCY_SELECTION_RATE && recencyMemoryOn){
//				
//				for (int k=0; k<4; k++){
//					if (!memory[k][j]) {colony[i].setSolution(k%2,j); count++; }
//				}
//			}
//			
//			if (count!=1){
//			float lower=0;
//			float upper=pheromoneTrails[0][j]/accumSum;
//			boolean notFound=true;
//			int k=0;
//			while (k<4 && notFound){
//				if (probability>=lower && probability<=upper){
//					int t=k%2;
//					colony[i].setSolution(t,j);
//					notFound=false;
//					//System.out.println("Sol. component: "+ j+" Ant number: "+ i+ " Solution: "+ colony[i].getSolution()[j]);
//				}
//				else {
//					lower=upper;
//					upper=upper+pheromoneTrails[k+1][j]/accumSum;
//					
//					if (Math.abs(upper-1)<=0.00001f || upper>1) {upper=1;
//					}
//					k++;
//					//System.out.println("Random #: "+probability +"  Lower: "+ lower+" Upper: "+ upper);
//				}
		}
		//System.out.print(colony[i].getSolution()[j]);
	}
		//System.out.print("\nSolution " + i+ ": ");
	
	
	

		
	
	
	public void optimize(){
		
		//System.out.println("Optimizing");
		updatePheromoneTrails();
		//System.out.println("Trails Updated");
		generateSolutions();
		//System.out.println("Solutions generated");
		//printAntColony();
		calculateFitness();
		//System.out.println("Fitness Updated\n");
		if (localSearchOn){
			doLocalSearch();
			//System.out.println("Local search completed\n");
		}
		updateBestAnt();
		//System.out.println("Best Ant: "+ bestAnt.toString());
		updateEliteAnts();
	}

	
	
	public void calculateFitness(){
		//for(int i=0; i<NUMBER_OF_ANTS; i++){
			//System.out.println(colony[i].toString());
		int i=0;
		while (i<NUMBER_OF_ANTS && this.getNumberOfEvaluations()<maxEvaluations){
				float[] solutions=colony[i].getArrayWithPhenValues();
				
				boolean satisfied=true;
				int j=0;
				while (satisfied && j<solutions.length){
					if (solutions[j]>upperBound[j] || solutions[j]<lowerBound[j]){
						satisfied=false;
						colony[i].setCurrentFitness(0f);
					}
					j++;
				}
				if (satisfied){
					
					colony[i].setCurrentFitness(fitnessValues.evaluateFloat(solutions));
				}
			
			i++;
		}
		
		
	}
	
	public void doLocalSearch(){
		int i=0;
		
		Random generator=new Random();
		float r=0;
		
		while (i<NUMBER_OF_ANTS && this.getNumberOfEvaluations()<maxEvaluations){
			//System.out.println("Local Search for Ant\n" + i);
			float fitness=colony[i].getCurrentFitness();
			int pos=-1; 
			float improvement=0;
			
			if (fitness!=0){
				float[] solutions=colony[i].getArrayWithPhenValues();
				
				int k=0;
				while (k<solutions.length && this.getNumberOfEvaluations()<maxEvaluations){
					r=(float) generator.nextGaussian();
					int std=2;
					if (k==0){std=2;}
					solutions[k]=solutions[k]+std*r;//(float) (solutions[k]+Math.pow(-1,j)*change[k]);
					float newFit=0;
					if (solutions[k]>upperBound[k] || solutions[k]<lowerBound[k]){
						newFit=0f;
						//System.out.println("OutOfBounds: " + newFit);
					}
					else {
						newFit=fitnessValues.evaluateFloat(solutions);
					}
					
					if (newFit>fitness){
						fitness=newFit;
						pos=k;
						improvement=std*r;
						//power=j;
						//System.out.println("Better Fitness");
					}
					solutions[k]=(float) (solutions[k]-std*r); //-Math.pow(-1,j)*change[k]);
					k++;
				}
				//}
				if (pos>-1){
				solutions[pos]=(float) (solutions[pos]+improvement); //Math.pow(-1,power)*change[pos]);
				//System.out.println("Parameter "+ pos +" : "+ solutions[pos]);
				colony[i].setCurrentFitness(fitness);
				colony[i].setSolutions(solutions[pos],pos);
				//System.out.println("Parameter "+ pos +" : "+ colony[i].getArrayWithPhenValues()[pos]);
				}
			}
			i++;
		}
	}
	
	public void printAntColony(){
		for(int i=0; i<NUMBER_OF_ANTS; i++){
			System.out.println(colony[i].toString());
		}
	}
	
	public void updateBestAnt(){
		for(int i=0; i<NUMBER_OF_ANTS; i++){
			if (colony[i].getCurrentFitness()>bestAnt.getCurrentFitness()){
				bestAnt=colony[i].copy();
			}
		}
		bestAnts.add(bestAnt);
	}
	
	public void initEliteAnts(){
		Arrays.sort(colony);
		for(int i=0; i<eliteAnts.length; i++){
			eliteAnts[i]=colony[colony.length-i-1].copy();	
		}
	}
	
	public Ant[] getBestAntsInGeneration(){
		Ant[] copy=new Ant[eliteAnts.length];
		Arrays.sort(colony);
		for(int i=0; i<eliteAnts.length; i++){
			copy[i]=colony[colony.length-i-1].copy();	
		}
		return copy;
	}
	
	public void updateEliteAnts(){
		
		List<Ant> list = new ArrayList<Ant>(Arrays.asList(getBestAntsInGeneration()));
	
		list.addAll(Arrays.asList(eliteAnts));
		Collections.sort(list);
		for (int i=0; i<eliteAnts.length; i++){
			eliteAnts[i]=list.get(i);
		}
		
		
	}
	
	public int getNumberOfEvaluations(){
		return fitnessValues.getNumberOfEvaluations();
	}
	
	public Ant findBestAntInGeneration(){
		Ant temp=new Ant(benchmarkNumber);
		for(int i=0; i<NUMBER_OF_ANTS; i++){
			if (colony[i].getCurrentFitness()>temp.getCurrentFitness()){
				temp=colony[i].copy();
			}
		}
		return temp;
	}

	@Override
	public boolean isOptimumFound() {
		return fitnessValues.isOptimumFound();
		
	}
}

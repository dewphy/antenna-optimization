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
	private float[][] pheromoneTrails;
	
	
	private int benchmarkNumber;
	
	private List<Ant> bestAnts=new ArrayList<Ant>();
	
	private boolean localSearchOn;
	private boolean recencyMemoryOn;
	private boolean[][] memory;
	
	private int maxEvaluations;
	
	final int NUMBER_OF_ANTS=30;
	final float EVAP_RATE=0.9f;
	
	private float[] lowerBound;
	private float[] upperBound;
	


	public ACO(int benchNumber, int maxEvaluations, boolean discrete, boolean recencyUse, boolean localSearch){
		this.maxEvaluations=maxEvaluations;
		localSearchOn=localSearch;
		recencyMemoryOn=recencyUse;
		benchmarkNumber=benchNumber;
		
		

		fitnessValues=new FitnessEvaluatorImpl(benchmarkNumber, discrete);
		
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
		float[] temp=new float[bestAnts.size()];
		for (int i=0; i<bestAnts.size();i++){
			temp[i]=bestAnts.get(i).getCurrentFitness();
		}
		return temp;
	}
	public FitnessEvaluatorImpl getFitnessValues(){
		return fitnessValues;
	}
	public float[][] getBestPositions(){
		float[][] temp=new float[bestAnts.size()][fitnessValues.getPositionLength()];
		
		for (int i=0; i<bestAnts.size();i++){
			temp[i]=bestAnts.get(i).getArrayWithPhenValues();
		}
		return temp;
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
				colony[k].setSolution(Math.round(gen.nextFloat()),l);
				//System.out.print(colony[k].getSolution()[l]);
			}
			//System.out.print("\nSolution " + k+ ": ");
		}
	}
	
	public void updatePheromoneTrails(){
	
	float fitSum=0;
	
	for (int k=0; k<colony.length; k++){
		fitSum=colony[k].getCurrentFitness()+fitSum;
		
		for (int l=0; l<4; l++){
			for (int j=0; j<colony[k].length(); j++){
				
					pheromoneTrails[l][j]=pheromoneTrails[l][j]*(1-EVAP_RATE);
				
					if (pheromoneTrails[l][j]<0.005){
					pheromoneTrails[l][j]=0.005f;
					
				}
			}
		}
			
		if (fitSum==0){}
		else{
			for (int i=0; i<colony[k].length();i++){
					int a=0;
					
					if (i==0) {a=0;} //to account for the transition from initial state 
					else{
					 a=colony[k].getSolution()[i-1];
					}
					
					int b=colony[k].getSolution()[i];
					
					int trans=(int) (a+Math.pow(2,b));
					pheromoneTrails[trans][i]=pheromoneTrails[trans][i]+colony[k].getCurrentFitness()/fitSum;
					memory[trans][i]=true;
				}
			
			}
		}
	}
	
	public void generateSolutions(){
	
		for(int i=0; i<NUMBER_OF_ANTS; i++){
		
		for (int j=0; j<colony[i].length();j++){
			float accumSum=0;
			
			for (int k=0; k<4;k++){
				accumSum=accumSum+pheromoneTrails[k][j];
			}
			
//			calculateProbability
			
			Random generator=new Random();
			float probability=generator.nextFloat();
			float recencyProbability=generator.nextFloat();
			int count=0;
			
			if (recencyProbability<= Constants.RECENCY_SELECTION_RATE && recencyMemoryOn){
				
				for (int k=0; k<4; k++){
					if (!memory[k][j]) {colony[i].setSolution(k%2,j); count++; }
				}
			}
			
			if (count!=1){
			float lower=0;
			float upper=pheromoneTrails[0][j]/accumSum;
			boolean notFound=true;
			int k=0;
			while (k<4 && notFound){
				if (probability>=lower && probability<=upper){
					int t=k%2;
					colony[i].setSolution(t,j);
					notFound=false;
					//System.out.println("Sol. component: "+ j+" Ant number: "+ i+ " Solution: "+ colony[i].getSolution()[j]);
				}
				else {
					lower=upper;
					upper=upper+pheromoneTrails[k+1][j]/accumSum;
					
					if (Math.abs(upper-1)<=0.00001f || upper>1) {upper=1;
					}
					k++;
					//System.out.println("Random #: "+probability +"  Lower: "+ lower+" Upper: "+ upper);
				}
		}
		//System.out.print(colony[i].getSolution()[j]);
	}
		//System.out.print("\nSolution " + i+ ": ");
	}
	}
	}
	

		
	
	
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
					colony[i].setCurrentFitness(fitnessValues.evaluate(solutions));
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
					int std=1;
					if (k==0){std=20;}
					solutions[k]=solutions[k]+std*r;//(float) (solutions[k]+Math.pow(-1,j)*change[k]);
					float newFit=0;
					if (solutions[k]>upperBound[k] || solutions[k]<lowerBound[k]){
						newFit=0f;
						//System.out.println("OutOfBounds: " + newFit);
					}
					else {
						newFit=fitnessValues.evaluate(solutions);
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

package algorithm.pso;
import java.util.ArrayList;

import java.util.List;

import java.util.Random;

import benchmark.FitnessEvaluatorImpl;

import algorithm.Algorithm;


public class PSO implements Algorithm {
	
	final int NUMBER_OF_PARTICLES=30;
	//final int NUMBER_OF_GENES=2;
	
	final float C1=1.494f;
	final float C2=1.494f;
	final float WEIGHT=0.73f;
	
	private Particle[] swarm;
	private FitnessEvaluatorImpl fitnessValues;
	private Particle bestParticleEver;
	private List<Particle> bestParticles=new ArrayList<Particle>();
	
	float[] dimension;
	String neighborhoodType;
	private int maxEvaluations;
	
	
	
	public PSO(int benchmarkNumber, int maxEvaluations, boolean discrete, String neighborhoodType){
		
		this.maxEvaluations=maxEvaluations;
		this.neighborhoodType=neighborhoodType;
		fitnessValues=new FitnessEvaluatorImpl(benchmarkNumber,discrete);
		//fitnessValues.load();
		//fitnessValues.print();
		swarm = new Particle[NUMBER_OF_PARTICLES];
		
		bestParticleEver=new Particle(benchmarkNumber);
		dimension=new float[bestParticleEver.getNumberOfGenes()];
		
		for(int i=0; i<NUMBER_OF_PARTICLES; i++){
			swarm[i]=new Particle(benchmarkNumber);
		//System.out.println( "Before Theta: "+ swarm[i].getPosition()[0] + "Length: " + swarm[i].getPosition()[1]);
			
			swarm[i].setCurrentFitness(fitnessValues.evaluate(swarm[i].getPosition()));
			swarm[i].setBestFitness(swarm[i].getCurrentFitness());
			
			
			//System.out.println("After: " + swarm[i].toString());
			if (swarm[i].getCurrentFitness()>bestParticleEver.getCurrentFitness()){
				bestParticleEver=swarm[i].copy();
				//System.out.println(bestParticleEver.toString());
				
			}
			
		}
		bestParticles.add(bestParticleEver);
		findDimension();
		
		while (!this.getFitnessValues().isOptimumFound() && !this.maxEvaluationsReached()){
			
			this.updateParticleSwarm();
	
		}
	}
	
	public float[] getBestPosition(){
		return bestParticleEver.getPosition();
	}
	
	public float getBestFitness(){
		return bestParticleEver.getCurrentFitness();
	}
	
	public float[] getBestFitnesses(){
		float[] temp=new float[bestParticles.size()];
		for (int i=0; i<bestParticles.size();i++){
			temp[i]=bestParticles.get(i).getCurrentFitness();
		}
		return temp;
	}
	
	public float[][] getBestPositions(){
		float[][] temp=new float[bestParticles.size()][fitnessValues.getPositionLength()];
		
		for (int i=0; i<bestParticles.size();i++){
			temp[i]=bestParticles.get(i).getPosition();
		}
		return temp;
	}
	
	public Particle getBestParticleEver(){
		return bestParticleEver;
	}
	
	public int getNumberOfEvaluations(){
		int k=fitnessValues.getNumberOfEvaluations();
		return k;
	}
	
	public FitnessEvaluatorImpl getFitnessValues(){
		return fitnessValues;
	}
	
	
	public void updateParticleSwarm(){
		int i=0;
		while (this.getNumberOfEvaluations()<maxEvaluations && i<NUMBER_OF_PARTICLES){
			
			float[] maxVelocity=new float[dimension.length];
			
			for (int k=0; k<dimension.length; k++){
				maxVelocity[k]=0.2f*dimension[k];
			}
						
			Random generator=new Random();
			float r1=generator.nextFloat();
			float r2=generator.nextFloat();
			
			float[] localBest=swarm[i].getPosition();
			
			Particle temp=swarm[i].copy();
			
			if (neighborhoodType.equals("Star") || neighborhoodType.equals("Hybrid")){
			//Star Topology
			swarm[i].updateParticle(WEIGHT, C1, C2, r1, r2, bestParticleEver.getParticleBest(), maxVelocity);
			updateParticleFitness(swarm[i]);
			}
			
			//Circle Topology with k=2 nearest neighbors
			if (neighborhoodType.equals("Circle")){
				localBest=findNeighborhoodBestPosition(swarm[i]);//Find the best position among the three particles
				swarm[i].updateParticle(WEIGHT, C1, C2, r1, r2, localBest, maxVelocity);
				updateParticleFitness(swarm[i]);
			}
			
			if (neighborhoodType.equals("Hybrid")){
			localBest=findNeighborhoodBestPosition(temp);//Find the best position among the three particles
			
			temp.updateParticle(WEIGHT, C1, C2, r1, r2, localBest, maxVelocity);
			updateParticleFitness(temp);
			
			//Compare the results of two updates; pick best
			
			if (temp.getCurrentFitness()>swarm[i].getCurrentFitness()){
				swarm[i]=temp;
				//System.out.println("Local Neighborhood - Better");
			} //else System.out.println("Global Neighborhood - Better");
			}
			//System.out.println("Iteration: "+ i + " Evaluation #: "+this.getNumberOfEvaluations());
			updateBestParticleEver(swarm[i]);
			i++;
			
		}
		bestParticles.add(bestParticleEver);
		findDimension();
		
	}
	
	public float[] findNeighborhoodBestPosition(Particle part){
		
		float distance=0;
		float distance1=0;
		
		for (int k=0; k<dimension.length; k++){
			distance1=distance1+(float) (Math.pow(dimension[k],2));
		}
		float distance2=distance1;
		
		int nearestNghb=0;
		int nearestNghb2=0;
		
		float[] position=new float[2];
		position=part.getParticleBest();
		
		
			for(int j=0; j<NUMBER_OF_PARTICLES; j++){
				for(int k=0; k<dimension.length; k++){
					distance=distance + (float) (Math.pow((part.getPosition()[k]-swarm[j].getPosition()[k]),2));
				}
				//System.out.println("particle # 1: "+ i +" particle # 2: "+j +"dimension: "+ dim);
				if (distance<distance2){
					if (distance<distance1){
						distance1=distance;
						nearestNghb=j;
					}
					else {
						distance2=distance;
						nearestNghb2=j;
					}
				}
			}
			
			if (swarm[nearestNghb].getBestFitness()>swarm[nearestNghb2].getBestFitness()){
				if (swarm[nearestNghb].getBestFitness()>part.getBestFitness()){
					 position=swarm[nearestNghb].getParticleBest();
				}
				//else fit=part.getBestFitness();
			} 
			else if (part.getBestFitness()>swarm[nearestNghb2].getBestFitness()){
				 position=swarm[nearestNghb2].getParticleBest(); 
			}
			
			return position;
			
	}
	
	public void updateParticleFitness(Particle part){
		
		float currentFit=fitnessValues.evaluate(part.getPosition());
		part.setCurrentFitness(currentFit);
		
		
		if (currentFit>part.getBestFitness()){
			part.setBestFitness(currentFit);
			part.setParticleBest(part.getPosition());
			
		}
	}
	
	
	public void updateBestParticleEver(Particle part){
		
		if (part.getCurrentFitness()>bestParticleEver.getBestFitness()){
			bestParticleEver=part.copy();
		}
		//System.out.println(bestParticleEver.toString());
	}
	
	public void findDimension(){
		
		float largestDim=0;
		int particleNumber=0;
		int particleNumber2=0;
		for(int i=0; i<NUMBER_OF_PARTICLES; i++){
			for(int j=i+1; j<NUMBER_OF_PARTICLES; j++){
				float dim=0;
				
				for (int k=0; k<dimension.length; k++){
				dim=dim + (float) (Math.pow((swarm[i].getPosition()[k]-swarm[j].getPosition()[k]),2));
				}
				//System.out.println("particle # 1: "+ i +" particle # 2: "+j +"dimension: "+ dim);
				if (dim>largestDim){
					largestDim=dim;
					particleNumber=i;
					particleNumber2=j;
					
				}
			}
		}
		//System.out.println("particle # 1: "+ particleNumber +" particle # 2: "+particleNumber2 +"dimension: "+ largestDim);
		for (int k=0; k<dimension.length; k++){
//		dimension[0]=Math.abs(swarm[particleNumber].getPosition()[0]-swarm[particleNumber2].getPosition()[0]);
//		dimension[1]=Math.abs(swarm[particleNumber].getPosition()[1]-swarm[particleNumber2].getPosition()[1]);
		dimension[k]=Math.abs(swarm[particleNumber].getPosition()[k]-swarm[particleNumber2].getPosition()[k]);
		}
	}
	
	public boolean converged(){
		
		boolean converged=true;
		int i=0;
		
		while(i<NUMBER_OF_PARTICLES-1 && converged){
			if (swarm[i].equals(swarm[i+1])){
				converged=true;
				i++;
			} else converged=false;
		}
		return converged;
	}
	
	public boolean maxEvaluationsReached(){
		if (this.getNumberOfEvaluations()>=maxEvaluations)
			return true;
		else return false;
	}
	
	
	public void printSwarm(){
		for(int i=0; i<NUMBER_OF_PARTICLES; i++){
			System.out.println(swarm[i].toString());
		}
		
		
	}
	
	public void printBestParticle(){
		String particle=bestParticleEver.toString();
		System.out.println(particle);
	}

	@Override
	public boolean isOptimumFound() {
		return fitnessValues.isOptimumFound();
	}

}

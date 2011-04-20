import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ParticleSwarmOptimizer {
	
	final int NUMBER_OF_PARTICLES=30;
	//final int NUMBER_OF_GENES=2;
	final int MAX_EVALUATIONS=1000;
	
	
	final float C1=1.494f;
	final float C2=1.494f;
	final float WEIGHT=0.73f;
	
	private Particle[] swarm;
	private FitnessEvaluatorImpl fitnessValues;
	private Particle bestParticleEver;
	List<Particle> evaluatedParticles;
	
	//final float[] MAX_VELOCITY={0.2f* (UPPER_BOUND[0]-LOWER_BOUND[0]), 
		//	0.2f*(UPPER_BOUND[1]-LOWER_BOUND[1])};
	
	float[] dimension;
	String neighborhoodType;
	
	//float[] globalBest;
	//float globalBestFitness;
	
	
	
	ParticleSwarmOptimizer(int benchmarkNumber, String neighborhood){
		
		evaluatedParticles=new ArrayList<Particle>();
		neighborhoodType=neighborhood;
		fitnessValues=new FitnessEvaluatorImpl(benchmarkNumber);
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
			evaluatedParticles.add(swarm[i].copy());
			
			//System.out.println("After: " + swarm[i].toString());
			if (swarm[i].getCurrentFitness()>bestParticleEver.getCurrentFitness()){
				bestParticleEver=swarm[i].copy();
				//System.out.println(bestParticleEver.toString());
				
			}
			
		}
		
		
		findDimension();
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
	
	public List<Particle> getEvaluatedParticles(){
		return evaluatedParticles;
		
	}
	
	public void updateParticleSwarm(){
		int i=0;
		while (this.getNumberOfEvaluations()<MAX_EVALUATIONS && i<NUMBER_OF_PARTICLES){
			
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
		int index=searchForEvaluatedParticle(part);
		float currentFit;
		if (index==-1){
			currentFit=fitnessValues.evaluate(part.getPosition());
			part.setCurrentFitness(currentFit);
			evaluatedParticles.add(part.copy()); //Particle best not updated in this array
		} else {
			//System.out.println("To be evaluated: " + part.toString());
			//System.out.println("Already in array: " + evaluatedParticles.get(index).toString());
			currentFit=evaluatedParticles.get(index).getCurrentFitness();
			part.setCurrentFitness(currentFit);
		}
		
		if (currentFit>part.getBestFitness()){
			part.setBestFitness(currentFit);
			part.setParticleBest(part.getPosition());
			
		}
	}
	
	public int searchForEvaluatedParticle(Particle part){
		float[] acc=new float[part.getPosition().length];
		
		//To account for different sizes of the position vector in different benchmarks
		
		switch (part.getPosition().length){
		
		case 2: acc[0]=0.01f;
				acc[1]=0.0001f;
				break;
		case 6: 
		case 12: 
				for (int i=0; i<part.getPosition().length; i++){
				acc[i]=0.01f;
				}
				break;
		
		}
		int i=0;
		int particleIndex=-1;
		while (i<evaluatedParticles.size() && particleIndex==-1){
			int j=0;
			particleIndex=i;
			while (j<part.getPosition().length && particleIndex!=-1){
				if (Math.abs(part.getPosition()[j]-evaluatedParticles.get(i).getPosition()[j])>=acc[j]){
					particleIndex=-1;
				}else { j++;}
			}
			
			i++;
		}
		return particleIndex;
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
		if (this.getNumberOfEvaluations()>=MAX_EVALUATIONS)
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
//		String particle=
//		"Best Theta: " + bestParticleEver.getPosition()[0] +"\n"+
//		"Best Length: " + bestParticleEver.getPosition()[1]+"\n"+
//		"Best Fitness: "+ bestParticleEver.getBestFitness() + "\n";
		
		System.out.println(particle);
	}

}

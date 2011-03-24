
public class ParticleSwarmOptimizer {
	
	final int NUMBER_OF_PARTICLES=30;
	final int NUMBER_OF_GENES=2;
	
	
	final float C1=1.494f;
	final float C2=1.494f;
	final float WEIGHT=0.73f;
	
	Particle[] swarm;
	FitnessEvaluatorImpl fitnessValues;
	Particle bestParticleEver;
	
	//final float[] MAX_VELOCITY={0.2f* (UPPER_BOUND[0]-LOWER_BOUND[0]), 
		//	0.2f*(UPPER_BOUND[1]-LOWER_BOUND[1])};
	
	float[] dimension;
	
	//float[] globalBest;
	//float globalBestFitness;
	
	
	
	ParticleSwarmOptimizer(int benchmarkNumber){
		
		
		fitnessValues=new FitnessEvaluatorImpl(benchmarkNumber);
		fitnessValues.load();
		fitnessValues.print();
		swarm = new Particle[NUMBER_OF_PARTICLES];
		
		bestParticleEver=new Particle();
		dimension=new float[NUMBER_OF_GENES];
		
		for(int i=0; i<NUMBER_OF_PARTICLES; i++){
			swarm[i]=new Particle();
			swarm[i].setCurrentFitness(fitnessValues.evaluate(swarm[i].getPosition()[0], swarm[i].getPosition()[1]));
			swarm[i].setBestFitness(swarm[i].getCurrentFitness());
			
			//System.out.println(swarm[i].toString());
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
	
	
	public FitnessEvaluatorImpl getFitnessValues(){
		return fitnessValues;
	}
	
	public void updateParticleSwarm(){
		for(int i=0; i<NUMBER_OF_PARTICLES; i++){
			float[] maxVelocity={(float) (0.2*dimension[0]), (float) (0.2*dimension[1])};
			swarm[i].updateParticle(WEIGHT, C1, C2, bestParticleEver.getParticleBest(), maxVelocity);
			updateParticleFitness(swarm[i]);
			updateBestParticleEver(swarm[i]);
			
		}
		findDimension();
		
	}
	
	
	public void updateParticleFitness(Particle part){
		float currentFit=fitnessValues.evaluate(part.getPosition()[0], part.getPosition()[1]);
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
				float dim=(float) (Math.pow((swarm[i].getPosition()[0]-swarm[j].getPosition()[0]),2)+
				Math.pow((swarm[i].getPosition()[1]-swarm[j].getPosition()[1]),2));
				//System.out.println("particle # 1: "+ i +" particle # 2: "+j +"dimension: "+ dim);
				if (dim>largestDim){
					largestDim=dim;
					particleNumber=i;
					particleNumber2=j;
					
				}
			}
		}
		//System.out.println("particle # 1: "+ particleNumber +" particle # 2: "+particleNumber2 +"dimension: "+ largestDim);
		dimension[0]=Math.abs(swarm[particleNumber].getPosition()[0]-swarm[particleNumber2].getPosition()[0]);
		dimension[1]=Math.abs(swarm[particleNumber].getPosition()[1]-swarm[particleNumber2].getPosition()[1]);
		
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
	
	public int getNumberOfEvaluations(){
		return fitnessValues.getNumberOfEvaluations();
	}
	
	public void printSwarm(){
		for(int i=0; i<NUMBER_OF_PARTICLES; i++){
			System.out.println(swarm[i].toString());
		}
		
		
	}
	
	public void printBestParticle(){
		String particle=
		"Best Theta: " + bestParticleEver.getPosition()[0] +"\n"+
		"Best Length: " + bestParticleEver.getPosition()[1]+"\n"+
		"Best Fitness: "+ bestParticleEver.getBestFitness() + "\n";
		
		System.out.println(particle);
	}

}

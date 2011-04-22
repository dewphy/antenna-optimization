package algorithm.pso;

import java.util.*;

import benchmark.Constants;

public class Particle implements Comparable<Object> {
	
	
	final float error=0.0001f;
	
	private int SIZE=2;
	private float[] position;
	private float[] velocity;
	private float[] particleBestPosition;
	
	private float currentFitness;
	private float bestFitness;
	
	private float[] lowerBound;
	private float[] upperBound;
	
	private int benchmarkNumber;
	public Particle(int benchNumber){
		benchmarkNumber=benchNumber;
		switch (benchmarkNumber){
		case 1: lowerBound=Constants.LOWER_BOUND_1;
				upperBound=Constants.UPPER_BOUND_1;
				break;
		case 2: lowerBound=Constants.LOWER_BOUND_2;
				upperBound=Constants.UPPER_BOUND_2;
				
				break;
		
		case 3: lowerBound=Constants.LOWER_BOUND_3;
				upperBound=Constants.UPPER_BOUND_3;
				
				break;
		
		case 4: lowerBound=Constants.LOWER_BOUND_4;
				upperBound=Constants.UPPER_BOUND_4;
				break;
		
		case 5: SIZE=6;
				lowerBound=new float[SIZE];
				upperBound=new float[SIZE];
				for(int i=0; i<SIZE; i++){
					upperBound[i]=Constants.UPPER_BOUND_5;
					lowerBound[i]=Constants.LOWER_BOUND_5;
				}
					break;
		
			
		case 6: SIZE=12;
			lowerBound=new float[SIZE];
			upperBound=new float[SIZE];
			for(int i=0; i<SIZE; i++){
				upperBound[i]=Constants.UPPER_BOUND_5;
				lowerBound[i]=Constants.LOWER_BOUND_5;
			}
			break;
	
		
	}
		position=new float[SIZE];
		velocity=new float[SIZE];
		particleBestPosition=new float[SIZE];
		
		Random generator=new Random();
		for(int i=0; i<SIZE; i++){
//			UPPER_BOUND[i]=Constants.UPPER_BOUND_5;
//			LOWER_BOUND[i]=Constants.LOWER_BOUND_5;
			position[i]=generator.nextFloat()*(upperBound[i]-lowerBound[i])+lowerBound[i];
			velocity[i]=2*generator.nextFloat()*(upperBound[i]-lowerBound[i])+(lowerBound[i]-upperBound[i]);
			
			particleBestPosition[i]=position[i];
			
			
		}
		
		
	}
	
	public int getNumberOfGenes(){
		return SIZE;
	}
		
	public void setPosition(float[] position) {
		for(int i=0; i<position.length; i++){
			this.position[i]=position[i];
		}
	}	
	
	public float[] getPosition() {
		return position;
	}	
	
	public void setVelocity(float[] vel) {
		for(int i=0; i<vel.length; i++){
			this.velocity[i]=vel[i];
		}
	}	
	
	public float[] getVelocity() {
		return velocity;
	}	
	
	public void setParticleBest(float[] best) {
			for(int i=0; i<best.length; i++){
				particleBestPosition[i]=best[i];
			}
		}
	
	public void setCurrentFitness(float best) {
		currentFitness=best;
	}
	
	public float getCurrentFitness(){
		return currentFitness;
	}
	public void setBestFitness(float best) {
		bestFitness=best;
	}
	
	public float getBestFitness(){
		return bestFitness;
	}
	public float[] getParticleBest(){
			
		return particleBestPosition;
	}
		
	public void updatePosition(){
		
		for(int i=0; i<position.length; i++){
				position[i]=position[i] + velocity[i];
				
				if (position[i]>=upperBound[i]){
					velocity[i]=(float)0;
					position[i]=lowerBound[i];
				}
				
				if (position[i]<=lowerBound[i]){
					velocity[i]=(float)0;
					position[i]=lowerBound[i];
				}
				
		}
				
			
}
			
//		public void updateVelocity(float weight, float c1, float c2, float[] globalBest, float[] maxVelocity){
//			for(int i=0; i<SIZE; i++){
//				Random generator=new Random();
//				float r1=generator.nextFloat();
//				float r2=generator.nextFloat();
//				velocity[i]=weight*velocity[i]+c1*r1*(globalBest[i]-position[i])+c2*r2*(particleBestPosition[i]-position[i]);
//				if (velocity[i]>maxVelocity[i]){
//					velocity[i]=maxVelocity[i];
//				}
//			}
//		}
		
		public void updateVelocity(float weight, float c1, float c2, float r1, float r2, float[] globalBest, float[] maxVelocity){
			for(int i=0; i<velocity.length; i++){
				
				velocity[i]=weight*velocity[i]+c1*r1*(globalBest[i]-position[i])+c2*r2*(particleBestPosition[i]-position[i]);
				if (velocity[i]>maxVelocity[i]){
					velocity[i]=maxVelocity[i];
				}
			}
		}
		
		public void updateParticle(float weight, float c1, float c2, float r1, float r2, float[] globalBest, float[] maxVelocity){
			
				this.updateVelocity(weight, c1, c2, r1, r2, globalBest, maxVelocity);
				this.updatePosition();
		}
		
		public Particle copy(){
			
			Particle copy=new Particle(benchmarkNumber);
			
			copy.setVelocity(velocity);
			copy.setPosition(position);
			copy.setParticleBest(particleBestPosition);
			copy.setCurrentFitness(currentFitness);
			copy.setBestFitness(bestFitness);
			
			return copy;
		}
		
		public boolean equals(Particle anotherParticle){
			boolean equal=false;
			for(int i=0; i<velocity.length; i++){
				if(Math.abs(bestFitness-anotherParticle.getBestFitness())<error){
					equal=true;
				}
				else equal=false;
			}
			
			return equal;
			
		}
		
		public int compareTo(Object anotherParticle){
			if (currentFitness==((Particle) anotherParticle).getCurrentFitness()){
				return 0;
			}else if (currentFitness>((Particle)anotherParticle).getCurrentFitness()){
				return 1;
			}
			else return -1;
		}
		
		public String toString(){
			String particle="Particle:\n";
			for (int i=0; i<position.length; i++){
				particle=particle + "GENE_" + i +position[i]+"\n";
			}
			
			particle=particle+"Best Fitness: "+ bestFitness + "\n";
//			String particle="Theta: " + position[0] +"\n"+
//			"Length: " + position[1]+ "\n"+
//			"Current Fitness: "+ currentFitness +"\n"+
//			"Best Theta: " + particleBestPosition[0] +"\n"+
//			"Best Length: " + particleBestPosition[1]+"\n"+
//			"Best Fitness: "+ bestFitness + "\n";
			
			return particle;
			
		}
}

	
	
	
	
	
	


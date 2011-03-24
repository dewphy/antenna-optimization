
import java.util.*;
import java.io.*;
import java.text.*;
public class Particle implements Comparable<Object> {
	
	final int SIZE=2;
	final float[] UPPER_BOUND = Constants.UPPER_BOUND_2;
	final float[] LOWER_BOUND = Constants.LOWER_BOUND_2;
	
	
	
	private float[] position;
	private float[] velocity;
	private float[] particleBestPosition;
	
	private float currentFitness;
	private float bestFitness;
	
	public Particle(){
		position=new float[SIZE];
		velocity=new float[SIZE];
		particleBestPosition=new float[SIZE];
		
		Random generator=new Random();
		for(int i=0; i<SIZE; i++){
			position[i]=generator.nextFloat()*(UPPER_BOUND[i]-LOWER_BOUND[i])+LOWER_BOUND[i];
			velocity[i]=2*generator.nextFloat()*(UPPER_BOUND[i]-LOWER_BOUND[i])+(LOWER_BOUND[i]-UPPER_BOUND[i]);
			
			particleBestPosition[i]=position[i];
			
			
		}
		
		
	}
		
	public void setPosition(float[] position) {
		for(int i=0; i<SIZE; i++){
			this.position[i]=position[i];
		}
	}	
	
	public float[] getPosition() {
		return position;
	}	
	
	public void setVelocity(float[] velocity) {
		for(int i=0; i<SIZE; i++){
			this.velocity[i]=velocity[i];
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
		
		for(int i=0; i<SIZE; i++){
				position[i]=position[i] + velocity[i];
				
				if (position[i]>=UPPER_BOUND[i]){
					velocity[i]=(float)0;
					position[i]=UPPER_BOUND[i];
				}
				
				if (position[i]<=LOWER_BOUND[i]){
					velocity[i]=(float)0;
					position[i]=LOWER_BOUND[i];
				}
				
		}
				
			
}
			
		public void updateVelocity(float weight, float c1, float c2, float[] globalBest, float[] maxVelocity){
			for(int i=0; i<SIZE; i++){
				Random generator=new Random();
				float r1=generator.nextFloat();
				float r2=generator.nextFloat();
				velocity[i]=weight*velocity[i]+c1*r1*(globalBest[i]-position[i])+c2*r2*(particleBestPosition[i]-position[i]);
				if (velocity[i]>maxVelocity[i]){
					velocity[i]=maxVelocity[i];
				}
			}
		}
		
		public void updateParticle(float weight, float c1, float c2, float[] globalBest, float[] maxVelocity){
			
				this.updateVelocity(weight, c1, c2, globalBest, maxVelocity);
				this.updatePosition();
		}
		
		public Particle copy(){
			
			Particle copy=new Particle();
			
			copy.setVelocity(velocity);
			copy.setPosition(position);
			copy.setParticleBest(particleBestPosition);
			copy.setCurrentFitness(currentFitness);
			copy.setBestFitness(bestFitness);
			
			return copy;
		}
		
		public boolean equals(Particle anotherParticle){
			boolean equal=false;
			for(int i=0; i<SIZE; i++){
				if(bestFitness==anotherParticle.getBestFitness()){
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
			
			String particle="Theta: " + position[0] +"\n"+
			"Length: " + position[1]+ "\n"+
			"Current Fitness: "+ currentFitness +"\n"+
			"Best Theta: " + particleBestPosition[0] +"\n"+
			"Best Length: " + particleBestPosition[1]+"\n"+
			"Best Fitness: "+ bestFitness + "\n";
			
			return particle;
			
		}
}

	
	
	
	
	
	


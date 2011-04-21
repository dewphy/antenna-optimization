import java.util.*;
public class Main {

	/**
	 * @param args
	 */
	final static float ACC=0.0001f;
    final static float BEST_FITNESS= 3.2063f;
	
    public static void main(String[] args) {
//    	ParticleSwarmOptimizer opt=new ParticleSwarmOptimizer(1);
//    	opt.printSwarm();
		
		for (int i=0; i<1; i++){
		
			
		
			ParticleSwarmOptimizer opt=new ParticleSwarmOptimizer(2, true, "Hybrid");
			
		
			
			
			//opt.printSwarm();
		
			
			while (!opt.getFitnessValues().isOptimumFound() && !opt.maxEvaluationsReached()){
				//System.out.println("Fitness gen" + j +": "+ opt.getBestParticleEver().getBestFitness());
				
				opt.updateParticleSwarm();
				
				
				
				//k=opt.getNumberOfEvaluations();
				//System.out.println("Evaluating: " + k +" Run: "+i);
				//opt.printBestParticle();
				
			}
			
			
			opt.printBestParticle();
			System.out.println("Number of Evaluations: " + opt.getNumberOfEvaluations());
			
		//}
		
		//int horiz=(int) (opt.getBestParticleEver().getPosition()[0]/0.5);
		//int vert=(int)((opt.getBestParticleEver().getPosition()[1]-5)/0.08);
		
		//System.out.println("Matrix row: "+ horiz + " Matrix column: " +vert);
		//System.out.println("Value: " + opt.getFitnessValues().evaluate(opt.getBestParticleEver().getPosition()[0], opt.getBestParticleEver().getPosition()[1]) );
		/*System.out.println("Value: " + opt.getFitnessValues().evaluate(45.55f, 6.51f) + (" ") + (int)45.55/0.5 + " "+ (int) ( (6.51-5)/0.08f));
		float[] test1={1f,0f,0f,0f};
		float[] test2={0f,1f,0f,0f};
		float[] test3={0f,0f,1f,0f};
		System.out.println("Point:" +opt.getFitnessValues().findPointInPlane(test1,test2,test3));
		opt.printSwarm();*/
		//FitnessEvaluatorImpl eval=new FitnessEvaluatorImpl(1);
		//eval.load();
		//eval.print();*/
		
		//System.out.println(eval.getNumberOfEvaluations());
		//opt.getFitnessValues().searchArray();
	}
		/*for (int i=0; i<data.size(); i++){
			for (int j=0; j<data.get(i).size();j++){
				System.out.print(data.get(i).get(j).getBestFitness()+" | ");
				
			}
			System.out.println(i);
		}*/
		//System.out.print(data);
		
		//System.out.println("Mean Hit Time: "+ findMeanHitTime(data, BEST_FITNESS));
		//System.out.println("Mean Best Fitness: "+ findMeanBestFitness(data, BEST_FITNESS));
//		//System.out.println("Mean Genotypic Distance: "+ findMeanGenotypicDistance(data, BEST_FITNESS));
//		

	}
	
	public static float findMeanHitTime(List<List<Particle>> data, float BEST_FITNESS){
		int count=0;
		int j=0;
		boolean notFound=true;
		
		for (int i=0; i<data.size(); i++){
			j=0;
			notFound=true;
			
			while (notFound && j!=data.get(i).size()){
				
				if (Math.abs(data.get(i).get(j).getBestFitness()-BEST_FITNESS)<ACC){
					
					count+=j;
					//System.out.println("Hit: "+data.get(i).get(j).getBestFitness()+" Generation: "+j+ " Count: "+ count);
					notFound=false; 
				}
				
				j++;
				
			}
		
		}
		
		return count/data.size();
	}
	
	public static float findMeanBestFitness(List<List<Particle>> data, float BEST_FITNESS){
		int count=0;
		float meanFitness=0f;
		
		for (int i=0; i<data.size(); i++){
			float temp=data.get(i).get(data.get(i).size()-1).getBestFitness();
			
			if (Math.abs(temp-BEST_FITNESS)>ACC){
				meanFitness+=data.get(i).get(data.get(i).size()-1).getBestFitness();
				count++;
			}
		
		}
		
		System.out.println("Failure Rate: "+ (float)count/data.size()*100 + "%");
		return meanFitness/(count*BEST_FITNESS);
		
	}
	
	public static float findMeanGenotypicDistance(List<List<Particle>> data){
		
		float[] GLOBAL_POSITION={0.99f, 0.99f, 0.99f,0.99f, 0.99f, 0.99f};
		float[] MAX={1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f};
		float[] MIN={0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f};
		float distanceAccum=0f;
//		float RANGE_1=180f;
//		float RANGE_2=10f;
//		float thetaGlobal=90f;
//		float lengthGlobal=6.5f;
		float distanceAcrossRuns=0;
		int count=1;
		
		for (int i=0; i<data.size(); i++){
			Particle temp=data.get(i).get(data.get(i).size()-1);
			
			if (Math.abs(temp.getBestFitness()-BEST_FITNESS)>ACC){
				
				for (int j=0; j<temp.getNumberOfGenes(); j++){
				
				distanceAccum+=Math.pow((temp.getPosition()[j]-GLOBAL_POSITION[j])/(MAX[j]-MIN[j]), 2);
				
				
			}
			distanceAcrossRuns+=Math.sqrt(distanceAccum);
			count++;
			}
			
		}
		return distanceAcrossRuns/count;
	}
}
		
		
		

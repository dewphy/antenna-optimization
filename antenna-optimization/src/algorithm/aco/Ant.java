package algorithm.aco;

import algorithm.pso.Particle;
import benchmark.Constants;

public class Ant implements Comparable<Object> {
	
	private int[] solution;
	private float currentFitness;
	//final float DECIMAL_PLACES=1000f;
	
	final int NUM=19;
	final int SEPARATOR=7;
	
	private int benchmarkNumber;
	private float[] lowerBound;
	private float[] upperBound;
	private int[] decimals;
	private int[] separator;
	
	
	public Ant(int benchNumber){
		benchmarkNumber=benchNumber;
		
		switch (benchmarkNumber){
		case 1: lowerBound=Constants.LOWER_BOUND_1;
				upperBound=Constants.UPPER_BOUND_1;
				decimals=Constants.DECIMAL_PLACES;
				break;
		case 2: lowerBound=Constants.LOWER_BOUND_2;
				upperBound=Constants.UPPER_BOUND_2;
				decimals=Constants.DECIMAL_PLACES;
				break;
		
		case 3: lowerBound=Constants.LOWER_BOUND_3;
				upperBound=Constants.UPPER_BOUND_3;
				decimals=Constants.DECIMAL_PLACES;
				break;
		
		case 4: lowerBound=Constants.LOWER_BOUND_4;
				upperBound=Constants.UPPER_BOUND_4;
				decimals=Constants.DECIMAL_PLACES;
				break;
		
		case 5: 
				lowerBound=new float[6];
				upperBound=new float[6];
				decimals=new int[6];
				for(int i=0; i<lowerBound.length; i++){
					upperBound[i]=Constants.UPPER_BOUND_5;
					lowerBound[i]=Constants.LOWER_BOUND_5;
					decimals[i]=Constants.DECIMAL_PLACES_5;
				}
				break;
				
		case 6: 
				lowerBound=new float[12];
				upperBound=new float[12];
				decimals=new int[12];
				for(int i=0; i<lowerBound.length; i++){
					upperBound[i]=Constants.UPPER_BOUND_5;
					lowerBound[i]=Constants.LOWER_BOUND_5;
					decimals[i]=Constants.DECIMAL_PLACES_5;
				}
				break;
		
	}
		float[] length=new float[lowerBound.length];
		separator=new int[lowerBound.length];
		int solutionLength=0;
		for (int i=0;i<lowerBound.length; i++){
			length[i]=(upperBound[i]-lowerBound[i])*decimals[i];
			separator[i]=(int)(Math.log(length[i])/Math.log(2))+1;
			//System.out.println("Number of bits: " +separator[i]);
			solutionLength=solutionLength+separator[i];
		}
		
		solution=new int[solutionLength];
	}

	public int[] getSolution(){
		return solution;
	}
	
	public void setSolution(int[] sol){
		for (int i=0; i<sol.length; i++){
			solution[i]=sol[i];
		}
	}
	
	public void setSolution(int sol, int j){
		
			solution[j]=sol;
	}
	public float getCurrentFitness(){
		return currentFitness;
	}
	
	public void setCurrentFitness(float fit){
		currentFitness=fit;
	}
	
	public int length(){
		return solution.length;
	}
	
//	public float getLength(){
//		float length=0;
//		for (int i=0; i<SEPARATOR-1; i++){
//			length=(float) (solution[solution.length-i-1]*Math.pow(0.1,2-i)+length);
//		}
//		return length;
//	}
//	
//	public float getTheta(){
//		float theta=0;
//		for (int i=0; i<SEPARATOR; i++){
//			theta=(float) (solution[i]*Math.pow(0.1,2-i)+theta);
//		}
//		return theta;
//	}
	
	public float getLength(){
		float length=lowerBound[0];
		for (int i=SEPARATOR; i<solution.length; i++){
			length=(float) (solution[i]*Math.pow(2,i-SEPARATOR)+length);
		}
		return length/1000;
	}
	
	public float getTheta(){
		float theta=0;
		for (int i=0; i<SEPARATOR; i++){
			theta=(float) (solution[i]*Math.pow(2,SEPARATOR-i-1)+theta);
		}
		return (float) Math.toDegrees(theta/100);
	}
	
//	public float[] getArrayWithPhenValues(){
//		
//		
//		float[] values=new float[separator.length];
//		
//		int pointer=0;
//		boolean flag=false;
//		for (int i=0; i<separator.length; i++){
//			values[i]=0;
//			flag=!flag;
//			for (int j=0; j<separator[i]; j++){
//				if (flag){
//				values[i]=(float) (solution[pointer]*Math.pow(2,separator[i]-j-1)+values[i]);
//				}else {
//					values[i]=(float) (solution[pointer]*Math.pow(2,j)+values[i]);
//				}
//				//System.out.println(pointer+ " "+solution[pointer]);
//				pointer++;
//			}
//			values[i]=values[i]/decimals[i]+lowerBound[i];
//			//System.out.print("\nValue "+i+": "+values[i]+"\n");
//		}
//
//		return values;
//	}
public float[] getArrayWithPhenValues(){
		
		
		float[] values=new float[separator.length];
		
		int pointer=0;
		boolean flag=false;
		for (int i=0; i<separator.length; i++){
			values[i]=0;
			flag=!flag;
			for (int j=0; j<separator[i]; j++){
				
					values[i]=(float) (solution[pointer]*Math.pow(2,j)+values[i]);
				
				//System.out.println(pointer+ " "+solution[pointer]);
				pointer++;
			}
			values[i]=values[i]/decimals[i]+lowerBound[i];
			//System.out.print("\nValue "+i+": "+values[i]+"\n");
		}

		return values;
	}
public void setSolutions(float gene, int position){
	int number=  Math.round((gene-lowerBound[position])*decimals[position]);
	//System.out.println("Number for " + gene + " at position " + position +" : "+ number);
	int start=0;
	for (int i=0; i<position; i++){
		start=start+separator[i];
	}
	//System.out.println("Start: "+ start);
	//System.out.println("Separator: "+ separator[position]);
	for (int i=0; i<separator[position]; i++){
		this.setSolution(number%2,start+i);
		number=number/2;
	}
	
}
//public void setSolutions(float gene, int position){
//		int number=  Math.round((gene-lowerBound[position])*decimals[position]);
//		//System.out.println("Number for " + gene + " at position " + position +" : "+ number);
//		int start=0;
//		for (int i=0; i<position; i++){
//			start=start+separator[i];
//		}
//		//System.out.println("Start: "+ start);
//		//System.out.println("Separator: "+ separator[position]);
//		for (int i=0; i<separator[position]; i++){
//			if (position%2==0){
//			this.setSolution(number%2,separator[position]+start-i-1);
//			//System.out.println(number%2 +" at " + (separator[position]+start-i-1));
//		}else {this.setSolution(number%2,start+i);}
//			number=number/2;
//		}
//		
//	}
	
	public String toString(){
		String ant="Ant:\nChromosome: " ;
		for (int j=0; j<solution.length; j++){
			ant=ant + this.getSolution()[j];
			
		}
		
		for (int i=0; i<separator.length; i++){
			ant=ant + "\nParameter " + i +" " +getArrayWithPhenValues()[i];
		}
			
		//ant= ant +  "\nTheta: " + this.getTheta() +"\n" + "Length: " + this.getLength() + "\n";
			
	
		
		ant=ant+"\nFitness: "+ this.getCurrentFitness() + "\n";
//		String particle="Theta: " + position[0] +"\n"+
//		"Length: " + position[1]+ "\n"+
//		"Current Fitness: "+ currentFitness +"\n"+
//		"Best Theta: " + particleBestPosition[0] +"\n"+
//		"Best Length: " + particleBestPosition[1]+"\n"+
//		"Best Fitness: "+ bestFitness + "\n";
		
		return ant;
		
	}
	
	public Ant copy(){
		Ant copied=new Ant(benchmarkNumber);
		copied.setSolution(solution);
		copied.setCurrentFitness(currentFitness);
		
		return copied;
		
	}
	
	public int compareTo(Object anotherAnt){
		if (currentFitness==((Ant) anotherAnt).getCurrentFitness()){
			return 0;
		}else if (currentFitness>((Ant)anotherAnt).getCurrentFitness()){
			return 1;
		}
		else return -1;
	}
}

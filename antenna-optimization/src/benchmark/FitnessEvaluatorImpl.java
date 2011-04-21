package benchmark;

import java.util.*;
import java.io.*;


import java.io.FileNotFoundException;
import java.io.IOException;


import java.text.*;

//Reads gain values from the file corresponding to the benchmark

public class FitnessEvaluatorImpl implements FitnessEvaluator{
	
	private int numberOfEvaluations; 
	private int benchmarkNumber; //1: theta, length; 2: theta, distance, 3: theta, beta, 4: alpha, length 
	private String filePath;	// defined in the interface
	private List<List<Float>> fitnessValues; //to create a matrix of fitness values 
	private DecimalFormat format1;
	private float theta_step;
	private float param2_step;
	private float lower_bound_1;
	private float lower_bound_2;
	private float upper_bound_1;
	private float upper_bound_2;
	
	//Constructor takes in the number of the benchmark, links to file path
	public FitnessEvaluatorImpl(int benchNum){
		numberOfEvaluations=0;
		benchmarkNumber=benchNum;
		fitnessValues=new ArrayList<List<Float>>();
		format1=new DecimalFormat("00.000000");
		
		switch (benchmarkNumber){
			case 1: filePath=PATH_BENCH1; 
					theta_step=THETA_STEP;
					param2_step=LENGTH_STEP; 
					lower_bound_1=LOWER_THETA;  
					upper_bound_1=UPPER_THETA_1;
					lower_bound_2=LOWER_LENGTH;
					upper_bound_2=UPPER_LENGTH;
					
					break;
			case 2: filePath=PATH_BENCH2; 
					theta_step=THETA_STEP; 
					param2_step=DISTANCE_STEP;
					lower_bound_1=LOWER_THETA;  
					upper_bound_1=UPPER_THETA_2;
					lower_bound_2=LOWER_DISTANCE;
					upper_bound_2=UPPER_DISTANCE;
					break;
			case 3: filePath=PATH_BENCH3; 
					theta_step=THETA_STEP; 
					param2_step=BETA_STEP;
					lower_bound_1=LOWER_BETA;  
					upper_bound_1=UPPER_BETA;
					lower_bound_2=LOWER_ALPHA;
					upper_bound_2=UPPER_ALPHA;
					break;
			case 4: filePath=PATH_BENCH4; 
					theta_step=THETA_STEP; 
					param2_step=LENGTH_STEP;
					lower_bound_1=LOWER_THETA;  
					upper_bound_1=UPPER_THETA_1;
					lower_bound_2=LOWER_LENGTH;
					upper_bound_2=UPPER_LENGTH;
					break;
			default: filePath=null;
			
		}
		
	}
	
	//Returns the number of Evaluations performed for the class instance
	public int getNumberOfEvaluations(){
		return numberOfEvaluations;
	}
	
	//returns path to file with the fitness values
	public String getFilePath(){
		return filePath;
	}
	
	//returns the # of the benchmark
	public int getBenchmarkNumber(){
		return benchmarkNumber;
	}
	
	//Loads data as float numbers into the ArrayList from file
	public void load(){
	
		File file = new File(filePath);
		
		FileReader fis = null;
		BufferedReader bis = null;
		//DataInputStream dis = null;

		try {
			fis = new FileReader(file);

			
			bis = new BufferedReader(fis);
			
			String str =bis.readLine();
			
			
		while (str!=null) {
			
				
			
				
				ArrayList<Float> gainAtLength = new ArrayList<Float>();
				
				String lineGain=str.trim(); //read a line from the file
				StringTokenizer strTokenizerGain=new StringTokenizer(lineGain, ",");
				//System.out.println(lineGain);
				
				
				while (strTokenizerGain.hasMoreTokens()){
					String temp = strTokenizerGain.nextToken().trim();
					gainAtLength.add(Float.parseFloat(temp));
					
				}
				
				
				fitnessValues.add(gainAtLength);
				str=bis.readLine();
				
				//System.out.println(lineGain);
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NumberFormatException e) {
			e.printStackTrace();
		}
}
	
	//Evaluates the fitness value for individual with parameters x1 and x2
	public float evaluate(float[] position){
		float x1 = position[0];
		float x2 = position[1];
		
		numberOfEvaluations++;
		
		int row=(int) ((x1-lower_bound_1)/theta_step);
		int column=(int)((x2-lower_bound_2)/param2_step);
		
		return fitnessValues.get(row).get(column);
	}
	
	public float evaluate(int[] position){
		numberOfEvaluations++;

		int row = position[0];
		int column = position[1];

		return fitnessValues.get(row).get(column);
	}

	public float findPointInPlane(float[] x, float[] y, float z[]){
		
		float n_x=(y[1]-y[0])*(z[2]-z[0])-(z[1]-z[0])*(y[2]-y[0]);
		float n_y=-(z[2]-z[0])*(x[1]-x[0])+(x[2]-x[0])*(z[1]-z[0]);
		float n_z=(x[1]-x[0])*(y[2]-y[0])+(y[1]-y[0])*(x[2]-x[0]);
		
		float result=z[0]+(n_x*(x[3]-x[0]) + n_y*(y[3]-y[0]))/n_z;
		
		return result;
		
	}
	public float getTheta(int row){
		return row*theta_step+lower_bound_1;
	}
	
	public float getParam2(int column){
		return column*param2_step+lower_bound_2;
	}
	//Prints the ArrayList with the fitness values
	
	public void print(){
		try {
		FileWriter fwriter= new FileWriter("data/Output.txt");
		PrintWriter outputFile=new PrintWriter(fwriter);
		
		for (int i=0; i<fitnessValues.size(); i++){
			//System.out.println(((ArrayList)fitnessValues).get(i).toString());
			//System.out.println(format1.format((float)(i+1))+" | ");
			
			for (int j=0; j< ((ArrayList<Float>) fitnessValues.get(i)).size();j++){
				//System.out.print(format1.format(((ArrayList<Float>) fitnessValues.get(i)).get(j))+" | ");
				outputFile.print(format1.format(((ArrayList<Float>) fitnessValues.get(i)).get(j))+" | ");
			}
			outputFile.println(i);
			//outputFile.println();
			
		}
		outputFile.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void searchArray(){
		float alreadyFound=0;
		int row=0;
		int column=0;
		for (int i=0; i<fitnessValues.size(); i++){
			
			for (int j=0; j< ((ArrayList<Float>) fitnessValues.get(i)).size();j++){
				//System.out.print(format1.format(((ArrayList<Float>) fitnessValues.get(i)).get(j))+" | ");
				if ((float)fitnessValues.get(i).get(j)>alreadyFound){
					alreadyFound=(float) fitnessValues.get(i).get(j);
					row=i;
					column=j;
				}
			}
			
		}
		System.out.println("Row: "+ row+" Column: "  + column + " BestFitness: " +  alreadyFound);
	}
	
	public void searchArray(float value){
		
		
		for (int i=0; i<fitnessValues.size(); i++){
			
			for (int j=0; j< ((ArrayList<Float>) fitnessValues.get(i)).size();j++){
				//System.out.print(format1.format(((ArrayList<Float>) fitnessValues.get(i)).get(j))+" | ");
				if ((float)fitnessValues.get(i).get(j)==value){
					System.out.println("Theta: "+ i*theta_step +" Length: " +(lower_bound_2+j*param2_step)+" Fitness: " + (float)fitnessValues.get(i).get(j));
					
				}
			}
		}
	}

	@Override
	public float[] getUpperBounds() {
		float[] upperBounds = {90f, 3f};
		return upperBounds;
	}

	@Override
	public float[] getLowerBounds() {
		float[] lowerBounds = {0f, 0.5f};
		return lowerBounds;
	}

	@Override
	public int[] getMaxIndexes() {
		int[] maxIndexes = {181, 126};
		return maxIndexes;
	}
	
	@Override
	public float[] getSteps() {
		float[] steps = {0.5f, 0.02f};
		return steps;
	}

	@Override
	public int getPositionLength() {
		return 2;
	}

	
	
//	@Override
//	public float[] pickRandomPosition() {
//		Random randomGenerator = new Random();
//		float[] position = new float[getPositionLength()];
//		for (int k = 0; k < getPositionLength(); k++) {
//			float lb = getLowerBounds()[k];
//			float ub = getUpperBounds()[k];
//			float step = getSteps()[k];
//
//			int delta = (int)(((ub-lb)/step + 1) * randomGenerator.nextFloat());
//			position[k] = lb + delta*step;
//		}
//		return position;
//	}

	@Override
	public int[] pickRandomPosition() {
		Random randomGenerator = new Random();
		int[] position = new int[getPositionLength()];
		for (int k = 0; k < getPositionLength(); k++) {
			int maxIndex = getMaxIndexes()[k];
			position[k] = (int)(maxIndex*randomGenerator.nextFloat());
		}
		return position;
	}
	
	@Override
	public int[] pickBestNeighbor(int[] position) {
		float bestFitness = evaluate(position);

		// Initialize moved position to current position.
		float movedFitness = evaluate(position);
		int[] movedPosition = new int[getPositionLength()];
		for (int k = 0; k < getPositionLength(); k++) {
			movedPosition[k] = position[k];
		}

		// Find the best neighbor.
		int moveIndex = 0;
		for (int k = 0; k < getPositionLength(); k++) {
			int maxIndex = getMaxIndexes()[k];

			movedPosition[k] = position[k] < maxIndex -1 ? position[k] + 1 : maxIndex -1;
			if (evaluate(movedPosition) > bestFitness) {
				bestFitness = evaluate(movedPosition);
				moveIndex = (k+1);
			}

			movedPosition[k] = position[k] > 0 ? position[k] - 1 : 0;
			if (evaluate(movedPosition) > bestFitness) {
				bestFitness = evaluate(movedPosition);
				moveIndex = -(k+1);
			}
			
			movedPosition[k] = position[k];
		}

		// Return best neighbor.
		if (moveIndex == 0) {
			return position;
		} else {
			int[] bestPosition = new int[getPositionLength()];
			for (int k = 0; k < getPositionLength(); k++) {
				bestPosition[k] = position[k];
			}
			if (moveIndex > 0) {
				bestPosition[moveIndex-1] += 1;
			} else {
				bestPosition[-moveIndex-1] -= 1;
			}
			return bestPosition;
		}
	}
	
	@Override
	public float[] pickBestNeighbor(float[] position) {
		float bestFitness = evaluate(position);

		// Initialize moved position to current position.
		float movedFitness = evaluate(position);
		float[] movedPosition = new float[getPositionLength()];
		for (int k = 0; k < getPositionLength(); k++) {
			movedPosition[k] = position[k];
		}

		// Find the best neighbor.
		int move = -1;
		for (int k = 0; k < getPositionLength(); k++) {
			float step = getSteps()[k];
			float ub = getUpperBounds()[k];
			float lb = getLowerBounds()[k];
			
			movedPosition[k] = movedPosition[k] + step < ub ? movedPosition[k] + step : ub;
			System.out.println("movedPosition[" + k +"]: " + movedPosition[k]);
			if (evaluate(movedPosition) > bestFitness) {
				bestFitness = evaluate(movedPosition);
				move = (k+1);
			}
			
			movedPosition[k] = movedPosition[k] - step > lb ? movedPosition[k] - step : lb;
			if (evaluate(movedPosition) > bestFitness) {
				bestFitness = evaluate(movedPosition);
				move = -(k+1);
			}
		}
		
		// Return best neighbor.
		if (move == -1) {
			return position;
		} else {
			float[] bestPosition = new float[getPositionLength()];
			for (int k = 0; k < getPositionLength(); k++) {
				bestPosition[k] = position[k];
			}
			if (move > 0) {
				bestPosition[move-1] += getSteps()[move-1];
			} else {
				bestPosition[-move-1] += getSteps()[-move-1];
			}
			return bestPosition;
		}
	}


	@Override
	public int[] pickAnyNeighbor(int[] position) {
		Random randomGenerator = new Random();

		// Set picked position to the current position.
		int[] pickedPosition = new int[getPositionLength()];
		for (int k = 0; k < getPositionLength(); k++) {
			pickedPosition[k] = position[k];
		}

		// Pick any current.
		int k = (int)(getPositionLength()*randomGenerator.nextFloat());
		
		if (randomGenerator.nextFloat() < 0.5) {
			int maxIndex = getMaxIndexes()[k];
			pickedPosition[k] = position[k] < maxIndex-1 ? position[k] + 1 : maxIndex-1;
		} else {
			pickedPosition[k] = position[k] > 0 ? position[k] - 1 : 0;
		}
		return pickedPosition;
	}
}
//
//public class FitnessEvaluatorImpl implements FitnessEvaluator {
//	private int numberOfEvaluation = 0;
//
//	@Override
//	public void load(int benchmarkNumber) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public float evaluate(float x1, float x2) {
//		numberOfEvaluation++;
//		return (x1-2)*(x1-2) + (x2-5)*(x2-5);
//	}
//
//	@Override
//	public void print() {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public int getNumberOfEvaluation() {
//		return numberOfEvaluation;
//	}
//
//}

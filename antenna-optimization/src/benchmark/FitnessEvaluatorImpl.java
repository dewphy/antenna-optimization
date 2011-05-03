package benchmark;

import java.util.*;
import java.io.*;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import java.text.*;

//Reads gain values from the file corresponding to the benchmark

public class FitnessEvaluatorImpl implements FitnessEvaluator{

	private int numberOfEvaluations;
	private final int maxNumberOfEvaluations;

	private int benchmarkNumber; //1: theta, length; 2: theta, distance, 3: theta, beta, 4: alpha, length 
	private String filePath;	// defined in the interface

	private List<List<Float>> fitnessValues; //to create a matrix of fitness values 
	private DecimalFormat format1;

	final String FORMAT="GW %3d, %3d, %7.4f, %7.4f, %7.4f, %7.4f, %7.4f, %7.4f, %7.4f\n";
	final String FORMAT_2="GW %3d, %3d, %8.4f, %8.4f, %8.4f, %8.4f, %8.4f, %8.4f, %8.4f\n";
	final String FORMAT_RP="RP 0,1,1,1010, %5.2f, 0., 0., 0.\n";
	final int RADIUS=1;
	final String NEC_OUT_FILE_NAME="../NEC/Debug/NEC.OUT";
	final String NEC_IN_FILE_NAME="../NEC/Debug/NEC.INP";
	final String NEC_COMMAND="../NEC/Debug/NEC";

	private float maxFitness;

	private float[][] maximaPositions;
	private int[] bestPosition;
	private float bestFitness = 0;

	private float[] step;
	private float[] lowerBound;
	private float[] upperBound;

	List<String> memory = new ArrayList<String>();
	Map<String,Float> cache = new HashMap<String,Float>();
	private boolean discrete;

	private int NDIP;

	//Constructor takes in the number of the benchmark, links to file path
	public FitnessEvaluatorImpl(int benchmarkNumber, int maxNumberOfEvaluations, boolean discrete){
		this.discrete = discrete;
		this.numberOfEvaluations = 0;
		this.maxNumberOfEvaluations = maxNumberOfEvaluations;
		this.benchmarkNumber=benchmarkNumber;
		fitnessValues=new ArrayList<List<Float>>();
		format1=new DecimalFormat("00.000000");

		switch (benchmarkNumber){
		case 1: lowerBound=Constants.LOWER_BOUND_1;
		upperBound=Constants.UPPER_BOUND_1;
		step=Constants.STEP_1;
		filePath=Constants.PATH_BENCH_1;
		maxFitness=Constants.MAX_FITNESS_1;
		maximaPositions=Constants.BEST_POSITION_1;
		break;

		case 2: lowerBound=Constants.LOWER_BOUND_2;
		upperBound=Constants.UPPER_BOUND_2;
		step=Constants.STEP_2;
		filePath=Constants.PATH_BENCH_2;
		maxFitness=Constants.MAX_FITNESS_2;
		maximaPositions=Constants.BEST_POSITION_2;
		NDIP=10;
		break;

		case 3: lowerBound=Constants.LOWER_BOUND_3;
		upperBound=Constants.UPPER_BOUND_3;
		step=Constants.STEP_3;
		filePath=Constants.PATH_BENCH_3;
		maxFitness=Constants.MAX_FITNESS_3;
		maximaPositions=Constants.BEST_POSITION_3;
		NDIP=8;
		break;

		case 4: lowerBound=Constants.LOWER_BOUND_4;
		upperBound=Constants.UPPER_BOUND_4;
		step=Constants.STEP_4;
		filePath=Constants.PATH_BENCH_4;
		maxFitness=Constants.MAX_FITNESS_4;
		maximaPositions=Constants.BEST_POSITION_4;
		break;

		case 5:		if (discrete){
						lowerBound=new float[4];
						upperBound=new float[4];
						for(int i=0; i<lowerBound.length; i++){
							upperBound[i]=Constants.UPPER_BOUND_5;
							lowerBound[i]=Constants.LOWER_BOUND_5;
							step[i]=Constants.STEP_5;
						}
						
						
						filePath=Constants.PATH_BENCH_5;
						maxFitness=Constants.MAX_FITNESS_5_2;
						maximaPositions=Constants.BEST_POSITION_5_2;
						
					}
		else{
					lowerBound=new float[6];
					upperBound=new float[6];
					discrete=false;

					for(int i=0; i<lowerBound.length; i++){
						upperBound[i]=Constants.UPPER_BOUND_5;
						lowerBound[i]=Constants.LOWER_BOUND_5;
					}
					maxFitness=Constants.MAX_FITNESS_5;
					maximaPositions=Constants.BEST_POSITION_5;
					NDIP=7;
		}
					break;

		case 6: 
			lowerBound=new float[12];
			upperBound=new float[12];
			discrete=false;
			for(int i=0; i<lowerBound.length; i++){
				upperBound[i]=Constants.UPPER_BOUND_5;
				lowerBound[i]=Constants.LOWER_BOUND_5;

			}
			maxFitness=Constants.MAX_FITNESS_6;
			maximaPositions=Constants.BEST_POSITION_6;
			NDIP=13;
			break; 
		}

		if (discrete){
			this.load();
		}

	}

	public float[] getUpperBound(){
		return upperBound;
	}
	public float[] getLowerBound(){
		return lowerBound;
	}

	public int getPositionLength(){
		return lowerBound.length;
	}

	//Returns the number of Evaluations performed for the class instance
	public int getNumberOfEvaluations(){
//		System.out.println("NumberOfEvaluations: " + memory.size() + "/" + numberOfEvaluations + "/" + cache.size());
		if (numberOfEvaluations != memory.size()) {
			new RuntimeException("Error: Number of evaluation does not match memory size!");
		}
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

	public float evaluate(float[] position){

		StringBuilder keyBuilder = new StringBuilder(); 
		for (int i = 0; i < position.length; i++) {
			keyBuilder.append(String.valueOf(position[i]) + "|");
		}
		String key = keyBuilder.toString();

		if (!cache.containsKey(key)) {
			Float fitness;
			numberOfEvaluations++;
			if (discrete) {
				int row=Math.round ((position[0]-lowerBound[0])/step[0]);
				int column=Math.round((position[1]-lowerBound[1])/step[1]);
				//System.out.println("theta: "+position[0]+" length: "+position[1]);
				//System.out.println("COlumn: "+ column +" Row: "+ row);
				fitness=fitnessValues.get(row).get(column);

			} else {
				this.createNecInputFile(position);
				this.runNec();
				fitness = this.getGainFromOutputFile();
			}
			cache.put(key, fitness);
		}
		return cache.get(key);
	}


	public float[] evaluate(float[][] positions) {
		float[] fitnesses = new float[positions.length];
		for (int i = 0; i < positions.length; i++) {
			fitnesses[i] = evaluate(positions[i]);
		}
		return fitnesses;
	}
	
	@Override
	public float evaluate(int[] position) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append(String.valueOf(position[0]));
		for (int i = 1; i < position.length; i++) {
			keyBuilder.append("|" + String.valueOf(position[i]));
		}
		String key = keyBuilder.toString();

		if (!cache.containsKey(key)) {
			Float fitness;
			numberOfEvaluations++;
			int row=position[0];
			int column=position[1];

			fitness=fitnessValues.get(row).get(column);
			cache.put(key, fitness);
			memory.add(key);
//			System.out.println("key: " + key);
			if (fitness > bestFitness) {
				bestFitness = fitness;
				bestPosition = copyPosition(position);
			}
		}
		return cache.get(key);
	}

	@Override
	public float[] evaluate(int[][] positions) {
		float[] fitnesses = new float[positions.length];
		for (int i = 0; i < positions.length; i++) {
			fitnesses[i] = evaluate(positions[i]);
		}
//		System.out.println(memory);
		return fitnesses;
	}

	private int[] keyToIntPosition(String key) {
		String[] blocks = key.split("\\|");
		
		if (blocks.length != getPositionLength()) {
			new RuntimeException("Error: Bad key");
		}
	
		int[] position = new int[getPositionLength()];
		for (int k = 0; k < position.length; k++) {
			position[k] = Integer.valueOf(blocks[k]);
		}
		return position;
	}
	
	public boolean isOptimumFound() {
		for (Float fitness : cache.values()) {
			if (Math.abs(fitness - maxFitness) < Constants.ACCURACY) {
				return true;
			}
		}
		if (benchmarkNumber==2){

			for(String keys:cache.keySet()){
				for (int i=0; i<maximaPositions.length; i++){

					String[] tokens=keys.split("\\|");

					float distance=0;
					for (int j=0; j<tokens.length;j++){

						float gene=Float.parseFloat(tokens[j].trim());
						distance=(float) (distance+Math.pow((gene-maximaPositions[i][j])/(upperBound[j]-lowerBound[j]),2));
					}
					if (Math.sqrt(distance) < Constants.ACCURACY) {
						return true;
					}
				}
			}
		}
		return false;
	}	

	public void runNec(){
		try {
			Process p = Runtime.getRuntime().exec(NEC_COMMAND + " -i "+ NEC_IN_FILE_NAME + " -o " + NEC_OUT_FILE_NAME);
			p.waitFor();
			//	            //System.exit(0);
		}
		catch (InterruptedException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public float getGainFromOutputFile(){

		float fitness=0;

		try {
			Scanner scanner = new Scanner(new File(NEC_OUT_FILE_NAME));
			scanner.useDelimiter("RADIATION PATTERNS"); 
			String data="No Data";
			while (scanner.hasNext()) {
				data=scanner.next();
				// System.out.println("This is " +i+"part: "+data);


			}
			//System.out.println(data);
			String[] tokens=data.split("\n", 6);
			//System.out.println(tokens[5]);
			String[] gainTokens=tokens[5].split("\\s+", 7);
			//System.out.println(gainTokens[5]);
			float gainDB=Float.parseFloat(gainTokens[5].trim());
			fitness=(float) (Math.pow(10,gainDB/10));

			if (benchmarkNumber==2){
				Random randomGenerator = new Random();
				fitness=(float) (fitness+randomGenerator.nextGaussian()*Math.pow(0.02, 0.5));
			}
			scanner.close();


		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		catch (NumberFormatException e) {
			e.printStackTrace();

		}

		return fitness;
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

	public void createNecInputFile(float[] position){
		try{
			float x1=position[0];
			float x2=position[1];
			FileWriter wis = new FileWriter(NEC_IN_FILE_NAME);
			PrintWriter bwis = new PrintWriter(wis);
			switch (benchmarkNumber){
			case 1: bwis.println("CM Benchmark #1\nCE");
			float lengthHalf=x2/2;
			int segments=(int)(x2/0.02)-1;
			StringBuilder sb = new StringBuilder();
			// Send all output to the Appendable object sb
			Formatter formatter = new Formatter(sb, Locale.US);

			formatter.format(FORMAT,1,segments,0.,0.,-lengthHalf,0.,0.,-.01,0.001);
			formatter.format(FORMAT,2,2,0.,0.,-.01,0.,0.,0.01,0.001);
			//System.out.println(formatter.format(FORMAT,1,segments,0.,0.,0.01,0.,0.,lengthHalf,0.001));
			bwis.print(formatter.format(FORMAT,1,segments,0.,0.,0.01,0.,0.,lengthHalf,0.001));
			bwis.println("GE 0");
			bwis.println("EX 0,2,1,0,0.5,0");
			bwis.println("EX 0,2,2,0,0.5,0");
			bwis.println(String.format(FORMAT_RP, x1));
			break;

			case 2: bwis.println("CM Benchmark #2\nCE");
			StringBuilder sb2 = new StringBuilder();
			// Send all output to the Appendable object sb
			Formatter formatter2 = new Formatter(sb2, Locale.US);
			for (int j=1; j<=NDIP; j++){
				float POSIC=-9*x2/2+(j-1)*x2;
				formatter2.format(FORMAT_2,j,49,POSIC,0.,-0.25,POSIC,0.,0.25,0.001);
			}
			bwis.print(formatter2);
			bwis.println("GE 0");
			for (int j=1; j<=10; j++){
				bwis.println(String.format("EX 0,%d,25,0,1.0,0",j));
			}
			bwis.println(String.format(FORMAT_RP, x1));
			break;

			case 3: bwis.println("CM Benchmark #3\nCE");
			StringBuilder sb3 = new StringBuilder();
			// Send all output to the Appendable object sb
			Formatter formatter3 = new Formatter(sb3, Locale.US);

			for (int j=1; j<=NDIP; j++){
				float posx=(float) (RADIUS*Math.cos(2*Math.PI*(j-1)/NDIP));
				float posy=(float) (RADIUS*Math.sin(2*Math.PI*(j-1)/NDIP));
				formatter3.format(FORMAT_2,j,49,posx,posy,-0.25,posx,posy,0.25,0.001);
			}

			bwis.print(formatter3);
			bwis.println("GE 0");

			for (int j=1; j<=NDIP; j++){
				float ex=(float) -Math.cos(2*Math.PI*x2*(j-1));
				bwis.println(String.format("EX 0,%d,25,0,1.0,%8.4f",j,ex));
			}
			bwis.println(String.format(FORMAT_RP, x1));
			break;

			case 4: bwis.println("CM Benchmark #4\nCE");
			StringBuilder sb4 = new StringBuilder();
			// Send all output to the Appendable object sb
			Formatter formatter4 = new Formatter(sb4, Locale.US);
			float posxfin=(float) ((x2-0.01)*Math.cos(Math.PI*x1/180));
			float poszfin=(float) ((x2-0.01)*Math.sin(Math.PI*x1/180));
			int segments4=(int) (x2/0.01-1);
			formatter4.format(FORMAT,1,segments4,posxfin,0.,-poszfin-0.01,0.,0.,-.01,0.001);
			formatter4.format(FORMAT,2,2,0.,0.,-.01,0.,0.,0.01,0.001);
			formatter4.format(FORMAT,3,segments4,0.,0.,0.01,posxfin,0.,poszfin+0.01,0.001);

			bwis.print(formatter4);
			bwis.println("GE 0");
			bwis.println("EX 0,2,1,0,0.5,0");
			bwis.println("EX 0,2,2,0,0.5,0");
			bwis.println(String.format(FORMAT_RP, 90f));
			break;

			case 5: 
			case 6:
				bwis.println("CM Benchmark #5\nCE");
				StringBuilder sb5 = new StringBuilder();
				// Send all output to the Appendable object sb
				Formatter formatter5 = new Formatter(sb5, Locale.US);
				float positionZ=0f;
				for (int k=0; k<position.length; k++){
					formatter5.format(FORMAT,k,49,0.,0.,positionZ-0.25f,0.,0.,positionZ+0.25f,0.0001);
					positionZ=positionZ+position[k];
				}
				formatter5.format(FORMAT,position.length,49,0.,0.,positionZ-0.25f,0.,0.,positionZ+0.25f,0.0001);
				bwis.print(formatter5);
				bwis.println("GE 0");
				for (int k=0; k<=position.length; k++){
					bwis.println(String.format("EX 0,%3d,25,0,1.0,0.",k));
				}
				bwis.println(String.format(FORMAT_RP, 90f));
				break;
			}	

			bwis.println("EN");
			bwis.close();
			wis.close();

		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldTerminate() {
		return (getNumberOfEvaluations() > maxNumberOfEvaluations) || isOptimumFound();
	}

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
		float bestNeighborFitness = evaluate(position);

		// Initialize neighbor position and best neighbor position to current position.
		int[] neighborPosition = copyPosition(position);
		int[] bestNeighborPosition = copyPosition(position);

		// Find neighbor with best fitness.
		for (int k = 0; k < getPositionLength(); k++) {

			if (position[k] < getMaxIndexes()[k] -1) {
				neighborPosition[k] = position[k] + 1;
				if (evaluate(neighborPosition) >= evaluate(bestNeighborPosition)) {
					bestNeighborPosition = copyPosition(neighborPosition);
				}
			}
			if (position[k] > 0) {
				neighborPosition[k] = position[k] - 1;
				if (evaluate(neighborPosition) >= bestNeighborFitness) {
					bestNeighborPosition = copyPosition(neighborPosition);
				}
			}
			neighborPosition[k] = position[k];
		}

		// Return best neighbor position if any.
		return bestNeighborPosition;
	}

	private int[] copyPosition(int[] position) {
		int[] neighborPosition = new int[getPositionLength()];
		for (int k = 0; k < getPositionLength(); k++) {
			neighborPosition[k] = position[k];
		}
		return neighborPosition;
	}

	@Override
	public int[] pickAnyNeighbor(int[] position) {

		// Initialize neighbor position and best neighbor position to current position.
		int[] pickedPosition = copyPosition(position);

		int k = (int)(getPositionLength()*Math.random());

		if (Math.random() < 0.5) {
			if (position[k] < getMaxIndexes()[k] - 1) {
				pickedPosition[k] = position[k] + 1;
			}
		} else {
			if (position[k] > 0) {
				pickedPosition[k] = position[k] - 1;
			}
		}
		return pickedPosition;
	}

	@Override
	public int[] getMaxIndexes() {
		int[] maxIndexes = new int[2];
		maxIndexes[0] = fitnessValues.size();
		maxIndexes[1] = fitnessValues.get(0).size();
		return maxIndexes;
	}

	@Override
	public void print(int[] position) {
		for (int k = 0; k < this.getPositionLength(); k++) {
			System.out.print("  position[" + k + "]: " + position[k]);
		}
		System.out.println("--> fitness: " + evaluate(position));
	}

	@Override
	public float[] convertPositionToFloat(int[] position) {
		float[] floatPosition = new float[getPositionLength()];
		for (int k = 0; k < getPositionLength(); k++) {
			floatPosition[k] = lowerBound[k] + position[k]*step[k];
		}
		return floatPosition;
	}

	@Override
	public float[] getBestPosition(){
		return convertPositionToFloat(bestPosition);
	}
	
	@Override
	public float[][] getBestPositions() {
		System.out.println("Size: " + memory.size());
		float[][] bestPositions = new float[memory.size()][getPositionLength()];
		bestPositions[0] = convertPositionToFloat(keyToIntPosition(memory.get(0)));
		for (int i = 1; i < bestPositions.length; i++) {
			float[] position = convertPositionToFloat(keyToIntPosition(memory.get(i)));
			if (this.evaluate(position) > this.evaluate(bestPositions[i-1])) {
				bestPositions[i] = position;
			} else {
				bestPositions[i] = bestPositions[i-1]; 
			}
		}
		return bestPositions;
	}
	
	@Override
	public float getBestFitness(){
		return bestFitness;
	}

	@Override
	public float[] getBestFitnesses() {
		return evaluate(getBestPositions());
	}

	@Override
	public int[] getBestIntPosition() {
		return copyPosition(bestPosition);
	}
}
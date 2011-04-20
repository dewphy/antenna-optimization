
import java.util.*;
import java.io.*;
import java.lang.*;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import java.text.*;

//Reads gain values from the file corresponding to the benchmark

public class FitnessEvaluatorImpl implements FitnessEvaluator{
	
	private int numberOfEvaluations; 
	private int benchmarkNumber; //1: theta, length; 2: theta, distance, 3: theta, beta, 4: alpha, length 
	private String filePath;	// defined in the interface
	private List<List<Float>> fitnessValues; //to create a matrix of fitness values 
	private DecimalFormat format1;
	
	final String FORMAT="GW %3d, %3d, %7.4f, %7.4f, %7.4f, %7.4f, %7.4f, %7.4f, %7.4f\n";
	final String FORMAT_2="GW %3d, %3d, %8.4f, %8.4f, %8.4f, %8.4f, %8.4f, %8.4f, %8.4f\n";
	final String FORMAT_RP="RP 0,1,1,1010, %5.2f, 0., 0., 0.\n";
	final int RADIUS=1;
	final String NEC_OUT_FILE_NAME="nec2c/res.out";
	final String NEC_IN_FILE_NAME="nec2c/NEC.INP";
	final String NEC_COMMAND="nec2c/nec2c";
	
	private float theta_step;
	private float param2_step;
	private float lower_bound_1;
	private float lower_bound_2;
	private float upper_bound_1;
	private float upper_bound_2;
	
	private int NDIP;
	
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
					NDIP=10;
					break;
			case 3: filePath=PATH_BENCH3; 
					theta_step=THETA_STEP; 
					param2_step=BETA_STEP;
					lower_bound_1=LOWER_BETA;  
					upper_bound_1=UPPER_BETA;
					lower_bound_2=LOWER_ALPHA;
					upper_bound_2=UPPER_ALPHA;
					NDIP=8;
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
	public float evaluate(float[] position){
		numberOfEvaluations++;
		this.createNecInputFile(position);
		this.runNec();
		return this.getGainFromOutputFile();
	}
		
	
	//Evaluates the fitness value for individual with parameters x1 and x2
public float evaluate(float x1, float x2){
		numberOfEvaluations++;
//		float fitness= 0;
//		float[] x=new float[4];
//		float[] y=new float[4];
//		float[] z=new float[4];
//		
		
		int row=(int) ((x1-lower_bound_1)/theta_step);
		int column=(int)((x2-lower_bound_2)/param2_step);
//		
		return fitnessValues.get(row).get(column);
}
//	 if (row==fitnessValues.size()-1 && column==fitnessValues.get(0).size()-1){
//			row=row-1;
//			column=column-1;
//		}else if (row==fitnessValues.size()-1){
//			row=row-1;
//		}else if (column==fitnessValues.get(0).size()-1){
//			column=column-1;
//		}
//		
//	 float dist1=(float) (Math.pow((x1-row),2)+Math.pow((x2-column),2));
//	 float dist2=(float) (Math.pow((x1-row-1),2)+Math.pow((x2-column-1),2));
//		
//		
//		int a,b=0;
//		
//		if (dist1>dist2){
//			a=row+1;
//			b=column+1;
//		} else {
//			a=row;
//			b=column;
//		}
//			x[0]=this.getTheta(a);
//			y[0]=this.getParam2(b);
//			z[0]=fitnessValues.get(a).get(b);
//			
//			x[1]=this.getTheta(row);
//			y[1]=this.getParam2(column+1);
//			z[1]=fitnessValues.get(row).get(column+1);
//			
//			x[2]=this.getTheta(row+1);
//			y[2]=this.getParam2(column);
//			z[2]=fitnessValues.get(row+1).get(column);
//			
//			x[3]=x1;
//			y[3]=x2;
//			z[3]=0;
//			fitness=findPointInPlane(x,y,z);
		
		//create NEC Input File
//		try{
//			FileWriter wis = new FileWriter("nec2c/NEC.INP");
//			PrintWriter bwis = new PrintWriter(wis);
//		
//		bwis.println("CM Benchmark #1\nCE");
//		float lengthHalf=x2/2;
//		int segments=(int)(x2/0.02)-1;
//		StringBuilder sb = new StringBuilder();
//		   // Send all output to the Appendable object sb
//		   Formatter formatter = new Formatter(sb, Locale.US);
//		 
//		formatter.format(FORMAT,1,segments,0.,0.,-lengthHalf,0.,0.,-.01,0.001);
//		formatter.format(FORMAT,2,2,0.,0.,-.01,0.,0.,0.01,0.001);
//		//System.out.println(formatter.format(FORMAT,1,segments,0.,0.,0.01,0.,0.,lengthHalf,0.001));
//		bwis.print(formatter.format(FORMAT,1,segments,0.,0.,0.01,0.,0.,lengthHalf,0.001));
//		bwis.println("GE 0");
//		bwis.println("EX 0,2,1,0,0.5,0");
//		bwis.println("EX 0,2,2,0,0.5,0");
//		
//		String RadiationPattern=String.format(FORMAT_RP, x1);
//		bwis.println(RadiationPattern);
//		bwis.println("EN");
//		bwis.close();
//		wis.close();
//		
//		}catch (FileNotFoundException e) {
//		       e.printStackTrace();
//		     } catch (IOException e) {
//			
//			e.printStackTrace();
//		}
		
		
//		WRITE(15,154) 1,J+23,0.,0.,-0.25-REAL(J-1)*0.01,0.,0.,-.01,0.001
//		WRITE(15,154) 2,2,0.,0.,-.01,0.,0.,0.01,0.001
//		WRITE(15,154) 2,J+23,0.,0.,0.01,0.,0.,0.25+REAL(J-1)*0.01,0.001
//
//		WRITE(15,153)'GE 0'
//		WRITE(15,153)'EX 0,2,1,0,0.5,0'
//		WRITE(15,153)'EX 0,2,2,0,0.5,0'
//		WRITE(15,153)'RP 0,361,1,1010, 0., 0., 0.5, 0.'
//		WRITE(15,153)'EN'
		
//		this.createNecInputFile(x1,x2);
//		this.runNec();
//		return this.getGainFromOutputFile();
//	}
		
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
	
}
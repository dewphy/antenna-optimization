package benchmark;
//Class contains the set parameters for benchmarks 1 through 4

public class Constants {
	
	public static final int[] DECIMAL_PLACES={10, 100};
	public static final int DECIMAL_PLACES_5=1000;

	public static float RECENCY_SELECTION_RATE=0.1f;
	
	public static final float[] UPPER_BOUND_1={90f, 3f};
	public static final float[] LOWER_BOUND_1={0f, 0.5f};
	public static final float[] STEP_1={(float) (0.5), (float) 0.02 };
	
	
	public static final float[] UPPER_BOUND_2={180f,15f};
	public static final float[] LOWER_BOUND_2={0f,5f};
	public static final float[] STEP_2={(float) 0.5, (float) (0.05)};
	
	public static final float[] UPPER_BOUND_3={180f, 4f };
	public static final float[] LOWER_BOUND_3={(float) 0, (float) 0};
	public static final float[] STEP_3={(float) 0.5, (float) (0.01)};
	
	public static final float[] UPPER_BOUND_4={90f, 1.5f};
	public static final float[] LOWER_BOUND_4={10f, 0.5f};
	public static final float[] STEP_4={(float) 1, (float) (0.05)};
	
	public static final float UPPER_BOUND_5=1.5f;
	public static final float LOWER_BOUND_5=0.5f;
	public static final float STEP_5=0.01f;
	
	public static String PATH_BENCH_1="data/directivity-b1.txt";
	public static String PATH_BENCH_2="data/directivity-b2.txt";
	public static String PATH_BENCH_3="data/directivity-b3.txt";
	public static String PATH_BENCH_4="data/directivity-b4.txt";
	public static String PATH_BENCH_5="data/directivity-b5_2.txt";
	
	public static float MAX_FITNESS_1=3.2063f;//2.9512 at 63 2.24;3.1696 at 90 1.26
	//public static float[] FITNESS_LOCAL_1;
	
	public static float MAX_FITNESS_2=18.2810f; 
	public static float[] FITNESS_LOCAL_2={18.03f, 17.824f, 17.66f, 17.539f, 17.458f, 17.378f, 17.298f, 17.258f, 17.179f}; //at l=5.9f; theta=90; 6.9f; 7.9f; 8.9f; 9.9f; 10.9f; 11.9f; 12.9f; 13.9f; 14.9f
	
	public static float MAX_FITNESS_3=7.0632f;// at 90; 0.5, 1.5, 2.5, 3.5
	public static float MAX_FITNESS_4=5.8210f;// at theta=41; l=1.5
	public static float MAX_FITNESS_5=13.1826f;//at d=0.9f
	public static float MAX_FITNESS_5_2=13.1826f;
	public static float MAX_FITNESS_6=25.0035f;// at d=0.9f
	
	public static float[][] BEST_POSITION_1={{35.5f,2.56f}};
	public static float[][] BEST_POSITION_2={{90f,5.9f},{90f,6.9f},{90f,7.9f},{90f,8.9f},{90f,9.9f},{90f,10.9f},{90f,11.9f},{90f,12.9f},{90f,13.9f},{90f,14.9f}};
	
	//public static float[][] LOCAL_POSITION_2={{90f,6.9f},{90f,7.9f},{90f,8.9f},{90f,9.9f},{90f,10.9f},{90f,11.9f},{90f,12.9f},{90f,13.9f},{90f,14.9f}};
	
	public static float[][] BEST_POSITION_3={{90f,0.5f}, {90f,1.5f}, {90f,2.5f}, {90f,3.5f}};
	public static float[][] BEST_POSITION_4={{41,1.5f}};
	public static float[][] BEST_POSITION_5_2={{0.99f,0.99f,0.99f,0.99f}};
	public static float[][] BEST_POSITION_5={{0.99f,0.99f,0.99f,0.99f,0.99f,0.99f}};
	public static float[][] BEST_POSITION_6={{0.9f,0.9f,0.9f,0.9f,0.9f,0.9f,0.9f,0.9f,0.9f,0.9f,0.9f,0.9f}};
	public static float ACCURACY=0.0001f;
	
	public Constants(){
	
	}
	
	

}

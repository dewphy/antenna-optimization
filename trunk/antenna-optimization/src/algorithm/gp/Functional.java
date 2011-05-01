package algorithm.gp;

public enum Functional {
//	sin("sin"),
//	cos("cos"),
//	plus("+"),
//	multiply("*");
//	
//	private String value;
//	
//	private Functional(String value){
//		this.value=value;
//	}
	
	PLUS("PLUS"), MINUS("MINUS"), TIMES("TIMES"), SIN("SIN"), COS("COS");
	
	private String value;
	
	private Functional(String value){
		this.value=value;
	}
	
	public String getValue() {
		   return value;
	}
	
	float eval(float x, float y){
        switch(this) {
            case PLUS:   return x + y;
            case MINUS:  return x - y;
            case TIMES:  return x * y;
            case SIN: 	 return -1;
            case COS: 	 return -1;
        }
        throw new AssertionError("Unknown operation: " + this);
    }
    
    float eval(float x){
        switch(this) {
            case PLUS:   return x;
            case MINUS:  return x;
            case TIMES:  return x;
            case SIN: 	 return (float) Math.sin(x);
            case COS: 	 return (float) Math.cos(x);
        }
        throw new AssertionError("Unknown operation: " + this);
    }
}

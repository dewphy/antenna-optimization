package algorithm.gp;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import benchmark.Constants;
import benchmark.FitnessEvaluatorImpl;

import algorithm.Algorithm;


//0.01 mutation



public class GP implements Algorithm {
	final float MUTATION_PROBABILITY=0.01f;
	final float RECOMBINATION_PROBABILITY=0.8f;
	final int NUMBER_OF_INDIVIDUALS=500;
	final int PENALTY=2000;
	final int MAX_DEPTH=15;
	float[] NUMBER_RANGE;
	final float TERMINAL_PROB=0.4f;
	private TreeNode[][] individuals;
	private TreeNode[][] individualsTemporary;
	float[] fitnesses;
	float[][] positions;
	float[] fitnessPenalties;
	
	List<float[]> bestPositions=new ArrayList<float[]>();
	
	
	int counter=0;
	

	private FitnessEvaluatorImpl fitnessValues;
	private int benchmarkNumber;
	private int numberOfEvaluations;

	public GP(int benchmarkNumber,int numberOfEvaluations, boolean discrete){
		this.benchmarkNumber=benchmarkNumber;
		this.numberOfEvaluations=numberOfEvaluations;
		fitnessValues=new FitnessEvaluatorImpl(benchmarkNumber,numberOfEvaluations,discrete);
		individuals=new TreeNode[NUMBER_OF_INDIVIDUALS][fitnessValues.getPositionLength()];
		individualsTemporary=new TreeNode[NUMBER_OF_INDIVIDUALS][fitnessValues.getPositionLength()];
		
		fitnesses=new float[individuals.length];
		fitnessPenalties=new float[individuals.length];
		positions=new float[individuals.length][individuals[0].length];
		NUMBER_RANGE=new float[fitnessValues.getPositionLength()];
		switch (benchmarkNumber){
			case 1: NUMBER_RANGE=Constants.UPPER_BOUND_1; break;
			case 2: NUMBER_RANGE=Constants.UPPER_BOUND_2; break;
			case 3: NUMBER_RANGE=Constants.UPPER_BOUND_3; break;
			case 4: NUMBER_RANGE=Constants.UPPER_BOUND_4; break;
			//case 5: numberRange=Constants.UPPER_BOUND_5; break;
			//case 6: numberRange=Constants.UPPER_BOUND_6; break;
		}
		
		initSolutions();
		
		while (fitnessValues.getNumberOfEvaluations()<numberOfEvaluations && !isOptimumFound()){
			
		
			//System.out.println("Number of evaluations: "+ fitnessValues.getNumberOfEvaluations());
			calculateFitness();
			
			float[] pos=new float[positions[0].length];
			for (int j=0;j<positions[0].length; j++){
				pos[j]=positions[getBestIndex()][j];	
			}
			
			bestPositions.add(pos);
			
			createOffspring();

		}

	}

	public void initSolutions(){
		for (int i=0; i<individuals.length; i++){
			for (int j=0; j<individuals[i].length; j++){
				individuals[i][j]=growTree(1,NUMBER_RANGE[j]);
			}
		}
	}
	
	public void calculateFitness(){
		
		for (int i=0; i<individuals.length; i++){
			int j=0;
			boolean validSolution=true;
			while (j<individuals[i].length){

				positions[i][j]=evaluateTreeNode(individuals[i][j]);
			
			if (validSolution){
				
				
				
				if (positions[i][j]<fitnessValues.getLowerBound()[j] || positions[i][j]>fitnessValues.getUpperBound()[j]){
					fitnesses[i]=0;
					fitnessPenalties[i]=0;
					validSolution=false; 
					
				}
				
				
			}
			j++;
			}
			if (validSolution) {
								fitnesses[i]=fitnessValues.evaluateFloat(positions[i]);
								fitnessPenalties[i]=fitnesses[i];
								for (int l=0; l<positions[i].length; l++){
									fitnessPenalties[i]=fitnessPenalties[i]-individuals[i][l].getSize()/PENALTY;
								}
			
			}
		}
		
	}

	public void createOffspring(){
		
		int k=0; 
		Random generator=new Random();
		while (k<individuals.length){
			if (generator.nextFloat()<=MUTATION_PROBABILITY){
				int ind=generator.nextInt(individuals.length);
				int chrNum=generator.nextInt(individuals[ind].length);
				
				int nodeNum=generator.nextInt(individuals[ind][chrNum].getSize())+1;
				individualsTemporary[k][chrNum]=individuals[ind][chrNum].copy();
				
				if (nodeNum==1){
					float value=(float) (evaluateTreeNode(individualsTemporary[k][chrNum])+NUMBER_RANGE[chrNum]*0.05f*generator.nextGaussian());
					individualsTemporary[k][chrNum].setValue(String.valueOf(value));//generator.nextFloat()));
				}
				else {
					
					MutateNodeAtPosition(individualsTemporary[k][chrNum],nodeNum,NUMBER_RANGE[chrNum]);
				}
				int i=0;
				
				while (i<individuals[ind].length){
					if (chrNum!=i) {
						individualsTemporary[k][i]=individuals[ind][i].copy();
					} 
					i++;
				}
				k++;
			}
			if (k<individuals.length && generator.nextFloat()<=RECOMBINATION_PROBABILITY){
					float fitnessSum=0;
					for (int l=0; l<individuals.length;l++){
						fitnessSum=fitnessSum+fitnesses[l];
					}

					float r1=generator.nextFloat();
					float r2=generator.nextFloat();
					int l=0;
					float lower=fitnessPenalties[l]/fitnessSum;

					while (r1>lower && l<fitnessPenalties.length-1){
						l++;
						
						lower+=fitnessPenalties[l]/fitnessSum;
					}
					
					int z=0;
					lower=fitnessPenalties[z]/fitnessSum;
					while (r2>lower && z<fitnessPenalties.length-1){
						z++;
						
						lower+=fitnessPenalties[z]/fitnessSum;
					}

					TreeNode[] parent1=new TreeNode[individuals[0].length];
					TreeNode[] parent2=new TreeNode[individuals[0].length];
				
					for (int j=0; j<individuals[0].length; j++){
						parent1[j]=individuals[l][j].copy();
						parent2[j]=individuals[z][j].copy();
						swapNodes(parent1[j],generator.nextInt(parent1[j].getSize())+1, parent2[j],generator.nextInt(parent2[j].getSize())+1);
						
					}
					individualsTemporary[k]=parent1;
					k++;
					if (k<individualsTemporary.length-1){
						individualsTemporary[k]=parent2;
						k++;
					}
			}
		}
		for (int i=0; i<individuals.length;i++){
			for (int j=0; j<individuals[i].length; j++){
				individuals[i][j]=individualsTemporary[i][j];
			}
		}
		
	}

		public void swapNodes(TreeNode node1, int position1, TreeNode node2, int position2){
			TreeNode newNode1=null;
			TreeNode newNode2=null;
			TreeNode newN_1;
			TreeNode newN_2;
		if (node1.getSize()==1 || position1==1){
			newN_1=node1;
			newNode1=node1;
		}
		else{
			counter=0;	

			newN_1=findNodeAtPosition(node1, position1);

			newNode1=node1.getParentNode(newN_1);
			

		}
		
		if (node2.getSize()==1 || position2==1){
			newN_2=node2;
			newNode2=node2;
		}
		else{
			counter=0;	
			newN_2=findNodeAtPosition(node2, position2);
			newNode2=node2.getParentNode(newN_2);
		}

			TreeNode temp=newN_1.copy();
			if (newNode1.isLeftNode(newN_1)){
				newNode1.setLeft(newN_2);
			}
			
			else if (newNode1.isRightNode(newN_1)){
				newNode1.setRight(newN_2);
			}
			else newN_1=newN_2;
			
			if (newNode2.isLeftNode(newN_2)){
				newNode2.setLeft(temp);
			}
			
			else if (newNode2.isRightNode(newN_2)){
				newNode2.setRight(temp);
			}
			else newN_2=temp;
			
		}
	
	public void MutateNodeAtPosition(TreeNode node, int position, float range){

		counter=0;
		TreeNode newNode;

		Random generator=new Random();
		float probOperator=generator.nextFloat();
		float perturbValue=(float) (range*0.05f*generator.nextGaussian());
		
		newNode=findNodeAtPosition(node,position);
		
		if (newNode.getSize()==1){
			float value=evaluateTreeNode(newNode)+perturbValue;
			newNode.setValue(String.valueOf(value));
		}
		else{
			
			float lower=0;
			int counter2=0;
			String value="";
			for (Functional functions : Functional.values()){
				if (probOperator<lower || probOperator>lower+0.2f){
					lower+=0.2f; counter2++;
				}else {value=functions.name(); break;}//System.out.println("Value assigned: "+ value+"\n probOperator: "+probOperator);break;}
			}
			
			newNode.setValue(String.valueOf(value));
			
			switch (counter2){
			case 0:
			case 1:
			case 2: if (newNode.getRight()==null){
							newNode.setRight(growTree(1,range));
							
						}
					if (newNode.getLeft()==null){
							newNode.setLeft(growTree(1,range));
							
						}
						break;
			case 3:
			case 4: if (newNode.getRight()!=null){
						
						
						TreeNode temp=new TreeNode(newNode.getValue());
						temp.setLeft(newNode.getLeft());
						newNode=temp;
						
						
					}	
					if (newNode.getLeft()==null){
						newNode.setLeft(growTree(1,range));
						
					}	
					break;
			
			}
			
		}
		

		
	}


	public TreeNode findNodeAtPosition(TreeNode node, int position){

		if (node!=null){
			
			counter++;
			if (counter == position){
				return node;
			} else {
				TreeNode foundNode = findNodeAtPosition(node.getLeft(), position);
				if (foundNode !=null) {
					return foundNode;
				}

				foundNode = findNodeAtPosition(node.getRight(), position);
				if (foundNode !=null) {
					return foundNode;
				}
			}
		}
		return null;
	}

	public void printGeneration(){
		for (int i=0; i<individuals.length;i++){
			for (int j=0; j<individuals[i].length;j++){
				System.out.println("\nIndividual #"+i+", Chromosome # "+j+ ", \nNode: " );
				individuals[i][j].printNode();
				System.out.println();
				
			}
		}
	}
	public float evaluateTreeNode(TreeNode node){
		float x=-15;
		boolean found=false;
		for (Functional functions : Functional.values()){
			if (node.getValue().equals(functions.name())){
				switch (Functional.valueOf(node.getValue())){
				case SIN:
				case COS:  	x=Functional.valueOf(node.getValue()).eval(evaluateTreeNode(node.getLeft()));
				break;
				case PLUS:
				case MINUS:
				case TIMES: x=Functional.valueOf(node.getValue()).eval(evaluateTreeNode(node.getLeft()),evaluateTreeNode(node.getRight()));
				break;

				default: 	x=-15;	
				}
				found=true;
				break;
			}
		}
		if (!found) {x=Float.parseFloat(node.getValue());}

		return x;
	}

	public TreeNode growTree(int count,float range){
		Random generator=new Random();
		TreeNode node;

		if (generator.nextFloat()>TERMINAL_PROB && count<MAX_DEPTH){//Functional
			float probOperator=generator.nextFloat();
			float lower=0;
			String value="";
			for (Functional functions : Functional.values()){
				if (probOperator<lower || probOperator>lower+0.2f){
					lower+=0.2f;
				}else {value=functions.name(); break;}//System.out.println("Value assigned: "+ value+"\n probOperator: "+probOperator);break;}
			}

			node=new TreeNode(value);
			count++;
			
			node.setLeft(growTree(count,range));
			if (node.getValue().equals("SIN") || node.getValue().equals("COS"))
			{count--;} 
			else { node.setRight(growTree(count,range));}

		}
		else{
			double value= (range*generator.nextFloat());
			node=new TreeNode(String.valueOf(value));
			
			count--;
			
		}

		return node;
	}
	
	public float[] getBestPosition(){
		
			return positions[getBestIndex()];
	
	}
	public float getBestFitness(){
		return fitnesses[getBestIndex()];
		
	}
	
	public int getBestIndex(){
		float max=0;
		int maxIndex=0;
		for (int i=0; i<positions.length; i++){
			if (fitnesses[i]>max){
				max=fitnesses[i];
				maxIndex=i;
			}
		}
		return maxIndex;
	}
	
	public float[] getBestFitnesses(){
//		//System.out.println("Size of positions: "+ bestPositions.size());
//		float[] bestFitnesses=new float[bestPositions.size()];
//		for (int i=0; i<bestPositions.size();i++){
//			for (int j=0; j<positions[0].length; j++){
//				if (bestPositions.get(i)[j]<fitnessValues.getLowerBound()[j] || bestPositions.get(i)[j]>fitnessValues.getUpperBound()[j]){
//					bestFitnesses[i]=0;
//				}
//				else {bestFitnesses[i]=fitnessValues.evaluate(bestPositions.get(i));}
//			}
//		}
//		return bestFitnesses;
		return fitnessValues.getBestFitnesses();
	}
	
	public float[][] getBestPositions(){
//		float[][] temp=new float[bestPositions.size()][individuals[0].length];
//		for (int i=0; i<bestPositions.size();i++){
//			for (int j=0; j<individuals[0].length; j++){
//				temp[i][j]=bestPositions.get(i)[j];
//			}
//		}
//		return temp;
		return fitnessValues.getBestPositions();
	}
	
	public boolean isOptimumFound(){
		return fitnessValues.isOptimumFound();
	}

	public int getNumberOfEvaluations(){
		return fitnessValues.getNumberOfEvaluations();
	}
}


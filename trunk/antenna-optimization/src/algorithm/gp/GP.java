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
		fitnessValues=new FitnessEvaluatorImpl(benchmarkNumber,discrete);
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
//		calculateFitness();
//		createOffspring();
//		printGeneration();
//		
//		calculateFitness();
//		createOffspring();
//		printGeneration();
//		printGeneration();
		
		while (fitnessValues.getNumberOfEvaluations()<numberOfEvaluations && !isOptimumFound()){
			
		
			//System.out.println("Number of evaluations: "+ fitnessValues.getNumberOfEvaluations());
			calculateFitness();
			
			float[] pos=new float[positions[0].length];
			for (int j=0;j<positions[0].length; j++){
				pos[j]=positions[getBestIndex()][j];	
			}
			
			bestPositions.add(pos);
			
			createOffspring();
//			
////////			printGeneration();
		}
//////		printGeneration();
		float max=0;
		for (int i=0; i<bestPositions.size();i++){
			if (getBestFitnesses()[i]>max){
				max=getBestFitnesses()[i];
			}
		}
		System.out.println("Absolute Best: "+ max);
		
		System.out.println("Best Fitness: "+ getBestFitness());
		System.out.println("Best Position: "+ getBestPosition()[0]+" "+getBestPosition()[1]);
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
//				individuals[i][j].printNode();
//				System.out.println();
				positions[i][j]=evaluateTreeNode(individuals[i][j]);
			
			if (validSolution){
				
				//System.out.println("Node Values at : "+ j+" "+ positions[j]);
				
				if (positions[i][j]<fitnessValues.getLowerBound()[j] || positions[i][j]>fitnessValues.getUpperBound()[j]){
					fitnesses[i]=0;
					fitnessPenalties[i]=0;
					validSolution=false; 
					//System.out.println("Fitness of individual "+ i+ ": "+fitnesses[i]);
				}
				
				
			}
			j++;
			}
			if (validSolution) {//positions[i][0]=(float) (positions[i][0]/Math.PI*180);
								fitnesses[i]=fitnessValues.evaluate(positions[i]);
								fitnessPenalties[i]=fitnesses[i];
								for (int l=0; l<positions[i].length; l++){
									fitnessPenalties[i]=fitnessPenalties[i]-individuals[i][l].getSize()/PENALTY;
								}
			//System.out.println("Fitness of individual "+ i+ ": "+fitnesses[i]);
			}
		}
		//System.out.println("Number of zero fitness ind "+ c);
	}

	public void createOffspring(){
		
		int k=0; 
		Random generator=new Random();
		while (k<individuals.length){
			if (generator.nextFloat()<=MUTATION_PROBABILITY){
				int ind=generator.nextInt(individuals.length);
				int chrNum=generator.nextInt(individuals[ind].length);
				//System.out.println("In Program size: "+ individuals[ind][chrNum].getSize());
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
						//if (l==fitnessPenalties.length) {break;}
						lower+=fitnessPenalties[l]/fitnessSum;
					}
					//System.out.println("R1: "+ r1+" index: "+l);
					int z=0;
					lower=fitnessPenalties[z]/fitnessSum;
					while (r2>lower && z<fitnessPenalties.length-1){
						z++;
						//if (z==fitnessPenalties.length) {break;}
						lower+=fitnessPenalties[z]/fitnessSum;
					}
//					if (z==0){z++;};
//					if (l==0){l++;};
					//System.out.println("R2: "+ r2+" index: "+z);
					TreeNode[] parent1=new TreeNode[individuals[0].length];
					TreeNode[] parent2=new TreeNode[individuals[0].length];
//					
//					
					for (int j=0; j<individuals[0].length; j++){
						parent1[j]=individuals[l][j].copy();
						parent2[j]=individuals[z][j].copy();
						swapNodes(parent1[j],generator.nextInt(parent1[j].getSize())+1, parent2[j],generator.nextInt(parent2[j].getSize())+1);
						//System.out.println("Nodes Swapped");
					}
					individualsTemporary[k]=parent1;
					k++;
					if (k<individualsTemporary.length-1){
						individualsTemporary[k]=parent2;
						k++;
					}
					
//					TreeNode[] parent1=new TreeNode[individuals[0].length];
//					TreeNode[] parent2=new TreeNode[individuals[0].length];
//					int chrNumRec=generator.nextInt(individuals[0].length);
//					parent1[chrNumRec]=individuals[l][chrNumRec].copy();
//					parent2[chrNumRec]=individuals[z][chrNumRec].copy();
//					
//					swapNodes(parent1[chrNumRec],generator.nextInt(parent1[chrNumRec].getSize())+1, parent2[chrNumRec],generator.nextInt(parent2[chrNumRec].getSize())+1);
//					float distance1=0;
//					float distance2=0;
//					float distance=0;
//					for (int j=0; j<individuals[0].length; j++){
//						if (j!=chrNumRec){
//							parent1[j]=individuals[l][j].copy();
//							parent2[j]=individuals[z][j].copy();
//						}
////						distance1=(float) (distance1+Math.pow((evaluateTreeNode(parent1[j])-evaluateTreeNode(individuals[l-1][j])),2));
////						distance2=(float) (distance2+Math.pow(evaluateTreeNode(parent2[j])-evaluateTreeNode(individuals[z-1][j]),2));
////						distance=(float) (distance+Math.pow((fitnessValues.getUpperBound()[j]-fitnessValues.getLowerBound()[j]),2));
//					}
//					distance1=Math.abs(evaluateTreeNode(parent1[chrNumRec])-evaluateTreeNode(individuals[l][chrNumRec]));
//					distance2=Math.abs(evaluateTreeNode(parent2[chrNumRec])-evaluateTreeNode(individuals[z][chrNumRec]));
//					distance=Math.abs(fitnessValues.getUpperBound()[chrNumRec]-fitnessValues.getLowerBound()[chrNumRec]);
//					if (distance1<=0.2*distance && distance2<=0.2*distance){
//						individualsTemporary[k]=parent1;
//					
//						if (k<individualsTemporary.length-1){
//							individualsTemporary[k+1]=parent2;
//						}
//					k=k+2;
//					}
//					else if (distance1<=0.2*distance){	
//						individualsTemporary[k]=parent1; k++;
//					}else if (distance2<=0.2*distance){
//						individualsTemporary[k]=parent2;k++;
//					}
					
					
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
		//	System.out.println("recombination ");
		//System.out.println("Position1: "+ position1 +" Position 2: "+position2);
		if (node1.getSize()==1 || position1==1){
			newN_1=node1;
			newNode1=node1;
		}
		else{
			counter=0;	
//			System.out.println("Initial node: ");
//			node1.printNode();
//			System.out.println("Found node at position "+ position1 +" :");
			newN_1=findNodeAtPosition(node1, position1);
//			newN_1.printNode();
			newNode1=node1.getParentNode(newN_1);
			
//			System.out.println("Final node: ");
//			newNode1.printNode();
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
		//System.out.println("\nNode 1 size: " + newNode1.getSize());
			//System.out.println("\nNode 1 to swap: ");
			//newNode1.printNode();
			//System.out.println("\nNode 2 size: " + newNode2.getSize());
			//System.out.println("\nNode 2 to swap: ");
			//newNode2.printNode();
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
//		System.out.println("mutation ");
//		System.out.println("Node for Mutation: ");
//		node.printNode();
//		System.out.println("\nAt position: "+position);
//		System.out.println("Node size "+node.getSize());
		counter=0;
		TreeNode newNode;
//		if (position==1){
//			newNode=growTree(1,range);
//		}
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
			//System.out.println("Counter: "+ counter2 + " Value: "+ String.valueOf(value));
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
						//System.out.println("Mutation: growing left node");
					}	
					break;
			
			}
			
		}
		
//		if (newNode.getLeft()!=null){
//			newNode.setLeft(growTree(1,range));
//		}
//		else if (newNode.getRight()!=null){
//			newNode.setRight(growTree(1,range));
//		}
		
	}


	public TreeNode findNodeAtPosition(TreeNode node, int position){

		if (node!=null){
			//System.out.println("Counter: " + counter + ", Position: " + position);
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
				//System.out.println("Size: "+ individuals[i][j].getSize());
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
			//System.out.println("Count Functional Left: "+ count);
			node.setLeft(growTree(count,range));
			if (node.getValue().equals("SIN") || node.getValue().equals("COS"))
			{count--;} //System.out.println("Count Functional Right SIN/COS: "+ count);}
			else { node.setRight(growTree(count,range));}//System.out.println("Count Functional Right: "+ count);

		}
		else{//Terminal
			double value= (range*generator.nextFloat());
			node=new TreeNode(String.valueOf(value));
			//System.out.println("Value assigned: "+ value);
			count--;
			//System.out.println("Count Terminal: "+ count);
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
		//System.out.println("Size of positions: "+ bestPositions.size());
		float[] bestFitnesses=new float[bestPositions.size()];
		for (int i=0; i<bestPositions.size();i++){
			for (int j=0; j<positions[0].length; j++){
				if (bestPositions.get(i)[j]<fitnessValues.getLowerBound()[j] || bestPositions.get(i)[j]>fitnessValues.getUpperBound()[j]){
					bestFitnesses[i]=0;
				}
				else {bestFitnesses[i]=fitnessValues.evaluate(bestPositions.get(i));}
			}
		}
		return bestFitnesses;
	}
	
	public float[][] getBestPositions(){
		float[][] temp=new float[bestPositions.size()][individuals[0].length];
		for (int i=0; i<bestPositions.size();i++){
			for (int j=0; j<individuals[0].length; j++){
				temp[i][j]=bestPositions.get(i)[j];
			}
		}
		return temp;
	}
	
	public boolean isOptimumFound(){
		return fitnessValues.isOptimumFound();
	}

	public int getNumberOfEvaluations(){
		return fitnessValues.getNumberOfEvaluations();
	}
}


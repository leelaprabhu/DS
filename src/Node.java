import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
public class Node{ 
	private int nodeNumber;
	private int nodeID;
	private float energy;
	private Coord coord;
	private boolean leader;
	private Controller controller;
	private BaseStation basestation;
	private Edge near;
	private Node bridge;
	private Edge sendEdge;
	private ArrayList<Edge> neighbours=new ArrayList<Edge>();
	private ArrayList<Edge> tree=new ArrayList<Edge>();
	private int treeNo;
	private int thresholdEnergy;
	PrintWriter writer;
	static Hashtable<Node, Integer> leaders= new Hashtable<Node, Integer>();
	static Hashtable<Integer, Integer> nodesChecked = new Hashtable<Integer, Integer>();
	static Hashtable<Integer, Integer> nodesChecked2 = new Hashtable<Integer, Integer>();
	static Hashtable<Integer, Integer> nodesChecked3 = new Hashtable<Integer, Integer>();
	static Hashtable<Integer, Integer> nodesChecked4 = new Hashtable<Integer, Integer>();
	static Hashtable<Integer, Integer> nodesChecked5 = new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> neighboursMap = new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> treeMap = new Hashtable<Integer, Integer>();
	
	public Node(int num, int node_id, Coord co, float e, int t){		//constructor for Node object
		this.nodeNumber= num;
		this.nodeID= node_id;
		this.energy= e;
		this.coord= co;
		this.leader= true;
		this.bridge= this;
		this.thresholdEnergy=t;
	}
	public Coord getCoord(){
		return this.coord;
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public void setCtrl(Controller ctrl){
		this.controller= ctrl;
	}
	
	public void setBS(BaseStation bs){
		this.basestation= bs;
	}
	
	public void discover(){ //Broadcasts discover messages and gets a reply from the neighbours with their distances
		this.neighbours=controller.getNeighbours(nodeNumber);
	}
	
	public void sort(){	//Sorts all edges according to distance so finding the nearest is easy
		Edge tempNode;
		for (int i=0; i <neighbours.size()-1; i++){
			for (int j=0; j < neighbours.size()-1-i; j++){
				if (neighbours.get(j).getDist() > neighbours.get(j+1).getDist()){
					tempNode= neighbours.get(j);
					neighbours.set(j,neighbours.get(j+1));
					neighbours.set(j+1, tempNode);
				}
			}
		}
	}
	
	public void addToTree(Node n, float f){	//Adds an edge with Destination Node n and edge length f
		Edge e= new Edge(n,f);
		if (!has1(tree, e)){		//Checks if it is already there, if no then adds
			tree.add(e);
			//treeMap.put(arg0, arg1)
		}	
	}
	
	public float energyCost(float dist){
		float energy= (float) (dist*1.2); //Finds the energy using the edge length as the parameter
		return energy;
	}
	
	public boolean has1(ArrayList<Edge> eList, Edge e){ //checks if an edge is present in an ArrayList of Edges
		for (int i=0; i<eList.size(); i++){
			if (eList.get(i).getDest()==e.getDest()){
				return true;
			}
		}
		return false;
	}
	
	public void setTreeNo(int treen){ // merging for the initial phase
		this.treeNo= treen;
	}
	
	public int getTreeNo(){
		return this.treeNo;
	}
	
	public boolean isLeader(){
		return leader;
	}
	
	public void merge() throws IOException{ 								//Merging for the later phases
			Node nearNode;
			nearNode=near.getDest();
			if (!has1(tree, near)){											//If the edge is not already present in the tree
				controller.neighbour(1, nearNode, this, near.getDist());	//Ask the other node to add you
				tree.add(near);												//Add the other node
				File log= new File("log.txt"); 								//print to log file
				FileWriter fileWriter = new FileWriter(log, true);			
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				System.out.println("added "+near.getDest().getNodeID()+"-"+this.nodeID);
				bufferedWriter.write("added "+near.getDest().getNodeID()+"-"+this.nodeID);
				System.out.println();
				bufferedWriter.newLine();
				bufferedWriter.close();
			}	
			
			if (treeNo>near.getDest().getTreeNo()){				//Leader election, the node with the higher ID is elected
				leader= true;					
				leaders.put(this,1);
				nodesChecked5.clear();						
				nodesChecked5.put(this.nodeID,1);				
				controller.neighbour(1, near.getDest(), this.nodeID);	//Tell all other nodes in the tree who the Leader is
			}
			else{
				leader = false;
				nodesChecked5.clear();
			}
		basestation.done2();
	}
	
	public void mergeCommon() throws IOException{
		
	}
	
	public void merge2() throws IOException{	//Merging the later part of the MST
		if (this.isLeader()==true){				//If the node is a leader
			Node nearNode;
			nearNode=near.getDest();
			if (!has1(bridge.tree, near)){		//If the edge hasn't already been added
				controller.neighbour(1, nearNode, bridge, near.getDist()); //Ask the nearest node to add bridge
				controller.neighbour(1, bridge, nearNode, near.getDist());			//Add the nearest node to bridge's tree.!!!!!!!!!! 
				File log= new File("log.txt");	//Write to a log File
				FileWriter fileWriter = new FileWriter(log, true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				System.out.println("added "+near.getDest().getNodeID()+"-"+bridge.nodeID);
				bufferedWriter.write("added "+near.getDest().getNodeID()+"-"+bridge.nodeID);
				bufferedWriter.newLine();
				bufferedWriter.close();
			}
				if (nodeID>near.getDest().getNodeID()){ //Leader Election
					leader= true;
					leaders.put(this,1);
					nodesChecked5.clear();
					nodesChecked5.put(this.nodeID,1);
					controller.neighbour(1, near.getDest(), this.nodeID); //Tell all other nodes in the tree who the leader is
				}
				else{
					leader = false;
					nodesChecked5.clear();
				}
		}
		basestation.done2();	//When done, tell the Base Station and wait for others to complete
	}
	
	public Edge getNearest(){
		return near;
	}
	
	public Node getBridge(){
		return bridge;
	}
	
	public Edge setNearest() throws IOException{		//Find the nearest node, that is the first node in the neighbours list of a different tree.
		bridge= this;
		nodesChecked2.put(this.nodeID, 1);
		float min;
		int l=0;
		do{
			l++;
			if (l>neighbours.size()){
				l=neighbours.size();
				break;
			}
		}while (controller.neighbour(neighbours.get(l-1).getDest())== treeNo && (l<=neighbours.size()));
		if(l==neighbours.size()){
			return null;
		}
		near= neighbours.get(l-1); // First node not in the same tree
		min= neighbours.get(l-1).getDist();
		int count=0;
		float p;
		Node nearNode=neighbours.get(l-1).getDest();
		for (int i=0; i < tree.size(); i++){ //Traverse the tree and find the minimum
			if (!(nodesChecked2.containsKey(tree.get(i).getDest().getNodeID()))){
				nodesChecked2.put(tree.get(i).getDest().getNodeID(),1);
				p= tree.get(i).getDest().setNearest().getDist();
				if (min>p){ //If minimum then swap
					min= p;
					nearNode= tree.get(i).getDest().getNearest().getDest();
					Edge tempEdge1 =new Edge(nearNode, min);
					near= tempEdge1;
					bridge= tree.get(i).getDest().getBridge(); //bridge is the node in the tree that connects to the nearest.
				}
				count=1;
			}
			if (count==0){
				return neighbours.get(l-1);
			}
			
		}
		Edge tempEdge= new Edge(nearNode, min);
		return (tempEdge);
	}
	
	public void mapNeighbours(){	//map neighbours to a Hash Table to access them quickly
		for (int i=0; i <neighbours.size(); i++){
			neighboursMap.put(neighbours.get(i).getDest().getNodeID(), i);
			
		}
	}
	
	public void startMST() throws IOException{ //Start an MST
		sort(); //Sort all edges
		treeNo= nodeID;
		mapNeighbours();
		if (neighbours.size()==0){ //If isolated node, terminate.
			leader=false;
			//controller.neighbour(this.treeNo);
			terminateTree(this.treeNo);
		}
		else{
			near=neighbours.get(0);
		}
		basestation.done(); //Tell basestation you are done and wait for the others
	}
	
	public void setTree(int tree1){ 
		nodesChecked.put(nodeID, 1);
		this.treeNo=tree1;
		for (int i=0; i < tree.size(); i++){
			if (!(nodesChecked.containsKey(tree.get(i).getDest().getNodeID()))){
				nodesChecked.put(tree.get(i).getDest().getNodeID(),1);
				controller.neighbour(2, tree.get(i).getDest(), tree1);
			}
		}
	}
	
	public void setLeaderFalse(int tree1){ //Tell everyone in your new tree that you are the leader.
		nodesChecked5.put(nodeID, 1);
		this.leader=false;
		this.treeNo=tree1;
		leaders.remove(this);
		for (int i=0; i < tree.size(); i++){
			if (!(nodesChecked5.containsKey(tree.get(i).getDest().getNodeID()))){
				nodesChecked5.put(tree.get(i).getDest().getNodeID(),1);
				controller.neighbour(1, tree.get(i).getDest(), tree1);
			}
		}
	}
	
	public void terminateTree(int tree1){ //When MST formation is done. Tell all nodes.
		nodesChecked3.put(nodeID, 1);
		basestation.terminate(this);
		for (int i=0; i < tree.size(); i++){
			if (!(nodesChecked3.containsKey(tree.get(i).getDest().getNodeID()))){
				nodesChecked3.put(tree.get(i).getDest().getNodeID(),1);
				//controller.neighbour(this.treeNo);
				controller.neighbour(3, tree.get(i).getDest(), tree1);
			}
		}
	}
		
	public void bcst() throws IOException{ //Start Broadcast
		nodesChecked4.clear();
		broadcast(); //Call a tree traversal to broadcast
	}
	
	public void failBcst(ArrayList<Edge> newTree){ //If a node dies, add it's tree to the parent's tree.
		Edge tempEdge;
		float tempDist=0;
		if (energy>thresholdEnergy){ //Only if it's own energy is above threshold
			for (int i=0; i< newTree.size(); i++){ 	// this part changes the distances to that from the new sender.
				if (newTree.get(i).getDest().getNodeID()!= nodeID){
					tempDist=neighbours.get(neighboursMap.get(newTree.get(i).getDest().getNodeID())).getDist();
				}	
				if (tempDist>0){
				tempEdge= new Edge(newTree.get(i).getDest(), tempDist);
				if(!has1(tree, tempEdge))
					tree.add(tempEdge);
				}
			}
		}
	}
	
	public void broadcast() throws IOException{ //Traverse the tree to broadcast
		nodesChecked4.put(nodeID, 1);	//Put node in a checking list
		for (int i=0; i < tree.size(); i++){ //Iterate over all it's children
			if ((!(nodesChecked4.containsKey(tree.get(i).getDest().getNodeID())))&&(tree.get(i).getDest().energy>=thresholdEnergy)&&(energy>=thresholdEnergy)){
				nodesChecked4.put(tree.get(i).getDest().getNodeID(),1);
				this.energy= this.energy- (float)(1.2*tree.get(i).getDist());
				File log= new File("log.txt");
				FileWriter fileWriter = new FileWriter(log, true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write("data from "+nodeID+" to "+tree.get(i).getDest().getNodeID()+", "+"energy:"+this.energy);
				bufferedWriter.newLine();
				bufferedWriter.close();
				System.out.println("data from "+nodeID+" to "+tree.get(i).getDest().getNodeID()+", "+"energy:"+this.energy);
				controller.neighbour(2, tree.get(i).getDest()); //Recursively call the broadcast on it's children
				if (this.energy<thresholdEnergy){
					File log2= new File("log.txt");
					FileWriter fileWriter2 = new FileWriter(log2, true);
					BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
					bufferedWriter2.write("node down "+nodeID);
					bufferedWriter2.newLine();
					bufferedWriter2.close();
					for(int l=0; l<tree.size(); l++){
						controller.neighbour(1, tree.get(l).getDest(), this.tree);
					}
					break;
				}
				bufferedWriter.close();
			}
		}
	}
	
	public void levelMST() throws IOException{ //MST levels after starting
		if (this.isLeader()==true){
			nodesChecked.clear();
			near= setNearest();
			nodesChecked2.clear();
			if (near==null){
				leader=false;
				terminateTree(this.treeNo);
			}
		}
		basestation.done3();			
	}
}
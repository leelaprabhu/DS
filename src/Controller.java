import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.*;

public class Controller{
	private int numNodes;
	private ArrayList<Node> list;
	PrintWriter writer;
	
	public Controller(int n, ArrayList<Node> list1){
		this.numNodes=n;
		this.list=list1;
		this.writer= writer;
	}
	private ArrayList<ArrayList<Edge>> edges=new ArrayList<ArrayList<Edge>>();
	public void initGraph(){
		float dist;
		Node node1, node2;
		Coord coord1, coord2;
		Edge tempEdge1, tempEdge2;
		for (int i=0; i< numNodes; i++){
			edges.add(new ArrayList<Edge>());
		}
		for (int i=0; i< numNodes-1; i++){
			for (int j=i+1; j< numNodes; j++){
				node1= list.get(i);
				coord1= node1.getCoord();
				node2= list.get(j);
				coord2= node2.getCoord();
				dist= (float)Math.sqrt(Math.pow((coord1.getX()- coord2.getX()),2)+Math.pow((coord1.getY()- coord2.getY()), 2));
				if (dist<=10){
					tempEdge1= new Edge(node1, dist);
					tempEdge2= new Edge(node2, dist);
					edges.get(i).add(tempEdge2);
					edges.get(j).add(tempEdge1);
				}
			}
		}
	}
	
	public void neighbour(int opt, Node node1, Node node2, float dist){
		if (opt==1)
			node1.addToTree(node2, dist);
	}
	
	public void neighbour(int opt, Node node1, int treeNo){
		if (opt==1)
			node1.setLeaderFalse(treeNo);
		else if (opt==2)
			node1.setTree(treeNo);
		else if (opt==3)
			node1.terminateTree(treeNo);
	}
	
	public void neighbour(int opt, Node node1) throws IOException{
		//if (opt==1)
			//node1.setLeaderFalse();
		if (opt==2)
			node1.broadcast();
	}
	
	public int neighbour(Node node1) throws IOException{
		return node1.getTreeNo();
	}
	
	public void neighbour(int opt, Node node1, ArrayList<Edge> tree){
		if (opt==1)
			node1.failBcst(tree);
	}
	
	public ArrayList<Node> getListAgain(){
		return list;
	}
	
	public ArrayList<Edge> getNeighbours(int k){
		return edges.get(k);
	} 
}
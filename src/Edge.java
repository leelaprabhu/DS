public class Edge{
	private Node destination;
	private float distance;
	private boolean sameTree=false;
	
	public Edge(Node dest, float dist){
		this.destination=dest;
		this.distance=dist;
	}
	public Node getDest(){
		return destination;
	}
	
	public float getDist(){
		return distance;
	}
	
	public boolean getSameTree(){
		return sameTree;
	}
	
	public void setSameTree(boolean torf){
		this.sameTree= torf;
	}
}
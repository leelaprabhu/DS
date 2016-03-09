import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

public class BaseStation{
	private ArrayList<Node> nodeList=new ArrayList<Node>();
	private static ArrayList<Integer> bcstList=new ArrayList<Integer>();
	private static ArrayList<Node> toRemove=new ArrayList<Node>();
	private static ArrayList<Node> leadersList=new ArrayList<Node>();
	Hashtable<Integer, Node> list1=new Hashtable<Integer, Node>();
	private int completed=0;
	private int completed2=0;
	PrintWriter writer;
	public BaseStation(ArrayList<Node> nlist, ArrayList<Integer> blist) throws IOException{
		nodeList= nlist;
		for(int k=0; k < nodeList.size(); k++){
			list1.put(nodeList.get(k).getNodeID(), nodeList.get(k));
		}
		File log= new File("log.txt"); //print to log the list of bs
		FileWriter fileWriter = new FileWriter(log, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		this.bcstList= blist;
		System.out.print("bs "+ nodeList.get(0).getNodeID());
		bufferedWriter.write("bs "+ nodeList.get(0).getNodeID());
		for (int i= 1; i < nodeList.size(); i++){
				System.out.print(","+nodeList.get(i).getNodeID());
				bufferedWriter.write(","+nodeList.get(i).getNodeID());
		}
		System.out.println();
		bufferedWriter.newLine();
		bufferedWriter.close();
	}

	
	public void done() throws IOException{ //All nodes come here when they are done with the first stage of MST
			completed++;
			if (completed==nodeList.size()){ //Wait till all nodes complete, then proceed
			completed=0;
			for (int k=0; k < toRemove.size(); k++){
				nodeList.remove(toRemove.get(k)); //Remove any nodes that terminated
				toRemove.remove(k);
				if (nodeList.size()==0){ //If all nodes have terminated broadcast
					startBroadcast(); 
				}
			}
			for (int i= 0; i < nodeList.size(); i++){
					nodeList.get(i).merge(); //If all nodes if have not finished MST, carry on to the merge phase
			}
		}
	}
	
	public void startBroadcast() throws IOException{ //Start broadcast for the nodes in the bcst list
		for (int i= 0; i < bcstList.size(); i++){ 
			list1.get(bcstList.get(i)).bcst(); //Use hash table for fast access
		}
	}
	
	public void done2() throws IOException{ //Nodes finish the merge and come for the next phase
		leadersList.clear();
		completed2++;
		if (completed2==nodeList.size()){ // after all nodes are done
			completed2=0;
			
			for (int i= 0; i < nodeList.size(); i++){
				if (nodeList.get(i).isLeader()==true){
					File log= new File("log.txt");
					FileWriter fileWriter = new FileWriter(log, true);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					System.out.println("elected "+ nodeList.get(i).getNodeID());
					bufferedWriter.write("elected "+ nodeList.get(i).getNodeID()); //print elected nodes
					bufferedWriter.newLine();
					bufferedWriter.close();
					leadersList.add(nodeList.get(i));
				}
			}
			if (leadersList.size()>0){ //broadcast next message to only leaders
				File log= new File("log.txt");
				FileWriter fileWriter = new FileWriter(log, true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				System.out.print("bs ");
				bufferedWriter.write("bs "+ leadersList.get(0).getNodeID());
				for (int i= 1; i < leadersList.size(); i++){
						System.out.print(","+leadersList.get(i).getNodeID());
						bufferedWriter.write(","+leadersList.get(i).getNodeID());
				}
				System.out.println();
				bufferedWriter.newLine();
				bufferedWriter.close();
			}
			for (int i= 0; i < nodeList.size(); i++){
					nodeList.get(i).levelMST(); //Start the next level of MST
			}
		}
	}
	public void done3() throws IOException{ //When a nodes are done with the merging components
		completed++;
		if (completed==nodeList.size()){ //When all nodes have finished
			completed=0;
			for (int k=0; k < toRemove.size(); k++){ //Remove nodes that have terminated
				this.nodeList.remove(toRemove.get(k));
				toRemove.remove(k);
				if (nodeList.size()==0){ //If all nodes have terminated start broadcast
					startBroadcast();
				}
			}
			for (int i= 0; i < nodeList.size(); i++){
					nodeList.get(i).merge2(); //If all nodes have not finished the MST, carry on to the merge component phase
			}
		}
		
	}
	
	public void terminate(Node n){ //When a node is done with MST construction
		toRemove.add(n);
	}
}
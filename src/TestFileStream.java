import java.io.*;
import java.util.ArrayList;

public class TestFileStream{
	private static ArrayList<Integer> bcstSource=new ArrayList<Integer>();
	private static ArrayList<Node> nodeList=new ArrayList<Node>();
	private static int len=0;
	private static Controller ctrl;
	private static BaseStation basestation;
	public static void main(String args[]) throws IOException{
		FileInputStream input= new FileInputStream("input0.txt"); 	//Read the input file
		//File file = new File(args[0]);
		//FileInputStream input= new FileInputStream(file);
		String str1=" ";
		int value;
		while((value= input.read())!= -1){
			char c = (char)value;
			str1=str1+c;
		}
		input.close();
		len=splitInput(str1);										//Split the input to get details
		ctrl= new Controller(len, nodeList);						//Initialize the controller
		sendController();											//Send all node objects the details of the controller
		ctrl.initGraph();											//Tell the controller to initialize the Graph
		discoverAll();												//Tell the nodes to start discovery
		basestation= new BaseStation(nodeList, bcstSource);			//Initialize the Base Station
		sendBaseStation();											//Inform the nodes about the base station
		minSpanningTree();											//Inform the nodes to start MST construction		
	}
	
	public static void discoverAll(){								
		for (int i=0; i < nodeList.size(); i++){					//Call all nodes' discover() 
			nodeList.get(i).discover();
		}
	}
	
	public static void minSpanningTree() throws IOException{
		for (int i=0; i < nodeList.size(); i++){					//Call all nodes' MST()
			nodeList.get(i).startMST();
		}
	}
	
	public static void sendController(){
		for (int i=0; i < nodeList.size(); i++){					//Send the controller to all nodes
			nodeList.get(i).setCtrl(ctrl);
		}
	}
	
	public static void sendBaseStation(){
		for (int i=0; i < nodeList.size(); i++){
			nodeList.get(i).setBS(basestation);						//Send the base station to all nodes
		}
	}
	
	public static int splitInput(String str) throws IOException{
		String[] lines = str.split("\n");
		String thresh= lines[0].trim();
		int threshold= Integer.parseInt(thresh);
		int lenNodes=0;
		Coord tempCoord;
		Node tempNode;
		for (int i=1; i < lines.length; i++){
			String types[]=lines[i].split(" ",2);
			if (types[0].equals("node")){							//process all the node parts and instantiate a Node object
				lenNodes++;
				String nodeDetails[]=types[1].split(",");
				tempCoord= new Coord(Float.parseFloat(nodeDetails[1]), Float.parseFloat(nodeDetails[2]));
				tempNode= new Node(i-1, Integer.parseInt(nodeDetails[0]), tempCoord, Float.parseFloat(nodeDetails[3]), threshold);	
				nodeList.add(tempNode);
			}
			if(types[0].equals("bcst")){							//process all the bcst parts
				String bcstDetails[]=types[1].split(" ");
				bcstSource.add(Integer.parseInt(bcstDetails[1]));
			}
		}
		return (lenNodes);
    }
}
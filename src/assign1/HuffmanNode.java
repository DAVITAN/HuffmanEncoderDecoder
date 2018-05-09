package assign1;
/*
 * Data Compression - Assignment 1 - Huffman Compression
 * Written By:
 * Dor Avitan - 
 * Omer Sirpad - 
 * Omer Amsalem - 
 */
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class HuffmanNode
{
	private final  int _freq;
	private byte _ch;
	private HuffmanNode left;
	private HuffmanNode right;
	
	public HuffmanNode(final int freq, final byte ch)
	{
		_freq = freq;
		_ch = ch;
	}
	
	public int get_freq()
	{
		return _freq;
	}
	
	public byte get_char()
	{
		return _ch;
	}
	public static HuffmanNode BuildTree(int [] arr)
	{
		PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>(arr.length,new HuffmanComperator());
		for (int i = 0; i < arr.length; i++)//Build tree from freq array
		{
			if (arr[i] != 0)//if freq is not 0
			{
				HuffmanNode hn = new HuffmanNode(arr[i],(byte)i);//Add node with freq+char 
				q.add(hn);//add to priority Q
			}
		}
		HuffmanNode root = null;
		while(q.size() >1)
		{
			//Builds the Tree by taking the lowest 2 priorities, combining them and putting the new node as the parent
			HuffmanNode first = q.poll();
			HuffmanNode second = q.poll();
			HuffmanNode newNode = new HuffmanNode(first._freq + second._freq,(byte)'`');
			newNode.left = first;
			newNode.right = second;
			root = newNode;
			q.add(newNode);//o(logn)
		}
		return root;
	}

      
    public void buildDictionary (HashMap<Byte, String> dictionary, String s)
    {
    	//create a code for every char in the tree.
    	
        if (this.left== null && this.right == null) {//If reached a node, save the string (the path we went to get to it - left=0 right=1)
            dictionary.put((byte)this._ch, s); 
            return;
        }

 
        // recursive calls for left and
        // right sub-tree of the generated tree.
        this.left.buildDictionary(dictionary,s + "0");
        this.right.buildDictionary(dictionary,s + "1");
    }
    public HuffmanNode getLeft(){
    	return this.left;
    }
    public HuffmanNode getRight(){
    	return this.right;
    }

    public void setLeft(HuffmanNode tmp){this.left=tmp;}
    public void setRight(HuffmanNode tmp){this.right=tmp;}
    
    public String writeTree(HuffmanNode root,String _tree){
    	/*
    	 * Writing the tree to the coded file
    	 * a leaf (no children nodes) is represented by a '1'
    	 * When a leaf is found, it's byte (char) is encoded immidiatly after.
    	 * a branch is represented by a '0'.
    	 * inorder traversal (left-root-right)
    	 */
    	StringBuilder tree=new StringBuilder();
    	if(root.left==null && root.right==null){
    		tree.append('1');
    		byte temp = (root.get_char());
    		tree.append((char)temp);
    		_tree+=tree.toString();
    	}
    	else {_tree+='0';
    	if(root.getLeft()!=null)_tree=writeTree(root.left,_tree);
    	if(root.getRight()!=null) _tree=writeTree(root.right,_tree);}
    	return _tree;
    }
    public static HuffmanNode ReBuildTree(Queue<Byte> byteQ){
    	/*
    	 * Rebuilding the tree using the tree code as a Queue.
    	 */
    	HuffmanNode root=new HuffmanNode(-1,(byte)'2');
    	byte tmp = byteQ.poll();//Removes first byte of the queue and assigns to tmp
    	
    	if (tmp == '1'){ 
    		//If found a '1', its a leaf - create a node and put the next byte as it's char
    		root=new HuffmanNode(-1, byteQ.poll());
    		
    	}
    	else{
    		
    		root.setLeft(ReBuildTree(byteQ));
    		
    		root.setRight(ReBuildTree(byteQ));
    		
    		}
    	
    	return root;
    	
    };
    
    
    
}


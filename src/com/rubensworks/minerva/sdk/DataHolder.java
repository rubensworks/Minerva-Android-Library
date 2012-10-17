package com.rubensworks.minerva.sdk;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tree representation of the data
 * @author Ruben Taelman
 *
 */
public class DataHolder {
	private String name;
	private String value;
	private DataHolder[] data;
	private boolean leaf;
	
	/**
	 * Constructs leaf
	 * @param name
	 * @param value
	 */
	public DataHolder(String name, String value) {
		this.name=name;
		this.value=value;
		this.data=null;
		this.leaf=true;
	}
	
	/**
	 * Constructs non-leaf
	 * @param name
	 * @param data
	 */
	public DataHolder(String name, DataHolder[] data) {
		this.name=name;
		this.value=null;
		this.data=data;
		this.leaf=false;
	}
	
	/**
	 * Checks if this DataHolder has a direct value, or else it has a sub dataholder
	 * @return	boolean
	 */
	public boolean isLeaf() {
		return this.leaf;
	}
	
	/**
	 * Data name of value or dataholder array
	 * @return	data name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the string value
	 * @return	string value
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Gets the sub dataholders
	 * @return	sub data holders
	 */
	public DataHolder[] getData() {
		return this.data;
	}
	
	/**
	 * Sets the string value
	 * @param value	string value
	 */
	public void setValue(String value) {
		this.value=value;
		this.data=null;
		this.leaf=true;
	}
	
	/**
	 * Sets a new dataholder array
	 * @param data	dataholder array
	 */
	public void setData(DataHolder[] data) {
		this.value=null;
		this.data=data;
		this.leaf=false;
	}

	@Override
	public String toString() {
		if(this.isLeaf())
			return ";"+this.getName()+":"+this.getValue()+"\n";
		else {
			String s = this.getName()+":\n";
			for(int i=0;i<this.getData().length;i++)
				s+=this.getData()[i];
			return s; 
		}
	}
	
	/**
	 * Make a DataHolder out of a Node that can have multiple subNodes
	 * @param node	parentnode
	 * @return	dataholder with the node data
	 */
	public static DataHolder addNodes(Node node) {
		NodeList nodeList = node.getChildNodes();
		if(nodeList.getLength()==1)
			return new DataHolder(node.getNodeName(),node.getTextContent());
		else {
			//looping trough the array twice to count how much non-empty nodes there are
			int amount=0;
			for (int i = 0; i < nodeList.getLength(); i++) {
	            Node currentNode = nodeList.item(i);
	            if(currentNode.getChildNodes().getLength()>0) {
	            	amount++;
	            }
	        }
			DataHolder[] data=new DataHolder[amount];
			int j=0;
			for (int i = 0; i < nodeList.getLength(); i++) {
	            Node currentNode = nodeList.item(i);
	            if(currentNode.getChildNodes().getLength()>0) {
	            	data[j]=DataHolder.addNodes(currentNode);
	            	j++;
	            }
	        }
			return new DataHolder(node.getNodeName(),data);
		}
    }
	
	/**
	 * True if the dataHolder contains an error message (call this on the top DataHolder)
	 * @return boolean
	 */
	public boolean isError() {
		return (data!=null && data.length==1 && "error".equals(data[0].getName()))
				|| ("error".equals(name));
	}
}

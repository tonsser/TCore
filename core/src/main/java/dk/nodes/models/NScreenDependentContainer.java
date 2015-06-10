package dk.nodes.models;

import java.io.Serializable;
import java.util.HashMap;

public class NScreenDependentContainer implements Serializable {

	
	private static NScreenDependentContainer instance;
	private HashMap<String, NScreenDependentItem> list = new HashMap<String, NScreenDependentItem>();
	
	public static NScreenDependentContainer getIntance(){
		if(instance == null)
			instance = new NScreenDependentContainer();
		
		return instance;
	}
	
	public void putItem(String key, NScreenDependentItem mNSizeDependentItem){
		list.put(key, mNSizeDependentItem);
	}
	
	public NScreenDependentItem getItem(String key){
		if(list.get(key) != null)
			return list.get(key);
		else
			return new NScreenDependentItem(0, 0, 0, 0, 0);
	}
}

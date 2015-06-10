package dk.nodes.map.v2.model;


public class NDataMarkerV2<T> extends NMarkerV2 {

	private T data = null;

	public T getData() {
		return data;
	}

	public void setData( T data ) {
		this.data = data;
	}
	
}

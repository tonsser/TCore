package dk.nodes.webservice.models;
import org.json.JSONException;

import dk.nodes.webservice.parser.NJSONObject;

/**
 * @author Casper Rasmussen 2012
 */

public class NResponse {

	private NJSONObject jsonReturn;
	private int responseCode;

	public NResponse(int responseCode){
		this.responseCode = responseCode;
	}
	
	public NResponse (NJSONObject jsonReturn, int responseCode) throws JSONException {
		this.jsonReturn = jsonReturn;
		this.responseCode = responseCode;
	}

	public int getResponseCode(){
		return responseCode;
	}
	public void setResponseCode(int newResponseCode){
		responseCode = newResponseCode;
	}
	public NJSONObject getResponseJson(){
		return jsonReturn;
	}
}

package dk.nodes.webservice.cookies;
/**
 * @author Casper Rasmussen 2012
 */
import java.io.Serializable;
import java.util.ArrayList;

public class NWebserviceCookieContainer implements Serializable {

	private static final long serialVersionUID = 1L; // Version 1
	private ArrayList<NWebserviceCookieClass> cookieList = new ArrayList<NWebserviceCookieClass>();

	
	public void setCookieList(ArrayList<NWebserviceCookieClass> cookieList){
		this.cookieList = cookieList;
	}
	public ArrayList<NWebserviceCookieClass> getCookieList(){
		return cookieList;
	}
	public boolean isEmpty(){
		if(cookieList==null || cookieList.size()==0)
			return true;
		else
			return false;
	}
}

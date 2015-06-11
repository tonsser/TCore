package dk.nodes.webservice.cookies;
/**
 * @author Casper Rasmussen 2012
 */

import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class NWebserviceCookieController {
	
	public static ArrayList<NWebserviceCookieClass> setCookieList(List<Cookie> cookieListInput){
		ArrayList<NWebserviceCookieClass> cookieListOutput = new ArrayList<NWebserviceCookieClass>();
		for(Cookie cookie : cookieListInput){
			cookieListOutput.add(new NWebserviceCookieClass(cookie));
		}
		return cookieListOutput;
	}

	public static ArrayList<Cookie> getCookieList(ArrayList<NWebserviceCookieClass> cookieListInput){
		ArrayList<Cookie> cookieListOutput = new ArrayList<Cookie>();
		for(NWebserviceCookieClass cookie : cookieListInput){
			if(!cookie.isExpired())
				cookieListOutput.add(cookie.getClientCookie());
		}
		return cookieListOutput;
	}
	public static ArrayList<Cookie> mergeCookieLists(List<Cookie> cookieList1, List<Cookie> cookieList2){

		ArrayList<Cookie> mergedCookieList = new ArrayList<Cookie>();
		mergedCookieList.addAll(cookieList1);
		
		for(int i = 0 ; i < cookieList1.size() ; i++){
			for(int j = 0 ; j < cookieList2.size() ; j++){
				if(!cookieList1.get(i).getName().equals(cookieList2.get(j).getName()))
					mergedCookieList.add(cookieList2.get(j));
			}
		}
		return mergedCookieList;	
	}
}

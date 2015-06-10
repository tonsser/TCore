package dk.nodes.webservice.cookies;
/**
 * @author Casper Rasmussen 2012
 */

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.Serializable;
import java.util.Date;

public class NWebserviceCookieClass implements Serializable {
	private static final long serialVersionUID = 1L; // Version 1
	public String name;
	public String value;
	public String domain;
	public Date expireDate;
	public String path;
	public boolean secure;
	public int version;


	public NWebserviceCookieClass (Cookie c){
		name = c.getName();
		value = c.getValue();
		domain = c.getDomain();
		expireDate = c.getExpiryDate();
		path = c.getPath();
		version = c.getVersion();
	}

	public BasicClientCookie getClientCookie(){
		BasicClientCookie cookie = new BasicClientCookie(name,value);
		cookie.setDomain(domain);
		cookie.setExpiryDate(expireDate);
		cookie.setPath(path);
		cookie.setSecure(secure);
		cookie.setVersion(version);
		return cookie;
	}

	public boolean isExpired(){
		if(System.currentTimeMillis()>expireDate.getTime())
			return true;
		else
			return false;
	}
}

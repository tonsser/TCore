package dk.nodes.models;

import java.io.Serializable;

import dk.nodes.controllers.NScreenParameters;

public class NScreenDependentItem implements Serializable {

	private double ldpi;
	private double mdpi;
	private double hdpi;
	private double xhdpi;
	private double xxhdpi;
	private String ldpiString;
	private String mdpiString;
	private String hdpiString;
	private String xhdpiString;
	private String xxhdpiString;
	
	public NScreenDependentItem(double ldpi, double mdpi, double hdpi, double xhdpi,
			double xxhdpi) {
		super();
		this.ldpi = ldpi;
		this.mdpi = mdpi;
		this.hdpi = hdpi;
		this.xhdpi = xhdpi;
		this.xxhdpi = xxhdpi;
	}
	
	public NScreenDependentItem(String ldpi, String mdpi, String hdpi, String xhdpi,
			String xxhdpi) {
		super();
		this.ldpiString = ldpi;
		this.mdpiString = mdpi;
		this.hdpiString = hdpi;
		this.xhdpiString = xhdpi;
		this.xxhdpiString = xxhdpi;
	}
	
	public double getSize(){
		if(NScreenParameters.screenDensity < 160)
			return ldpi;
		else if(NScreenParameters.screenDensity >= 160 && NScreenParameters.screenDensity < 240)
			return mdpi;
		else if(NScreenParameters.screenDensity >= 240 && NScreenParameters.screenDensity < 320)
			return hdpi;
		else if(NScreenParameters.screenDensity >= 320 && NScreenParameters.screenDensity < 480)
			return xhdpi;
		else
			return xxhdpi;
	}
	
	public String getSizeString(){
		if(NScreenParameters.screenDensity < 160)
			return ldpiString;
		else if(NScreenParameters.screenDensity >= 160 && NScreenParameters.screenDensity < 240)
			return mdpiString;
		else if(NScreenParameters.screenDensity >= 240 && NScreenParameters.screenDensity < 320)
			return hdpiString;
		else if(NScreenParameters.screenDensity >= 320 && NScreenParameters.screenDensity < 480)
			return xhdpiString;
		else
			return xxhdpiString;
	}
}

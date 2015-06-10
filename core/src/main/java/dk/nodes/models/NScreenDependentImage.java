package dk.nodes.models;
/**
 * @author Casper Rasmussen 2012
 */
import java.io.Serializable;

import dk.nodes.controllers.NScreenParameters;

public class NScreenDependentImage implements Serializable {

	private String imageNormalUrlMdpi; //Also used for small hdpi
	private String imageNormalUrlHdpi; //Also used for large mdpi
	private String imageNormalUrlXhdpi; //Also used in large hdpi or x-large mdpi
	private String imageNormalUrlXXhdpi; // Also used for x-large xhdpi

	/**
	 * Use this method if you have 4 image sizes
	 * @param imageUrlMdpi is also used for small hdpi
	 * @param imageUrlHdpi is also used for large mdpi
	 * @param imageUrlXhdpi is also used in large hdpi or x-large mdpi
	 * @param imageNormalUrlXXhdpi is also used for x-large xhdpi
	 */
	public NScreenDependentImage(String imageNormalUrlMdpi, String imageNormalUrlHdpi,String imageNormalUrlXhdpi, String imageNormalUrlXXhdpi){
		this.imageNormalUrlMdpi = imageNormalUrlMdpi;
		this.imageNormalUrlHdpi = imageNormalUrlHdpi;
		this.imageNormalUrlXhdpi = imageNormalUrlXhdpi;
		this.imageNormalUrlXXhdpi = imageNormalUrlXXhdpi;
	}
	/**
	 * Use this constructor if you have 3 image sizes
	 * @param imageUrlMdpi is also used for small hdpi
	 * @param imageUrlHdpi is also used for large mdpi
	 * @param imageUrlXhdpi is also used in large hdpi or x-large mdpi
	 */
	public NScreenDependentImage(String imageNormalUrlMdpi, String imageNormalUrlHdpi,String imageNormalUrlXhdpi){
		this.imageNormalUrlMdpi = imageNormalUrlMdpi;
		this.imageNormalUrlHdpi = imageNormalUrlHdpi;
		this.imageNormalUrlXhdpi = imageNormalUrlXhdpi;
	}
	/**
	 * Use this constructor if you have 2 image sizes, 
	 * @param imageNormalUrlHdpi
	 * @param imageNormalUrlXhdpi
	 */
	public NScreenDependentImage(String imageNormalUrlHdpi,String imageNormalUrlXhdpi){
		this.imageNormalUrlHdpi = imageNormalUrlHdpi;
		this.imageNormalUrlXhdpi = imageNormalUrlXhdpi;
	}
	/**
	 * Use this constructor if you got 1 image size, maybe it's not worth using this class :)
	 * @param imageNormalUrlXhdpi
	 */
	public NScreenDependentImage(String imageNormalUrlXhdpi){
		this.imageNormalUrlXhdpi = imageNormalUrlXhdpi;
	}

	/**
	 * This method will return image dependent on screen density only.
	 * Else use getImageScreenSizeDependent()
	 * @return String or null
	 */
	public String getImageDPIDependent(){
		if(NScreenParameters.screenDensityConstant<= 1f)
			return getImageMdpi();
		else if(NScreenParameters.screenDensityConstant<= 1.5f)
			return getImageHdpi();
		else if(NScreenParameters.screenDensityConstant<= 2f)
			return getImageXhdpi();
		else
			return getImageXXhdpi();
	}

	/**
	 * This method will return image dependent on screen size & density
	 * @return
	 */
	public String getImageScreenSizeAndDPIDependent(){
		if(NScreenParameters.screenType == NScreenParameters.SMALL_SCREEN ){
			return getImageMdpi();
		}
		else if(NScreenParameters.screenType == NScreenParameters.NORMAL_SCREEN){
			if(NScreenParameters.screenDensityConstant<=1f)
				return getImageMdpi();
			else if(NScreenParameters.screenDensityConstant<=1.5f)
				return getImageHdpi();
			else if(NScreenParameters.screenDensityConstant<=2.0f)
				return getImageXhdpi();
			else
				return getImageXXhdpi();
		}
		else if(NScreenParameters.screenType == NScreenParameters.LARGE_SCREEN){
			if(NScreenParameters.screenDensityConstant<=1f)
				return getImageHdpi();
			else if(NScreenParameters.screenDensityConstant<=1.5f)
				return getImageXhdpi();
			else
				return getImageXXhdpi();
		}
		else if(NScreenParameters.screenType == NScreenParameters.X_LARGE_SCREEN){
			if(NScreenParameters.screenDensityConstant<=1f)
				return getImageXhdpi();
			else
				return getImageXXhdpi();
		}
		else
			return getImageXhdpi();
	}

	/**
	 * Get imageUrl of mdpi size, if mdpi is null it returns hdpi else xdpi eler null
	 * @return String
	 */
	private String getImageMdpi(){
		if(imageNormalUrlMdpi!=null)
			return imageNormalUrlMdpi;
		else if(imageNormalUrlHdpi!=null)
			return imageNormalUrlHdpi;
		else if(imageNormalUrlXhdpi!=null)
			return imageNormalUrlXhdpi;
		else if(imageNormalUrlXXhdpi!=null)
			return imageNormalUrlXXhdpi;
		else
			return null;
	}
	/**
	 * Get imageUrl of hdpi size, if hdpi is null it returns xhdpi else mdpi eler null
	 * @return String
	 */
	private String getImageHdpi(){
		if(imageNormalUrlHdpi!=null)
			return imageNormalUrlHdpi;
		else if(imageNormalUrlXhdpi!=null)
			return imageNormalUrlXhdpi;
		else if(imageNormalUrlXXhdpi!=null)
			return imageNormalUrlXXhdpi;
		else if(imageNormalUrlMdpi!=null)
			return imageNormalUrlMdpi;		
		else
			return null;
	}

	/**
	 * Get imageUrl of xhdpi size, if xhdpi is null it returns hdpi else mdpi eler null
	 * @return String
	 */
	private String getImageXhdpi(){
		if(imageNormalUrlXhdpi!=null)
			return imageNormalUrlXhdpi;
		else if(imageNormalUrlXXhdpi!=null)
			return imageNormalUrlXXhdpi;
		else if(imageNormalUrlHdpi!=null)
			return imageNormalUrlHdpi;
		else if(imageNormalUrlMdpi!=null)
			return imageNormalUrlMdpi;
		else
			return null;
	}

	private String getImageXXhdpi(){
		if(imageNormalUrlXXhdpi!=null)
			return imageNormalUrlXXhdpi;
		else if (imageNormalUrlXhdpi!=null)
			return imageNormalUrlXhdpi;
		else if(imageNormalUrlHdpi!=null)
			return imageNormalUrlHdpi;
		else if(imageNormalUrlMdpi!=null)
			return imageNormalUrlMdpi;
		else
			return null;
	}
}

package dk.tonsser.controllers.rateme;
/**
 * @author Martin 2012, modified by CR
 */

import android.content.Context;
import android.content.SharedPreferences;

import dk.tonsser.utils.math.NUnits;
import dk.tonsser.utils.NBuild;

public class NRateMeController {

	private String appTitle;
	private Context mContext;
	private int daysUntilPrompt = 3;
	private int launchesUntilPrompt = 3;
	private String infoText = "Please rate the app";
	private String AskMeLaterText ="Ask me later";
	private String yesText= "Yes";
	private String noText ="No";
	private OnNrateMeControllerPromptListener mOnNrateMeControllerPromptListener;
	private NRateMeDialog mNRateMeDialog;

	/**
	 * Use this method if you want to set all parameter for the controller, but want to use standard dialog.
	 * Remember to call execute to start the calculations
	 * @param mContext
	 * @param appTitle
	 * @param daysUntilPrompt
	 * @param launchesUntilPrompt
	 * @param infoText
	 * @param AskMeLaterText
	 * @param yesText
	 * @param noText
	 */
	public NRateMeController(Context mContext,String appTitle,int daysUntilPrompt,int launchesUntilPrompt,String infoText,String AskMeLaterText,String yesText, String noText){
		this.mContext = mContext;
		this.appTitle = appTitle;
		this.daysUntilPrompt = daysUntilPrompt;
		this.launchesUntilPrompt = launchesUntilPrompt;		
		this.AskMeLaterText = AskMeLaterText;
		this.infoText = infoText;
		this.yesText = yesText;
		this.noText  = noText;
	}
	/**
	 * Use this method if you wanna use all the standard values and a standard dialog
	 * Remember to call execute to start the calculations
	 * @param mContext
	 * @param appTitle
	 */
	public NRateMeController(Context mContext,String appTitle){
		this.mContext = mContext;
		this.appTitle = appTitle;
	}
	/**
	 * Use this method if you wanna use all the standard values, but want a callback when to prompt
	 * Remember to call execute to start the calculations
	 * @param mContext
	 * @param appTitle
	 * @param mOnNrateMeControllerPromptListener
	 */
	public NRateMeController(Context mContext,String appTitle,OnNrateMeControllerPromptListener mOnNrateMeControllerPromptListener){
		this.mContext = mContext;
		this.appTitle = appTitle;	
		this.mOnNrateMeControllerPromptListener = mOnNrateMeControllerPromptListener;
	}
	/**
	 * Use this method if you wanna set all values and get a callback when to prompt
	 * Remember to call execute to start the calculations
	 * @param mContext
	 * @param appTitle
	 * @param mOnNrateMeControllerPromptListener
	 * @param daysUntilPrompt
	 * @param launchesUntilPrompt
	 */
	public NRateMeController(Context mContext,String appTitle,OnNrateMeControllerPromptListener mOnNrateMeControllerPromptListener,int daysUntilPrompt,int launchesUntilPrompt){
		this.mContext = mContext;
		this.appTitle = appTitle;	
		this.daysUntilPrompt = daysUntilPrompt;
		this.launchesUntilPrompt = launchesUntilPrompt;		
		this.mOnNrateMeControllerPromptListener = mOnNrateMeControllerPromptListener;
	}

	/**
	 * Call this method when to execute prompt calculations
	 */
	public void execute() {
		SharedPreferences prefs = mContext.getSharedPreferences("rateMeLaunchCount", 0);
		if (prefs.getBoolean("rateMeDontShowAgain", false)) { return ; }

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong("rateMeLaunchCount", 0) + 1;
		editor.putLong("rateMeLaunchCount", launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("rateMeDateFirstLaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("rateMeDateFirstLaunch", date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= launchesUntilPrompt) {        	
			if (System.currentTimeMillis() >= date_firstLaunch + (daysUntilPrompt * NUnits.DAY_IN_MS)) {
				if(mOnNrateMeControllerPromptListener==null)
					showRateDialog(mContext, editor);
				else
					mOnNrateMeControllerPromptListener.OnNrateMeControllerPromptListenerOnPrompt();
			}
		}
		editor.commit();
	}   

	/**
	 * Method for showing the rate dialog. You can set the message content of the dialog here by using the RateMeDialog constructor.
	 * @param mContext
	 * @param editor
	 */
	private  void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
		if(mNRateMeDialog==null || (mNRateMeDialog!=null && !mNRateMeDialog.isShowing())){
			mNRateMeDialog = new NRateMeDialog(mContext, infoText, AskMeLaterText, yesText,noText, editor, NBuild.getPackageName(mContext), appTitle);
			mNRateMeDialog.show();   
		}
	}


	public interface OnNrateMeControllerPromptListener{
		void OnNrateMeControllerPromptListenerOnPrompt();
	}
}
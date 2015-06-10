package dk.nodes.widgets.togglebuttons;

import android.text.SpannableString;
import android.widget.ToggleButton;

public class NToggleBtnController {
	
	public static void setSameTextAsAllStates(ToggleButton tb, String text){
		tb.setText(text);
		tb.setTextOff(text);
		tb.setTextOn(text);
	}
	
	public static void setSameTextAsAllStates(ToggleButton tb, SpannableString text){
		tb.setText(text);
		tb.setTextOff(text);
		tb.setTextOn(text);
	}
}

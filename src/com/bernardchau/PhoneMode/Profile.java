package com.bernardchau.PhoneMode;

import android.net.wifi.WifiManager;

public class Profile {
	public long pID;
	public String pNAME;
	public int pTYPE;
	public int pWIFI;
	public int pBRIGHT;
	public int pBLUE;

	public static final int STATE_NO_CHANGE = -1;
	public static final int STATE_SWITCH_OFF = 0;
	public static final int STATE_SWITCH_ON = 1;
	
	public static final int TYPE_MANUAL = -1;
	public static final int TYPE_AUTO = 1;
	
	public boolean applyProfile(PhoneMode pm) {
		// TODO Auto-generated method stub
		boolean isSuccess = true;
		
		isSuccess = applyWifi(pm);
		isSuccess = applyBlue();
		isSuccess = applyBright(pm);
		
		return isSuccess;
	}

	private boolean applyBright(PhoneMode pm) {
		// TODO Auto-generated method stub
		android.view.Window w = pm.getWindow();
		android.view.WindowManager.LayoutParams layoutParams = w.getAttributes();
		if (pBRIGHT != STATE_NO_CHANGE) {
			layoutParams.screenBrightness = (float)pBRIGHT / 255f;
			w.setAttributes(layoutParams);
			return android.provider.Settings.System.putInt(pm.getContentResolver(),
				     android.provider.Settings.System.SCREEN_BRIGHTNESS, pBRIGHT);
		}
		return true;
	}

	private boolean applyBlue() {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean applyWifi(PhoneMode pm) {
		// TODO Auto-generated method stub
		if (pWIFI != STATE_NO_CHANGE) {
			WifiManager WifiMan = (WifiManager) pm.getSystemService(android.content.Context.WIFI_SERVICE);
			return WifiMan.setWifiEnabled(pWIFI == STATE_SWITCH_ON ? true : false);
		}		
		return true;
	}
}

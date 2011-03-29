package com.bernardchau.PhoneMode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class EditProfile extends Activity{
	private EditText txtName;
	
	private CheckBox cbxBright;
	private CheckBox cbxWifi;
	private CheckBox cbxBlue;
	
	private SeekBar skbBright;
	private RadioGroup rdoWifi;
	private RadioButton rdoWifiOn;
	private RadioButton rdoWifiOff;
	private RadioGroup rdoBlue;
	private RadioButton rdoBlueOn;
	private RadioButton rdoBlueOff;

	Long dbID = null;
    Context myCtx = null;
    
    public static float brightnessMax = 255f, brightnessMin = 1f;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCtx = this;
        
        setContentView(R.layout.profile_edit);        
        setTitle("Profile Details");

        txtName = (EditText) findViewById(R.id.txtName);
        cbxBright = (CheckBox) findViewById(R.id.cbxBright);
        cbxWifi = (CheckBox) findViewById(R.id.cbxWifi);
        cbxBlue = (CheckBox) findViewById(R.id.cbxBlue);
        skbBright = (SeekBar) findViewById(R.id.skbBright);
        rdoWifi = (RadioGroup) findViewById(R.id.rdoWifi);
        rdoWifiOn = (RadioButton) findViewById(R.id.rdoWifiOn);
        rdoWifiOff = (RadioButton) findViewById(R.id.rdoWifiOff);
        rdoBlue = (RadioGroup) findViewById(R.id.rdoBlue);
        rdoBlueOn = (RadioButton) findViewById(R.id.rdoBlueOn);
        rdoBlueOff = (RadioButton) findViewById(R.id.rdoBlueOff);
        
        cbxBright.setOnCheckedChangeListener(cbxBrightListener);
        skbBright.setOnSeekBarChangeListener(skbBrightListener);
        cbxWifi.setOnCheckedChangeListener(cbxWifiListener);
        cbxBlue.setOnCheckedChangeListener(cbxBlueListener);        
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dbID = extras.getLong(PmDbAdapter.cHEADER[PmDbAdapter.cID]);
        	fillDetails(PhoneMode.getProfile(dbID));
        }
	}

	private void fillDetails(Profile p) {
		// TODO Auto-generated method stub
		
		//name
		txtName.setText(p.pNAME);
		
		//brightness
		if (p.pBRIGHT != Profile.STATE_NO_CHANGE) {
			cbxBright.setChecked(true);
			skbBright.setProgress(p.pBRIGHT-1);
		}
		
		//bluetooth
		if (p.pBLUE != Profile.STATE_NO_CHANGE) {
			cbxBlue.setChecked(true);
			if (p.pBLUE == Profile.STATE_SWITCH_OFF)
				rdoBlueOff.setChecked(true);
			else if (p.pBLUE == Profile.STATE_SWITCH_ON)
				rdoBlueOn.setChecked(true);
		}
		
		//wifi
		if (p.pWIFI != Profile.STATE_NO_CHANGE) {
			cbxWifi.setChecked(true);
			if (p.pWIFI == Profile.STATE_SWITCH_OFF)
				rdoWifiOff.setChecked(true);
			else if (p.pWIFI == Profile.STATE_SWITCH_ON)
				rdoWifiOn.setChecked(true);
		}
	}
	
	private OnCheckedChangeListener cbxBrightListener = new OnCheckedChangeListener () {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				skbBright.setVisibility(View.VISIBLE);
			}
			else {
				skbBright.setVisibility(View.GONE);
			}				
		}		
	};

	private OnCheckedChangeListener cbxWifiListener = new OnCheckedChangeListener () {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				rdoWifi.setVisibility(View.VISIBLE);
			}
			else {
				rdoWifi.setVisibility(View.GONE);
			}				
		}		
	};
	
	private OnCheckedChangeListener cbxBlueListener = new OnCheckedChangeListener () {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				rdoBlue.setVisibility(View.VISIBLE);
			}
			else {
				rdoBlue.setVisibility(View.GONE);
			}				
		}		
	};
	
	private OnSeekBarChangeListener skbBrightListener = new OnSeekBarChangeListener () {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
			layoutParams.screenBrightness = (float)((arg1 + brightnessMin) / brightnessMax);
			getWindow().setAttributes(layoutParams);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
		
	};
	
	public void btnDiscardOnClick (View v) {
		setResult(RESULT_CANCELED, new Intent());
		finish();
	}
	
	public void btnSaveOnClick (View v) {
		Bundle bundle = new Bundle();
		
		//id
		if (dbID != null)
			bundle.putLong(PmDbAdapter.cHEADER[PmDbAdapter.cID], dbID);
		
		//name
		bundle.putString(PmDbAdapter.cHEADER[PmDbAdapter.cNAME], txtName.getText().toString());
		
		//brightness
		if (!cbxBright.isChecked()) //not selected
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cBRIGHT], Profile.STATE_NO_CHANGE);
		else //selected
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cBRIGHT], skbBright.getProgress()+1);
		
		//wifi
		if (!cbxWifi.isChecked()) //not selected
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cWIFI], Profile.STATE_NO_CHANGE);
		else if (rdoWifiOn.isChecked()) //selected on
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cWIFI], Profile.STATE_SWITCH_ON);
		else if (rdoWifiOff.isChecked()) //selected off
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cWIFI], Profile.STATE_SWITCH_OFF);
		
		//bluetooth
		if (!cbxBlue.isChecked()) //not selected
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cBLUE], Profile.STATE_NO_CHANGE);
		else if (rdoBlueOn.isChecked()) //selected on
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cBLUE], Profile.STATE_SWITCH_ON);
		else if (rdoBlueOff.isChecked()) // selected off
			bundle.putInt(PmDbAdapter.cHEADER[PmDbAdapter.cBLUE], Profile.STATE_SWITCH_OFF);
		//Toast.makeText(myCtx, skbBright.getProgress() + "", Toast.LENGTH_SHORT).show();
		
		Intent i = new Intent();
		i.putExtras(bundle);
		setResult(RESULT_OK, i);
		finish();
	}
}

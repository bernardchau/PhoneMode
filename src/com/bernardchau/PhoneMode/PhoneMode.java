package com.bernardchau.PhoneMode;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PhoneMode extends ListActivity {
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = INSERT_ID + 1;
	private static final int ABOUT_ID = DELETE_ID + 1;
	private static final int EDIT_ID = ABOUT_ID + 1;
	
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private PmDbAdapter dbHelper;
	private static ArrayList<Profile> myProfiles = new ArrayList<Profile>();
	private static ArrayList<String> myProfileNames = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_list);
		
		dbHelper = new PmDbAdapter(this);
		dbHelper.open();
		//dbHelper.dropTable();
		//dbHelper.createTable();
		fillProfiles();
		registerForContextMenu(getListView());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		//menu.clear();
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		menu.add(0, ABOUT_ID, 0, R.string.menu_about);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:			
			//createProfile("testing" + String.valueOf(myProfiles.size()), -1, -1, -1, -1);
			createProfile();
			return true;
		case ABOUT_ID:			
			showAlertDialog("About " + getResources().getString(R.string.app_name),
					getResources().getString(R.string.about_content));
			return true;
		default:
			showToast("default" + String.valueOf(item.getItemId()), Toast.LENGTH_SHORT);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch(item.getItemId()) {
            case DELETE_ID:
                if (deleteProfile((int) info.id))                               
                	return true;
                break;
            case EDIT_ID:
                editProfile((int) info.id);
                return true;
                //break;
            default:
            	showToast("default" + String.valueOf(item.getItemId()), Toast.LENGTH_SHORT);
    			break;
        }
        return super.onContextItemSelected(item);
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
	        Bundle extras = intent.getExtras();
	        switch(requestCode) {
	            case ACTIVITY_CREATE:
	                createProfile(extras);
	                break;
	            case ACTIVITY_EDIT:
	                updateProfile(extras);
	                break;
	        }
        }
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final String diffPosId = "position and id different: " + position + " " + id;
        
        if (position != id){
        	showToast(diffPosId, Toast.LENGTH_LONG);
        	return;
        }
        applyProfile(position);        	
    }
    
    private void applyProfile(int index) {
        Profile p = myProfiles.get(index);
        final String success = "Profile '" + p.pNAME + "' applied!";
        final String failure = "Fail to apply profile '" + p.pNAME + "'!";
        
        if (p.applyProfile(this)) {
        //if (applyProfile(p)) {
        	showToast(success, Toast.LENGTH_SHORT);
        }
        else
        	showToast(failure, Toast.LENGTH_LONG);
    }
   /* 
    private boolean applyProfile(Profile p) {
    	boolean isSuccess = true;
		
		isSuccess = applyWifi(p);
		isSuccess = applyBlue(p);
		isSuccess = applyBright(p);
		
		return isSuccess;
    }
    
	private boolean applyBlue(Profile p) {
		// TODO Auto-generated method stub
		return true;
	}


	private boolean applyBright(Profile p) {
		
		if (p.pBRIGHT != Profile.STATE_NO_CHANGE)
			//WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
			return android.provider.Settings.System.putInt(getContentResolver(),
				     android.provider.Settings.System.SCREEN_BRIGHTNESS, p.pBRIGHT);
		return true;
	}


	private boolean applyWifi(Profile p) {
		if (p.pWIFI != Profile.STATE_NO_CHANGE) {
			WifiManager WifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			return WifiMan.setWifiEnabled(p.pWIFI == Profile.STATE_SWITCH_ON ? true : false);
		}
		return true;
	}
*/

	private void fillProfiles() {
		myProfiles.clear();
		myProfileNames.clear();
		Cursor c = dbHelper.fetchAllProfiles();
		startManagingCursor(c);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			createProfile(
				c.getInt(PmDbAdapter.cID),
				c.getString(PmDbAdapter.cNAME),
				c.getInt(PmDbAdapter.cTYPE),
				c.getInt(PmDbAdapter.cWIFI),
				c.getInt(PmDbAdapter.cBRIGHT),
				c.getInt(PmDbAdapter.cBLUE)
				//-1,-1,-1,-1
			);

			c.moveToNext();
		}

		if (myProfiles.size() > 0) {
			refreshList();	
		}
	}
	
	private void createProfile() {
		Intent i = new Intent(this, EditProfile.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	private void createProfile(Bundle extras) {
		String name = extras.getString(PmDbAdapter.cHEADER[PmDbAdapter.cNAME]);
		int type = -1;
		int wifi = extras.getInt(PmDbAdapter.cHEADER[PmDbAdapter.cWIFI]);
		int bright = extras.getInt(PmDbAdapter.cHEADER[PmDbAdapter.cBRIGHT]);
		int blue = extras.getInt(PmDbAdapter.cHEADER[PmDbAdapter.cBLUE]);
		createProfile(name, type, wifi, bright, blue);
	}
	
	private boolean createProfile
	(String name, int type, int wifi, int bright, int blue){
		final String success = "Profile '" + name + "' created!";
		final String failure = "Fail to create profile '" + name + "'!";
		if (myProfileNames.contains(name)) {
			showToast(failure + " This profile name has been used!", Toast.LENGTH_LONG);
			return false;
		}
		long returnID = dbHelper.createProfile(name, type, wifi, bright, blue);		
		//showToast("ID: " + String.valueOf(returnID), Toast.LENGTH_SHORT);		
		if (returnID != -1) {
			showToast(success, Toast.LENGTH_SHORT);
			createProfile(returnID, name, type, wifi, bright, blue);
			refreshList();
			return true;
		}
		showToast(failure, Toast.LENGTH_LONG);
		return false;
	}

	private void createProfile
	(long id, String name, int type, int wifi, int bright, int blue){
		Profile newProfile = new Profile();
		newProfile.pID = id;
		newProfile.pNAME = name;
		newProfile.pTYPE = type;
		newProfile.pWIFI = wifi;
		newProfile.pBRIGHT = bright;
		newProfile.pBLUE = blue;
		
		myProfileNames.add(newProfile.pNAME);
		myProfiles.add(newProfile);
	}
	
	private boolean deleteProfile (int listID) {
		long dbID = myProfiles.get(listID).pID;
		String pNAME = myProfiles.get(listID).pNAME;
		boolean isSuccess = true;
		final String success = "Profile '" + pNAME + "' deleted!";
		final String failure = "Fail to delete profile '" + pNAME + "'!";
		
		if (dbHelper.deleteProfile(dbID))
			showToast(success, Toast.LENGTH_SHORT);
		else {
			showToast(failure, Toast.LENGTH_LONG);
			isSuccess = false;
		}
		
		if (isSuccess) {
			int before = myProfiles.size();
			myProfileNames.remove(listID);
			myProfiles.remove(listID);
			if (
				(before == myProfiles.size() + 1) 
				&&
				(before == myProfileNames.size() + 1)
			){
				refreshList();
			}
			else
				isSuccess = false;
		}
		return isSuccess;
	}
	
	private void editProfile(int listID) {
		Intent i = new Intent(this, EditProfile.class);
		i.putExtra(PmDbAdapter.cHEADER[PmDbAdapter.cID], myProfiles.get(listID).pID);
		startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	private void updateProfile(Bundle extras){
		long dbID = extras.getLong(PmDbAdapter.cHEADER[PmDbAdapter.cID]);
		String name = extras.getString(PmDbAdapter.cHEADER[PmDbAdapter.cNAME]);
		int type = Profile.TYPE_MANUAL;
		int wifi = extras.getInt(PmDbAdapter.cHEADER[PmDbAdapter.cWIFI]);
		int bright = extras.getInt(PmDbAdapter.cHEADER[PmDbAdapter.cBRIGHT]);
		int blue = extras.getInt(PmDbAdapter.cHEADER[PmDbAdapter.cBLUE]);
		updateProfile(dbID, name, type, wifi, bright, blue);
	}

	private boolean updateProfile
	(long id, String name, int type, int wifi, int bright, int blue){
		final String success = "Profile '" + name + "' updated!";
		final String failure = "Fail to update profile '" + name + "'!";
		
		boolean updated = dbHelper.updateProfile(id, name, type, wifi, bright, blue);		
		//showToast("ID: " + String.valueOf(returnID), Toast.LENGTH_SHORT);		
		if (updated == false) {
			showToast(failure, Toast.LENGTH_LONG);
			return false;
		}

		showToast(success, Toast.LENGTH_SHORT);
		
		Profile p = getProfile(id);
		p.pID = id;
		p.pNAME = name;
		p.pTYPE = type;
		p.pWIFI = wifi;
		p.pBRIGHT = bright;
		p.pBLUE = blue;
		
		int index = myProfiles.indexOf(p);
		myProfileNames.remove(index);
		myProfileNames.add(index, name);
		refreshList();
		return true;
	}
	
	private void refreshList() {
		//showNumOfProfiles();
		setListAdapter(new ArrayAdapter<String>(
				this, R.layout.list_item, myProfileNames)
			);	
	}
	
	private void showToast(String msg, int duration) {
		Toast.makeText(this, msg, duration).show();	
	}
	
	private void showNumOfProfiles() {
		final String text2show = "# of profiles: " + String.valueOf(myProfiles.size());
		showToast(text2show, Toast.LENGTH_SHORT);	
	}
	
	public void showAlertDialog (String title, String msg) {
		Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		alertDialog.setNeutralButton("OK", null);
		alertDialog.show();
	}
	
	public static Profile getProfile(long dbID){
		for (Profile p: myProfiles) {
			if (p.pID == dbID)
				return p;
		}
		return null;		
	}
}
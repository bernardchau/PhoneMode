package com.bernardchau.PhoneMode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PmDbAdapter {
	//db
	private static final String dbNAME = "PhoneModeDB";
	private static final int dbVERSION = 2;
	
	//table
	private static final String tNAME = "Profiles";
	
	//columns
	public static final String[] cHEADER = {
		"_ID", "Name", "Type", "Wifi", "Brightness", "BlueTooth"
	};
		
	public static final int cID = 0;
	public static final int cNAME = 1;
	public static final int cTYPE = 2;
	public static final int cWIFI = 3;
	public static final int cBRIGHT = 4;
	public static final int cBLUE = 5;
	
	private static final String TAG = "PmDbAdapter";
	private final Context myCtx;
	private PmDbHelper myDbHelper;
	private SQLiteDatabase myDb;
	
	private static class PmDbHelper extends SQLiteOpenHelper {

		PmDbHelper(Context context) {
			super(context, dbNAME, null, dbVERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			final String sql = 
				"create table " + tNAME + " ("
				+ cHEADER[cID] + " integer primary key autoincrement, "
				+ cHEADER[cNAME] + " text not null, "
				+ cHEADER[cTYPE] + " integer, "
				+ cHEADER[cWIFI] + " integer, "
				+ cHEADER[cBRIGHT] + " integer, "
				+ cHEADER[cBLUE] + " integer"
				+ ");";
			
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + tNAME);
            onCreate(db);
		}		
	} //inner class PmDbHelper ends
	
	public PmDbAdapter(Context ctx) {
		this.myCtx = ctx;
	}
	
	/**
     * Open the PhoneMode database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
	public PmDbAdapter open() throws SQLException {
        myDbHelper = new PmDbHelper(myCtx);
        myDb = myDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDbHelper.close();
    }
    
    public void dropTable() {
    	myDb.execSQL("DROP TABLE IF EXISTS " + tNAME);
    }
    
    public void createTable() {
    	final String sql = 
			"create table " + tNAME + " ("
			+ cHEADER[cID] + " integer primary key autoincrement, "
			+ cHEADER[cNAME] + " text not null, "
			+ cHEADER[cTYPE] + " integer, "
			+ cHEADER[cWIFI] + " integer, "
			+ cHEADER[cBRIGHT] + " integer, "
			+ cHEADER[cBLUE] + " integer"
			+ ");";
		
		myDb.execSQL(sql);
    }
    
    /**
     * Create a new profile. If the profile is
     * successfully created return the new cID for that profile, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name: the name of the profile
     * @param type: whether the profile is an auto or manual profile
     * @param wifi: the wifi state of the profile
     * @param bright: the brightness of the profile
     * @param blue: the bluetooth state of the profile
     * @return cId or -1 if failed
     */
    public long createProfile
    (String name, int type, int wifi, int bright, int blue) {
        ContentValues values = new ContentValues();
        values.put(cHEADER[cNAME], name);
        values.put(cHEADER[cTYPE], type);
        values.put(cHEADER[cWIFI], wifi);
        values.put(cHEADER[cBRIGHT], bright);
        values.put(cHEADER[cBLUE], blue);

        return myDb.insert(tNAME, null, values);
    }
    
    /**
     * Delete the profile with the given id
     * 
     * @param id: id of profile to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteProfile(long id){
    	return myDb.delete(tNAME, cHEADER[cID] + "=" + id, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all profiles in the database
     * 
     * @return Cursor over all profiles
     */
    public Cursor fetchAllProfiles() {

        return myDb.query(tNAME, cHEADER, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the profile that matches the given rowId
     * 
     * @param id: id of the profile to retrieve
     * @return Cursor positioned to matching profile, if found
     * @throws SQLException if profile could not be found/retrieved
     */
    public Cursor fetchProfile(long id) throws SQLException {

        Cursor myCursor =

            myDb.query(true, tNAME, cHEADER, cHEADER[cID] + "=" + id, null,
                    null, null, null, null);
        if (myCursor != null) {
            myCursor.moveToFirst();
        }
        return myCursor;

    }

    /**
     * Update the profile using the details provided. The profile to be updated is
     * specified using the id, and it is altered to use the parameters passed in
     * 
     * @param id: id of the profile to update
     * @param name: the name of the profile
     * @param type: whether the profile is an auto or manual profile
     * @param wifi: the wifi state of the profile
     * @param bright: the brightness of the profile
     * @param blue: the bluetooth state of the profile
     * @return true if the profile was successfully updated, false otherwise
     */
    public boolean updateProfile
    (long id, String name, int type, int wifi, int bright, int blue) {
    	ContentValues values = new ContentValues();
        values.put(cHEADER[cNAME], name);
        values.put(cHEADER[cTYPE], type);
        values.put(cHEADER[cWIFI], wifi);
        values.put(cHEADER[cBRIGHT], bright);
        values.put(cHEADER[cBLUE], blue);

        return myDb.update(tNAME, values, cHEADER[cID] + "=" + id, null) > 0;
    }
}

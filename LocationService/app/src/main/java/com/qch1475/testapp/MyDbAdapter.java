package com.qch1475.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Adapter providing common methods for accessing and creating location data
 */
public class MyDbAdapter {

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_LONGTITUDE = "longtitude";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_TIME = "time";
	private static final String DB_TABLE = "locations";
	private Context context;
	private SQLiteDatabase db;
	private MyDatabaseHelper dbHelper;

	public MyDbAdapter(Context context) {
		this.context = context;
	}

	public MyDbAdapter open() throws SQLException {
		dbHelper = new MyDatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	//Insert a new location in the database
	public long insertLocation(String longtitude, String latitude, String time) {
		ContentValues values = createContentValues(longtitude, latitude, time);
		return db.insert(DB_TABLE, null, values);
	}

	//Get all of the location entries in the database
	public Cursor getAllLocations() {
		return db.query(DB_TABLE, new String[] { KEY_ROWID, KEY_LONGTITUDE,
				KEY_LATITUDE, KEY_TIME }, null, null, null, null, null);
	}

	private ContentValues createContentValues(String longtitude,
			String latitude, String time) {
		ContentValues values = new ContentValues();
		values.put(KEY_LONGTITUDE, longtitude);
		values.put(KEY_LATITUDE, latitude);
		values.put(KEY_TIME, time);
		return values;
	}
}
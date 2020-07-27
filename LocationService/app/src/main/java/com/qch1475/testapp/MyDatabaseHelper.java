package com.qch1475.testapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteOpenHelper extension with methods to create and
 * upgrade tables
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "locationdata";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table locations ("
		+ "_id integer primary key autoincrement, "
		+ "longtitude text not null, "
		+ "latitude text not null, "
		+ "time text not null);";

	public MyDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the db
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	// Method is called during an upgrade of the database,
	// e.g. if increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		//LocationTable.onUpgrade(database, oldVersion, newVersion);
		Log.i(MyDatabaseHelper.class.getName(), "Upgrading database from version "
			+ oldVersion + " to " + newVersion
			+ ", which will destroy all old data");
		db.execSQL("Drop table if exists todo");
		onCreate(db);
	}

}

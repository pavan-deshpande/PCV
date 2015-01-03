package com.example.datarate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DAOHelper extends SQLiteOpenHelper {

	public static final String TABLE_DATA_RATE = "dataRate";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_RATE = "rate";
	private static final String DATABASE_NAME = "DataRate.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_DATA_RATE + "(" + COLUMN_ID + " integer primary key autoincrement,"
			+ COLUMN_TIME + " double not null, "
			+ COLUMN_DISTANCE  + " double not null, "
			+ COLUMN_RATE  + " double not null);";

	public DAOHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(DAOHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_RATE);
		    onCreate(db);

	}
	
	public void reCreateTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_RATE);
        onCreate(db);
    }

}

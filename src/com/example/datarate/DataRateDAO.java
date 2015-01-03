package com.example.datarate;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataRateDAO {

	private SQLiteDatabase database;
	private DAOHelper dbHelper;
	private String[] allColumns = { DAOHelper.COLUMN_ID,
			DAOHelper.COLUMN_DISTANCE, DAOHelper.COLUMN_TIME, DAOHelper.COLUMN_RATE };

	public DataRateDAO(Context context) {
		dbHelper = new DAOHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long createEntry(DataRateModel model) {

		if (database == null || !database.isOpen())
			open();
		Log.d(DataRateActivity.TAG, "Creating the entry in database");
		Log.d(DataRateActivity.TAG, "Distance is " + model.getDistance());
		Log.d(DataRateActivity.TAG, "Time is " + model.getTime());
		Log.d(DataRateActivity.TAG, "Rate is " + model.getRate());
		ContentValues values = new ContentValues();
		values.put(DAOHelper.COLUMN_DISTANCE, model.getDistance());
		values.put(DAOHelper.COLUMN_TIME, model.getTime());
		values.put(DAOHelper.COLUMN_RATE, model.getRate());
		long insertId = database
				.insert(DAOHelper.TABLE_DATA_RATE, null, values);

		/*
		 * Cursor cursor = database.query(DAOHelper.TABLE_DATA_RATE, allColumns,
		 * DAOHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		 * cursor.moveToFirst(); DataRateModel dr = cursorToDataRate(cursor);
		 * //Comment newComment = cursorToComment(cursor); dr.close();
		 */
		close();
		// return dr;
		return insertId;
	}

	public void deleteALlEntries() {
		open();
		dbHelper.reCreateTable(database);
		close();
	}

	public List<DataRateModel> getAllEntries() {
		if (database == null || !database.isOpen())
			open();
		List<DataRateModel> dataRates = new ArrayList<DataRateModel>();
		Cursor cursor = database.query(DAOHelper.TABLE_DATA_RATE, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DataRateModel model = cursorToDataRate(cursor);
			dataRates.add(model);
			cursor.moveToNext();
		}
		cursor.close();
		close();
		return dataRates;
	}
	
	public List<DataRateModel> getEntriesOnDistance(double distance){
		if (database == null || !database.isOpen())
			open();
		List<DataRateModel> dataRates = new ArrayList<DataRateModel>();
		Cursor cursor = database.query(DAOHelper.TABLE_DATA_RATE, allColumns, DAOHelper.COLUMN_DISTANCE +"=?",
				new String[] {String.valueOf(distance)}, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DataRateModel model = cursorToDataRate(cursor);
			dataRates.add(model);
			cursor.moveToNext();
		}
		cursor.close();
		close();
		return dataRates;
		
	}

	private DataRateModel cursorToDataRate(Cursor cursor) {
		DataRateModel dr = new DataRateModel();
		dr.setId(cursor.getInt(0));
		dr.setDistance(cursor.getInt(1));
		dr.setTime(cursor.getDouble(2));
		dr.setRate(cursor.getDouble(3));
		return dr;
	}
}

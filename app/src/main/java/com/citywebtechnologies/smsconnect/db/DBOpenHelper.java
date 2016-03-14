package com.citywebtechnologies.smsconnect.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "stocksms.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_MESSAGES = "messages";

	public static final String MSG_COLUMN_ID = "id";
	public static final String MSG_COLUMN_REC_ID = "recId";
	public static final String MSG_COLUMN_ADDRESS = "address";
	public static final String MSG_COLUMN_MESSAGE = "message";
	public static final String MSG_COLUMN_SENT_STATUS = "sent_status";
	public static final String MSG_COLUMN_DATE_ADDED = "date_added";

	//added original smsId field to database
	private static final String TABLE_MSG_CREATE = "CREATE TABLE "
			+ TABLE_MESSAGES + " (" + MSG_COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MSG_COLUMN_ADDRESS
			+ " TEXT, " + MSG_COLUMN_MESSAGE + " TEXT, "
			+ MSG_COLUMN_DATE_ADDED + " NUMERIC," + MSG_COLUMN_REC_ID + " INTEGER," + MSG_COLUMN_SENT_STATUS
			+ " INTEGER )";

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_MSG_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		onCreate(db);
	}

}

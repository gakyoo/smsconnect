package com.citywebtechnologies.smsconnect.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.citywebtechnologies.smsconnect.model.ConnectSMS;


public class Datasource {

    private SQLiteOpenHelper dbhelper;
    private SQLiteDatabase database;
    private static String TAG = "Datasource";

    //new column added for storing original smsId
    static final String[] messageColumnNames = {DBOpenHelper.MSG_COLUMN_ID,
            DBOpenHelper.MSG_COLUMN_ADDRESS, DBOpenHelper.MSG_COLUMN_MESSAGE,
            DBOpenHelper.MSG_COLUMN_DATE_ADDED,DBOpenHelper.MSG_COLUMN_REC_ID,
            DBOpenHelper.MSG_COLUMN_SENT_STATUS};

    public Datasource(Context context) {
        dbhelper = new DBOpenHelper(context);
    }

    public void open() {
        database = dbhelper.getWritableDatabase();
    }

    public void close() {
        dbhelper.close();
    }

    public ConnectSMS createMessage(ConnectSMS sms) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.MSG_COLUMN_ADDRESS, sms.getAddress());
        values.put(DBOpenHelper.MSG_COLUMN_MESSAGE, sms.getMessage());
        values.put(DBOpenHelper.MSG_COLUMN_DATE_ADDED, sms.getDateSent());
        values.put(DBOpenHelper.MSG_COLUMN_SENT_STATUS, sms.getSentStatus());
        //store original smsId
        values.put(DBOpenHelper.MSG_COLUMN_REC_ID,sms.getRec());
        long insertid = database.insert(DBOpenHelper.TABLE_MESSAGES, null,
                values);
        Log.i(TAG, "Created a message with id " + insertid);
        sms.setId(insertid);
        return sms;
    }
    //check whether sms exists using original smsId
    public Boolean MessageExists(ConnectSMS sms) {
        String Query = "Select * from " + DBOpenHelper.TABLE_MESSAGES + " where " + DBOpenHelper.MSG_COLUMN_REC_ID + " = " + sms.getRec();
        Log.i(TAG, "Checking existence " + Query.toString());
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            //sms doesnt exists
            return false;
        }
        cursor.close();
        //sms exists
        return true;
    }

    public List<ConnectSMS> findFilterdMessages(String selection, String orderBy) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_MESSAGES,
                messageColumnNames, selection, null, null, null, orderBy);
        List<ConnectSMS> m = getMessageFromCursor(cursor);
        cursor.close();
        return m;
    }


    private List<ConnectSMS> getMessageFromCursor(Cursor cursor) {
        List<ConnectSMS> mList = new ArrayList<ConnectSMS>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ConnectSMS sms = new ConnectSMS();
                sms.setId(cursor.getInt(cursor
                        .getColumnIndex(DBOpenHelper.MSG_COLUMN_ID)));
                sms.setAddress(cursor.getString(cursor
                        .getColumnIndex(DBOpenHelper.MSG_COLUMN_ADDRESS)));
                sms.setMessage(cursor.getString(cursor
                        .getColumnIndex(DBOpenHelper.MSG_COLUMN_MESSAGE)));
                sms.setDateSent(cursor.getLong(cursor
                        .getColumnIndex(DBOpenHelper.MSG_COLUMN_DATE_ADDED)));
                //read original smsId
                sms.setRec(cursor.getInt(cursor
                        .getColumnIndex(DBOpenHelper.MSG_COLUMN_REC_ID)));
                sms.setSendStatus(cursor.getInt(cursor
                        .getColumnIndex(DBOpenHelper.MSG_COLUMN_SENT_STATUS)));

                mList.add(sms);
            }
        }
        return mList;
    }

    public int updateMessageSendStatus(ConnectSMS sms) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.MSG_COLUMN_DATE_ADDED, sms.getDateSent() + "");
        values.put(DBOpenHelper.MSG_COLUMN_SENT_STATUS, sms.getSentStatus());
        return database.update(DBOpenHelper.TABLE_MESSAGES, values,
                DBOpenHelper.MSG_COLUMN_ID + " = " + sms.getId(), null);
    }
}

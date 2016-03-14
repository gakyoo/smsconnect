package com.citywebtechnologies.smsconnect.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.citywebtechnologies.smsconnect.MainActivity;
import com.citywebtechnologies.smsconnect.RestClient;
import com.citywebtechnologies.smsconnect.db.DBOpenHelper;
import com.citywebtechnologies.smsconnect.db.Datasource;
import com.citywebtechnologies.smsconnect.model.ConnectSMS;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SMSConnectSyncPendingMessagesService extends Service {

    public static final String BROADCAST_ACTION = "com.citywebtechnologies.smsconnect.service.SMSConnectSyncPendingMessagesService.Sending";
    private static String TAG = "Emaktaba Sync service";
    Intent intent;
    private Datasource ds;
    private ConnectSMS sms;
    private Boolean running = false;
    List list = Collections.synchronizedList(new ArrayList());
    private void DisplayLoggingInfo() {
        Log.d(TAG, "entered DisplayLoggingInfo");
        try {
            intent.putExtra("time", new Date().toLocaleString());
            sendBroadcast(intent);
        } catch (Exception e) {

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        intent = new Intent(BROADCAST_ACTION);
        ds = new Datasource(getApplicationContext());
        ds.open();
    }

    @Override
    public void onDestroy() {
        ds.close();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (running)
            return super.onStartCommand(intent, flags, startId);
        running = true;
        Log.d(TAG, "Entered sms sync service onStartCommand method");

        String selection = DBOpenHelper.MSG_COLUMN_SENT_STATUS + " != 1 ";
        long DELAY = 10000;
        Log.d(TAG, "queryer = " + selection);
        String orderBy = DBOpenHelper.MSG_COLUMN_ID + " DESC";
        List<ConnectSMS> pendingSms = ds.findFilterdMessages(selection, orderBy);
        int pendingSMSCount = pendingSms.size();
        if (pendingSMSCount > 0) {
            for (int i = 0; i < pendingSMSCount; i++) {

                final long smsId = pendingSms.get(i).getId();
                final long smsId2 = pendingSms.get(i).getRec();
                sms = new ConnectSMS();
                sms.setId(pendingSms.get(i).getId());
                sms.setAddress(pendingSms.get(i).getAddress());
                sms.setMessage(pendingSms.get(i).getMessage());
                sms.setSendStatus(pendingSms.get(i).getSentStatus());
                sms.setDateReceived(pendingSms.get(i).getDateReceived());
                final String ads = sms.getAddress();
                final String msg = sms.getMessage();
                if (sms.getSentStatus() != 2) {
                    if (sms.getSentStatus() != 100){
                        sms.setSendStatus(100);
                        ds.updateMessageSendStatus(sms);
                        Handler handler = new Handler(Looper.getMainLooper());
                        final Runnable r = new Runnable() {
                            public void run() {
                                Log.d(TAG, "Contains = " + list.contains(smsId));
                                Log.d(TAG, "Index of obj = " + list.lastIndexOf(smsId));
                                if (!list.contains(smsId)){
                                    list.add(smsId);
                                    int i =sendMessage(ads, msg, smsId);
                                    Log.d(TAG, "Sends OutPut = " + i);
                                    if(i == 7){
                                        Log.d(TAG, "Sends OutPut 2 = " + i);
                                        ConnectSMS sms2 = new ConnectSMS();
                                        sms2.setId(smsId);
                                        sms2.setDateSent(Calendar.getInstance().getTimeInMillis());
                                        sms2.setSendStatus(6);
                                        ds.updateMessageSendStatus(sms2);
                                        DisplayLoggingInfo();
                                    }
                                }

                            }
                        };
                        handler.postDelayed(r, DELAY);
                        DELAY = DELAY + 10000;
                        Log.d(TAG, "Log = " + DELAY);

                    }else{
                        sms.setSendStatus(99);
                        ds.updateMessageSendStatus(sms);
                    }
                } else {
                    updateServerSendStatus(smsId, smsId2);
                }
/*                try {


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

            }
        } else
            Log.d(TAG, "All is okay");

        DisplayLoggingInfo();
        running=false;
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateServerSendStatus(final long smsId, final long smsId2) {
        RequestParams params = new RequestParams();
        params.add("cmd", "update");
        params.add("status", "sent");
        params.put("sms_id", "" + smsId2);

        Log.d(TAG, "Request parameters " + params.toString());
        RestClient.client.setTimeout(70000);
        RestClient.post("sms.php", params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        String replyMessage = "";
                        sms = new ConnectSMS();
                        sms.setId(smsId);
                        sms.setDateSent(Calendar.getInstance().getTimeInMillis());
                        sms.setSendStatus(1);
                        Log.d(TAG, "Update status from Stock Sync ");

                        try {
                            if (response.getString("status").equalsIgnoreCase("success")) {
                                Log.d(TAG, "Success " + response.toString());
                                ds.updateMessageSendStatus(sms);
                            } else {
                                Log.d(TAG, "Failed " + response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sendBroadcast(intent);
                        //DisplayLoggingInfo();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                        super.onFailure(statusCode, headers, responseBody, e);
                        Log.d(TAG, "Error code " + statusCode + ", Response body " + responseBody);
                    }
                });
    }

    protected int sendMessage(String senderNumber, String replyMessage, long smsId) {
        try {
            Log.d(TAG, "sending id " + smsId);
            String SENT = "SMS_SENT";
            Intent in  =  new Intent(SENT);
            in.putExtra("smsId", smsId);


            PendingIntent sentPI = PendingIntent.getBroadcast(this,(int)smsId,in ,PendingIntent.FLAG_CANCEL_CURRENT);

            //---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver() {
                int rt = 0;
                Bundle b;
                @Override
                public void onReceive(Context arg0, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            //Toast.makeText(getBaseContext(), "SMS sent",Toast.LENGTH_SHORT).show();
                            rt = 2;
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            //Toast.makeText(getBaseContext(), "Generic failure",Toast.LENGTH_SHORT).show();
                            rt = 3;
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            //Toast.makeText(getBaseContext(), "No service",Toast.LENGTH_SHORT).show();
                            rt = 4;
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            //Toast.makeText(getBaseContext(), "Radio off",Toast.LENGTH_SHORT).show();
                            rt = 5;
                            break;
                    }
                    if(intent.getAction().equals("SMS_SENT")){
                        b = intent.getExtras();
                        init();
                    }



                }

                public int init() {

                    sms = new ConnectSMS();
                    sms.setId(b.getLong("smsId"));
                    sms.setDateSent(Calendar.getInstance().getTimeInMillis());
                    sms.setSendStatus(rt);
                    Log.d(TAG, "MessageId : " + b.getLong("smsId") + ", id: " + ds.updateMessageSendStatus(sms));
                    ds.updateMessageSendStatus(sms);
                    Log.d(TAG, "MessageId : " + sms.getId() + ", status: " + sms.getSentStatus());
                    unregisterReceiver(this);
                    Log.d(TAG, "2Contains = " + list.contains(sms.getId()));
                    Log.d(TAG, "2Index of obj = " + list.lastIndexOf(sms.getId()));
                    if (list.contains(sms.getId())){
                        list.remove(sms.getId());
                    }

                    return 1;
                }
            }, new IntentFilter(SENT));
            Log.d(TAG, "Reply message: " + replyMessage + ", number: " + senderNumber);
            SmsManager.getDefault().sendTextMessage(senderNumber, null, replyMessage, sentPI, null);
            return 1;
        } catch (Exception e) {
            if (list.contains(smsId)){
                list.remove(smsId);
            }
            e.printStackTrace();
            return 7;
        }

    }
}

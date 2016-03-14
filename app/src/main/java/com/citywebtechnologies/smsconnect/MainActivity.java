package com.citywebtechnologies.smsconnect;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.citywebtechnologies.smsconnect.db.DBOpenHelper;
import com.citywebtechnologies.smsconnect.db.Datasource;
import com.citywebtechnologies.smsconnect.model.ConnectSMS;
import com.citywebtechnologies.smsconnect.service.DownloadAndSendSMSService;
import com.citywebtechnologies.smsconnect.service.SMSConnectSyncPendingMessagesService;
import com.citywebtechnologies.smsconnect.utils.CommonUtility;

public class MainActivity extends Activity {

    private Context context = this;
    private List<ConnectSMS> connectSMSList;
    private Datasource ds;
    private ListView smsListView;
    private ConnectSMSListAdapter adapter;
    Button b;
    Boolean running = false;
    private static String TAG = "Main Activity";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh(true);
            Log.d(TAG, "refreshing - - " + intent.toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadAndSendSMSService.BROADCAST_ACTION));
        registerReceiver(broadcastReceiver, new IntentFilter(SMSConnectSyncPendingMessagesService.BROADCAST_ACTION));
        //intent = new Intent(this, DownloadAndSendSMSService.class);
        ds = new Datasource(context);
        ds.open();
        smsListView = (ListView) findViewById(R.id.smsList);
        smsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> rootView, View v,
                                    int position, long id) {
                connectSMSList.get(position);
                Toast.makeText(context, "Not implemented for now",
                        Toast.LENGTH_LONG).show();
            }
        });

        //bind refresh button click
        b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(new MyClass());
        CommonUtility.setAlarm(context);
        startService(new Intent(context, SMSConnectSyncPendingMessagesService.class));
        refresh(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_resend:
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void refresh(boolean b) {
        if (running)
            return;
        running = true;
        if (!b){
            startService(new Intent(context,DownloadAndSendSMSService.class));
            startService(new Intent(context, SMSConnectSyncPendingMessagesService.class));
        }

        smsListView.setAdapter(null);

        new Thread(new Runnable() {
            public void run() {
                String orderBy = DBOpenHelper.MSG_COLUMN_ID + " DESC";
                connectSMSList=ds.findFilterdMessages(null,orderBy);

                if(connectSMSList.size()>0)

                {
                    Log.i(TAG, "Found " + connectSMSList.size() + " records");

                    adapter = new ConnectSMSListAdapter(context, connectSMSList);


                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                                smsListView.setAdapter(adapter);

                        }

                    });


                }
                else{
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "No records found \n This could mean remote server has no sms or remote server not queryed yet \n If no data displayed for sometime press home and reopen the application", Toast.LENGTH_LONG).show();

                        }

                    });
                   Log.d(TAG, "No records found");
                }
                running=false;


            }
        }).start();



        }
                //reload view on resume
        @Override
    protected void onResume() {
        ds.open();
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadAndSendSMSService.BROADCAST_ACTION));
        registerReceiver(broadcastReceiver, new IntentFilter(SMSConnectSyncPendingMessagesService.BROADCAST_ACTION));
        refresh(true);
    }

    @Override
    protected void onPause() {
        ds.close();
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        ds.close();
        super.onDestroy();
    }


    public class MyClass implements View.OnClickListener {

        //refresh click event listener
        @Override
        public void onClick(View v) {
            v.setClickable(false);
            refresh(false);
            v.setClickable(true);
        }

    }

}

package com.citywebtechnologies.smsconnect.utils;

import java.util.Calendar;

import com.citywebtechnologies.smsconnect.receiver.AlarmReceiver;
import com.citywebtechnologies.smsconnect.service.SMSConnectSyncPendingMessagesService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CommonUtility {

	public static boolean isNetworkConnectionAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static void setAlarm(Context context) {

		Intent intentAlarm = new Intent(context, AlarmReceiver.class);

		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1,
				intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 6);
		calendar.set(Calendar.MINUTE, 0);
		//some edits happened here
		Intent intent2 = new Intent(context, BroadcastReceiver.class);
		boolean alarmIsSet = (PendingIntent.getBroadcast(context, 0,intent2, PendingIntent.FLAG_NO_CREATE) != null);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		if (!alarmIsSet){
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
		}

	}

	// check battery level
}

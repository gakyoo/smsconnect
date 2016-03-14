package com.citywebtechnologies.smsconnect;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.citywebtechnologies.smsconnect.model.ConnectSMS;

public class ConnectSMSListAdapter extends BaseAdapter {
	Context context;
	List<ConnectSMS> connectSMSList;

	public ConnectSMSListAdapter(Context context, List<ConnectSMS> smsList) {
		this.context = context;
		this.connectSMSList = smsList;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_msg, null);
		}

		ConnectSMS sms = connectSMSList.get(position);

		TextView tv = (TextView) convertView.findViewById(R.id.message);
		tv.setText(sms.getMessage());

		tv = (TextView) convertView.findViewById(R.id.address);
		tv.setText(sms.getAddress());

		tv = (TextView) convertView.findViewById(R.id.sentStatus);
		if (sms.getSentStatus() == 1)
			tv.setText("Sent & server updated");
		else if (sms.getSentStatus() == 2)
			tv.setText("Sent, Waiting server update");
		else if (sms.getSentStatus() == 3)
			tv.setText("Failed, Retrying");
		else if (sms.getSentStatus() == 4)
			tv.setText("No service, Retrying");
		else if (sms.getSentStatus() == 5)
			tv.setText("No radio, Retrying");
		else if (sms.getSentStatus() == 6)
			tv.setText("Permission Denied, Retrying");
		else if (sms.getSentStatus() == 100 || sms.getSentStatus() == 99)
			tv.setText("Queued");
		else
			tv.setText("Pending");
		tv = (TextView) convertView.findViewById(R.id.dateReceived);

		SimpleDateFormat sdf = new SimpleDateFormat("d MMM yy HH:mm",
				Locale.getDefault());

		tv.setText(sdf.format(new Date(sms.getDateSent())));

		return convertView;
	}

	@Override
	public int getCount() {
		return connectSMSList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}

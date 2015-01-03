package com.example.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datarate.DAOActivity;
import com.example.datarate.DAOHelper;
import com.example.datarate.DataRateActivity;
import com.example.datarate.DataRateDAO;
import com.example.pcv.DatabaseToText;
import com.example.pcv.PCVActivity;
import com.example.pcv.R;

public class MainActivity extends Activity {

	public static final String TAG = "mainActivity";
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	public final static String DATA_MESSAGE = "Data Rate Profile";
	public static String PCV_MESSAGE = "PCV message";
	public static String DISTANCE_ENTERED = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		// mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel,
		// this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
		DataRateDAO dao = new DataRateDAO(this);
		DatabaseToText obj = new DatabaseToText(dao);
		
		try {
			InputStream externalDBStream = this.getAssets().open("DataRate.db");
			String filename = "datarate.txt";
			
			OutputStream out = new FileOutputStream(filename);
			
			byte[] buffer = new byte[1024];
		    int bytesRead;
		    while ((bytesRead = externalDBStream.read(buffer)) > 0) {
		        out.write(buffer, 0, bytesRead);
		    }
		    out.close();externalDBStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//obj.writeToText();
		//obj.writebject();
	}

	


	public void dataRate(View view) {
		Intent dataRateIntent = new Intent(this, DataRateActivity.class);
		// dataRateIntent.putExtra(DATA_MESSAGE, "Data Rate Activity");
		
		EditText dist = (EditText) findViewById(R.id.enterDistance);
		int distance;
		if (dist.getText().toString() != null) {
			distance = Integer.parseInt(dist.getText().toString());
		} else {
			distance = 2;
		}
		dataRateIntent.putExtra(DISTANCE_ENTERED, distance);
		MainActivity.this.startActivity(dataRateIntent);

	}

	public void pcv(View view) {
		Intent pcvIntent = new Intent(this, PCVActivity.class);
		pcvIntent.putExtra(PCV_MESSAGE, "PCV Activity");
		startActivity(pcvIntent);
	}

	public void viewDB(View view) {
		Intent viewIntent = new Intent(this, DAOActivity.class);
		startActivity(viewIntent);
	}

	public void clearDB(View view) {
		DataRateDAO dao = new DataRateDAO(this);
		dao.deleteALlEntries();
		Toast.makeText(this, "Database recreated", Toast.LENGTH_SHORT).show();
	}
}

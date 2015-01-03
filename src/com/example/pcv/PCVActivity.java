package com.example.pcv;

import java.net.InetAddress;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.main.MainActivity;
import com.example.pcv.PCVDeviceListFragment.DeviceActionListener;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class PCVActivity extends Activity implements ChannelListener,
		DeviceActionListener , LocationListener{

	public static final String TAG = "contactVolumePrediction";
	private WifiP2pManager manager;
	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;
	public int distance = 1;
	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private BroadcastReceiver receiver = null;
	public int size = 1700;
	public long time;
//	Util util;
	static InetAddress groupOwnerAddress;

	static LocationModel myLocation1;
	static LocationModel peerLocation1;
	

	 
    private LocationManager locationManager;
	private String provider;
	// private TextView accelerometerField;
	private static double radiusEarth = 6378.137;
;
	private Location location;
	double lat;
	double lng;
	double speed;

	/**
	 * @param isWifiP2pEnabled
	 *            the isWifiP2pEnabled to set
	 */
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	
public LocationModel getLocation(){
    	
		
		
		onLocationChanged(location);
		LocationModel myLocation = new LocationModel();
		if (location != null) {
			
			myLocation.setLatitude(lat);
			myLocation.setLongitude(lng);
			myLocation.setVelocity(speed);
			//onLocationChanged(location);
			
		} else {
			
			
			
			// latituteField.setText("Location not available");
			// longitudeField.setText("Location not available");
		}
		
		return myLocation;
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// add necessary intent values to be matched.
		
		myLocation1 = null;

		
		peerLocation1 = null;
	
		
		

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);

		Intent intent = getIntent();
		distance = intent.getIntExtra(MainActivity.DISTANCE_ENTERED, 10);
		PCVDeviceDetailFragment.distance = distance;
		setContentView(R.layout.pcv_main);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    	Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, 0, 0, this);
		location = locationManager.getLastKnownLocation(provider);
	//	util = new Util(this);
		//System.out.println(util.getLocation());
		//myLocation1 = util.getLocation();
		
		
		

	}
	
	
	/** register the BroadcastReceiver with the intent values to be matched */
	@Override
	public void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 0, 0, this);
		receiver = new PCVBroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
		unregisterReceiver(receiver);
	}

	/**
	 * Remove all peers and clear all fields. This is called on
	 * BroadcastReceiver receiving a state change event.
	 */
	public void resetData() {
		PCVDeviceListFragment fragmentList = (PCVDeviceListFragment) getFragmentManager()
				.findFragmentById(R.id.frag_list);
		PCVDeviceDetailFragment fragmentDetails = (PCVDeviceDetailFragment) getFragmentManager()
				.findFragmentById(R.id.frag_detail);
		if (fragmentList != null) {
			fragmentList.clearPeers();
		}
		if (fragmentDetails != null) {
			fragmentDetails.resetViews();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atn_direct_enable:
			if (manager != null && channel != null) {

				// Since this is the system wireless settings activity, it's
				// not going to send us a result. We will be notified by
				// WiFiDeviceBroadcastReceiver instead.

				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			} else {
				Log.e(TAG, "channel or manager is null");
			}
			return true;

		case R.id.atn_direct_discover:
			if (!isWifiP2pEnabled) {
				Toast.makeText(PCVActivity.this, R.string.p2p_off_warning,
						Toast.LENGTH_SHORT).show();
				return true;
			}
			final PCVDeviceListFragment fragment = (PCVDeviceListFragment) getFragmentManager()
					.findFragmentById(R.id.frag_list);
			fragment.onInitiateDiscovery();
			manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

				@Override
				public void onSuccess() {
					Toast.makeText(PCVActivity.this, "Discovery Initiated",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(int reasonCode) {
					Toast.makeText(PCVActivity.this,
							"Discovery Failed : " + reasonCode,
							Toast.LENGTH_SHORT).show();
				}
			});
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void showDetails(WifiP2pDevice device) {
		PCVDeviceDetailFragment fragment = (PCVDeviceDetailFragment) getFragmentManager()
				.findFragmentById(R.id.frag_detail);
		fragment.showDetails(device);

	}

	@Override
	public void connect(WifiP2pConfig config) {
		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				
				
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(PCVActivity.this, "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void disconnect() {
		final PCVDeviceDetailFragment fragment = (PCVDeviceDetailFragment) getFragmentManager()
				.findFragmentById(R.id.frag_detail);
		fragment.resetViews();
		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onFailure(int reasonCode) {
				Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

			}

			@Override
			public void onSuccess() {
				fragment.getView().setVisibility(View.GONE);
			}

		});
	}

	@Override
	public void onChannelDisconnected() {
		// we will try once more
		if (manager != null && !retryChannel) {
			Toast.makeText(this, "Channel lost. Trying again",
					Toast.LENGTH_LONG).show();
			resetData();
			retryChannel = true;
			manager.initialize(this, getMainLooper(), this);
		} else {
			Toast.makeText(
					this,
					"Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void cancelDisconnect() {

		/*
		 * A cancel abort request by user. Disconnect i.e. removeGroup if
		 * already connected. Else, request WifiP2pManager to abort the ongoing
		 * request
		 */
		if (manager != null) {
			final PCVDeviceListFragment fragment = (PCVDeviceListFragment) getFragmentManager()
					.findFragmentById(R.id.frag_list);
			if (fragment.getDevice() == null
					|| fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
				disconnect();
			} else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
					|| fragment.getDevice().status == WifiP2pDevice.INVITED) {

				manager.cancelConnect(channel, new ActionListener() {

					@Override
					public void onSuccess() {
						Toast.makeText(PCVActivity.this, "Aborting connection",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailure(int reasonCode) {
						Toast.makeText(
								PCVActivity.this,
								"Connect abort request failed. Reason Code: "
										+ reasonCode, Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		 lat = (double) (location.getLatitude());
		 lng = (double) (location.getLongitude());
		 speed = (double) (location.getSpeed());
		 // latituteField.setText("Latitude: " + String.valueOf(lat));
		// longitudeField.setText("Longitude: " + String.valueOf(lng));
	//	calculateDistance(lat, lng, location);
		
	}
	
	private void calculateDistance(double lat, double lng, Location location) {

		double lat1 = (double) (location.getLatitude());
		double lng1 = (double) (location.getLongitude());
		// latituteField.setText("Latitude: " + String.valueOf(lat1));
		// longitudeField.setText("Longitude " + String.valueOf(lng1));
		/*
		 * double dlat = lat1 - lat; double dlng = lng1 - lng;
		 * 
		 * double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat)
		 * Math.cos(lat1) * Math.pow(Math.sin(dlng / 2), 2); // c = 2 * atan2(
		 * sqrt(a), sqrt(1-a) ) // d = R * c (where R is the radius of the
		 * Earth)
		 * 
		 * double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		 * 
		 * double distance = radiusEarth * c;
		 */

		double dlat = (lat1 - lat) * Math.PI / 180;
		double dlng = (lng1 - lng) * Math.PI / 180;

		double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
				+ Math.cos(lat * Math.PI / 180)
				* Math.cos(lat1 * Math.PI / 180) * Math.sin(dlng / 2)
				* Math.sin(dlng / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = radiusEarth * c;
		double distance = d * 1000;
		
		 Log.d(PCVActivity.TAG, ""+distance);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}

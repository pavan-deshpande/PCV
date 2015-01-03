/*
 * Copyright (C) 2011 The Android Open Source Project

 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pcv;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datarate.DataRateDAO;
import com.example.datarate.DataRateModel;
import com.example.pcv.PCVDeviceListFragment.DeviceActionListener;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class PCVDeviceDetailFragment extends Fragment implements
		ConnectionInfoListener {

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private View mContentView = null;
	private WifiP2pDevice device;
	// private WifiP2pInfo info;
	ProgressDialog progressDialog = null;
	static int distance;
	Context context = null;

	Util util;
	PCVActivity pActivity;
	DataRateDAO dao;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();
		dao = new DataRateDAO(context);
		
		pActivity = (PCVActivity) context;
		util = new Util(pActivity);
		mContentView = inflater.inflate(R.layout.pcvdevice_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						WifiP2pConfig config = new WifiP2pConfig();
						config.deviceAddress = device.deviceAddress;
						config.wps.setup = WpsInfo.PBC;
						if (progressDialog != null
								&& progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						progressDialog = ProgressDialog.show(getActivity(),
								"Press back to cancel", "Connecting to :"
										+ device.deviceAddress, true, true
						// new DialogInterface.OnCancelListener() {
						//
						// @Override
						// public void onCancel(DialogInterface dialog) {
						// ((DeviceActionListener)
						// getActivity()).cancelDisconnect();
						// }
						// }
								);
						((DeviceActionListener) getActivity()).connect(config);

					}
				});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((DeviceActionListener) getActivity()).disconnect();
					}
				});

		mContentView.findViewById(R.id.predict_contact_volume)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("predict volume");
						//PCVActivity.myLocation1 = pActivity.getLocation();
						final double contactVolume = predict() / 1024;
						
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								DecimalFormat df = new DecimalFormat("#.##");
								Toast.makeText(
										pActivity,
										"Contact Volume Predicted is: "
												+ df.format(contactVolume) + " MB",
										Toast.LENGTH_LONG).show();

							}
						});

					}
				});

		return mContentView;
	}

	public double predict() {
		dao.open();
		ContactVolumePrediction obj = new ContactVolumePrediction();
		System.out.println("My Location " + PCVActivity.myLocation1);
		System.out.println("Peer Location " + PCVActivity.peerLocation1);
		PCVActivity.myLocation1.setVelocity(0.0);
		if (PCVActivity.peerLocation1.getVelocity() < 1.0) {
			PCVActivity.peerLocation1.setVelocity(1.0);
		}
		List<Integer> distanceList = obj.predictContactVolume(
				PCVActivity.myLocation1, PCVActivity.peerLocation1);
		List<DataRateModel> list = new ArrayList<DataRateModel>();
		for (int i = 0; i < distanceList.size(); i++) {

			List<DataRateModel> tempList = dao
					.getEntriesOnDistance(distanceList.get(i));
			for (int j = 0; j < tempList.size(); j++) {
				list.add(tempList.get(j));
				System.out.println(tempList.get(j).getRate());
			}
		}
		double contactVolume = 0.0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != null) {
				
				contactVolume += list.get(i).getRate();
			}
		}
		dao.close();
		return contactVolume;

	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		// this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		PCVActivity.groupOwnerAddress = info.groupOwnerAddress;
		// System.out.println(util.getLocation());


		PCVActivity.myLocation1 = pActivity.getLocation();
		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(
						R.string.yes) : getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - "
				+ info.groupOwnerAddress.getHostAddress());

		// After the group negotiation, we assign the group owner as the file
		// server. The file server is single threaded, single connection server
		// socket.
		mContentView.findViewById(R.id.send_receive).setVisibility(View.VISIBLE);
		mContentView.findViewById(R.id.predict_contact_volume)
		.setVisibility(View.VISIBLE);
		if (info.groupFormed && info.isGroupOwner) {

			// mContentView.findViewById(R.id.send_receive).setVisibility(
			// View.VISIBLE);
			

			new ServerAsyncTask(context, Util.SERVER_SOCKET, info).execute();
			
		} else if (info.groupFormed) {
			// The other device acts as the client. In this case, we enable the
			// get file button.
			// mContentView.findViewById(R.id.send_receive).setVisibility(
			// View.VISIBLE);
			
			
			Intent serviceIntent = new Intent(getActivity(),
					DataTransferService.class);
			serviceIntent.setAction(DataTransferService.ACTION_SEND_DATA);
			serviceIntent.putExtra(
					DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
					info.groupOwnerAddress.getHostAddress());

			serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT,
					Util.SERVER_SOCKET);

			getActivity().startService(serviceIntent);

			// new ClientAsyncTask((PCVActivity) getActivity()).execute();
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}

	/**
	 * Updates the UI with device data
	 * 
	 * @param device
	 *            the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView
				.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView
				.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.send_receive).setVisibility(View.GONE);
		mContentView.findViewById(R.id.predict_contact_volume).setVisibility(
				View.GONE);
		this.getView().setVisibility(View.GONE);
	}

}

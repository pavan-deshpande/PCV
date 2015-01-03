package com.example.pcv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;

public class ServerAsyncTask extends AsyncTask<Void, Void, String> {

	private PCVActivity pActivity;
	private int port;
	
	private List<LocationModel> peerList = null;
	private List<LocationModel> myList = null;
	
	WifiP2pInfo info;

	public ServerAsyncTask(Context activity, int port, WifiP2pInfo info) {
		pActivity = (PCVActivity) activity;
		this.port = port;
		this.info = info;
	}

	@Override
	protected void onPreExecute() {
		peerList = null;
		myList = new ArrayList<LocationModel>();
		myList.add(PCVActivity.myLocation1);
	//	myList.add(PCVActivity.myLocation2);

	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doInBackground(Void... params) {
		ServerSocket socket = null;
		Socket client = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			socket = new ServerSocket(port);
			
			client = socket.accept();
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());
			while (peerList == null) {
				peerList = (List<LocationModel>) ois.readObject();
			}
			oos.writeObject(myList);
			oos.flush();
			PCVActivity.peerLocation1 = peerList.get(0);
			//PCVActivity.peerLocation2 = peerList.get(1);
			
			System.out.println("Server");
			System.out.println(myList.get(0));
           // System.out.println(myList.get(1));
           System.out.println(peerList.get(0));
           //System.out.println(peerList.get(1));
			return peerList.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return e.getMessage();
		}  finally {

			if (!socket.isClosed())
				try {
					ois.close();
					oos.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	protected void onPostExecute(final String result) {
		super.onPostExecute(result);
		new ServerAsyncTask(pActivity, Util.SERVER_SOCKET, info).execute();
	}

}

package com.example.pcv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DataTransferService extends IntentService {

	public static final String ACTION_SEND_DATA = "SEND_DATA";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	private static final int SOCKET_TIME_OUT = 5000;
	List<LocationModel> myList , peerList;

	public DataTransferService(String name) {
		super(name);
	}

	public DataTransferService() {
		super("DataTransferService");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandleIntent(Intent intent) {
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		myList = new ArrayList<LocationModel>();
		myList.add(PCVActivity.myLocation1);
		//myList.add(PCVActivity.myLocation2);
		
		
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
        if (intent.getAction().equals(ACTION_SEND_DATA)) {
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
           

            try {
            	
                Log.d(PCVActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIME_OUT);

                Log.d(PCVActivity.TAG, "Client socket - " + socket.isConnected());
                oos = new ObjectOutputStream(socket.getOutputStream());
    			ois = new ObjectInputStream(socket.getInputStream());
    			oos.writeObject(myList);
    			oos.flush();
    			
    			while (peerList == null) {
    				peerList = (List<LocationModel>) ois.readObject();
    			}

    			PCVActivity.peerLocation1 = peerList.get(0);
    			//PCVActivity.peerLocation2 = peerList.get(1);

    			System.out.println("Client");
            //   System.out.println(myList.get(0));
                //System.out.println(myList.get(1));
              // System.out.println(peerList.get(0));
           //    System.out.println(peerList.get(1));
                
                
                Log.d(PCVActivity.TAG, "Client: Data written");
               
               
               
            } catch (IOException e) {
                Log.e(PCVActivity.TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
            	Log.e(PCVActivity.TAG, e.getMessage());
			} finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
	}

}

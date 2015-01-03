package com.example.pcv;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class Util implements SensorEventListener2 {
    public static final String TAG = Util.class.getSimpleName();

    private PCVActivity pActivity;
    
  //  private LocationManager locationManager;
	//private String provider;
	//private Location location;
	// private TextView accelerometerField;
	private static double radiusEarth = 6378.137;
	private SensorManager sensorManager;
	private long lastUpdate;
	private boolean color = false;
	
	double lat;
	double lng;
	double speed;
	
	
	
	
	
    public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public static final int SLEEP_TIME_LONG = 15000;
    public static final int SLEEP_TIME_MEDIUM = 5000;
    public static final int SLEEP_TIME_SHORT = 3000;
    public static final int SERVER_SOCKET = 5000;

    public final static String D1 = "fa:a9:d0:02:21:08";
    public final static String D2 = "fa:a9:d0:03:e0:1b";
    public final static String D4 = "fa:a9:d0:1c:03:16";
    public final static String N1 = "fa:a9:d0:07:64:d7";
    public final static String N2 = "fa:a9:d0:03:ea:82";

    static String thisDeviceAddress;

    public Util(PCVActivity activity){
    	pActivity = activity;
    //	locationManager.removeUpdates(this);
    	/*locationManager = (LocationManager) pActivity.getSystemService(Context.LOCATION_SERVICE);

    	Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, 0, 0, this);
		location = locationManager.getLastKnownLocation(provider);
		*/
    }

	static final long startTime = System.currentTimeMillis();
	 /*
    public LocationModel getLocation(){
    	
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void onLocationChanged(Location location) {
		 lat = (double) (location.getLatitude());
		 lng = (double) (location.getLongitude());
		 speed = (double) (location.getSpeed());
		 // latituteField.setText("Latitude: " + String.valueOf(lat));
		// longitudeField.setText("Longitude: " + String.valueOf(lng));
		calculateDistance(lat, lng, location);
	}
    
	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(pActivity, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
		
	}
	
	
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(pActivity, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();
		
	}



	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void unregisterLocation(){
		locationManager.removeUpdates(this);
	}
	*/

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
		// locationDistance.setText("" + location.getSpeed());
		System.out.println(distance);
	}





    public void getAcceleration(){
    	//	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    		
    		sensorManager.registerListener(this,
    				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
    				SensorManager.SENSOR_DELAY_NORMAL);
    	
    		sensorManager = (SensorManager) pActivity.getSystemService("sensor");
    		lastUpdate = System.currentTimeMillis();
    	}


    static boolean stopUpdate = false;

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}		
	}


	public void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		double x = values[0];
		double y = values[1];
		double z = values[2];

		double accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = event.timestamp;
		if (accelationSquareRoot >= 2) //
		{
			if (actualTime - lastUpdate < 200) {
				return;
			}
			lastUpdate = actualTime;
			Toast.makeText(pActivity, "Device was shuffed", Toast.LENGTH_SHORT)
					.show();
			// accelerometerField.setText(""+accelationSquareRoot);
			if (color) {
				// accelerometerField.setBackgroundColor(Color.GREEN);
			} else {
				// accelerometerField.setBackgroundColor(Color.RED);
			}
			color = !color;
		}
	}

	@Override
	public void onFlushCompleted(Sensor sensor) {
		// TODO Auto-generated method stub
		
	}


	

	
	
}

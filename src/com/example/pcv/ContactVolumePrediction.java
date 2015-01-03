
package com.example.pcv;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;


public class ContactVolumePrediction {

	LocationModel myLocation, peerLocation;
	// LocationModel myLocation2,peerlocation2;
	double matrix3[][];
	double t0;
	int count = 0;

	

	public List<Integer> predictContactVolume(LocationModel model1, LocationModel model2) {
		
		myLocation = clone(model1);
		// myLocation2 = clone(PCVActivity.myLocation2);

		peerLocation = clone(model2);
		
		// peerlocation2 = clone(PCVActivity.peerLocation2);
		// changeLocation(myLocation2);
		// changeLocation(peerlocation2);

		Vector<Double> p1 = new Vector<Double>();
		p1.add(0.0);
		p1.add(0.0);

		Vector<Double> p2 = new Vector<Double>();
		p2.add(Math.abs((peerLocation.getLatitude() - myLocation.getLatitude()) * 6371000));
		p2.add(Math.abs((peerLocation.getLongitude() - myLocation.getLongitude()) * 6371000));

		Vector<Double> v1 = new Vector<Double>();
		v1.add(0.0);
		v1.add(0.0);

		Vector<Double> v2 = new Vector<Double>();
		v2.add(Math.abs(peerLocation.getVelocity() - myLocation.getVelocity() + 0.1));
		v2.add(Math.abs(peerLocation.getVelocity() - myLocation.getVelocity() + 0.1));

		
		int range = 150;

		double m = (peerLocation.getVelocity() - myLocation.getVelocity())
				/ (peerLocation.getVelocity() - myLocation.getVelocity() + 0.1);

		double sigma = ((peerLocation.getLatitude() - myLocation.getLatitude()) * 6371000)
				- ((peerLocation.getVelocity() - myLocation.getVelocity()
						/ peerLocation.getVelocity() - myLocation.getVelocity() + 0.1) * ((peerLocation
						.getLatitude() - myLocation.getLatitude()) * 6371000));

		double a = m + 1;

		double b = 2 * m * sigma;

		double c = Math.pow(sigma, 2) - range;

		double root1 = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c)) / (2 * a));
		
		double root2 = (-b - Math.sqrt(Math.pow(b, 2) - (4 * a * c)) / (2 * a));
		
		double displacement = Math.abs(root1 - root2);
		
		double contactDuration = displacement/ (peerLocation.getVelocity() + 0.1);
		
		t0 = contactDuration;
		
		double theta = Math.atan(peerLocation.getVelocity()/( myLocation.getVelocity() + 0.1));
		
		double matrix1[][] = {{Math.cos(theta), Math.sin(theta)},
				              {-Math.sin(theta), Math.cos(theta)}};
		
		double matrix2[][] = {{v1.get(0), v2.get(0), root1, root2},
								{v1.get(1), v2.get(1), root1, root2}};
		
		matrix3 =  multiply(matrix1, matrix2);
		System.out.println("In Contact Volume " +(int)t0);
		
		
		List<Integer> distanceList = new ArrayList<Integer>();
		
		for(int i = 0; i < t0; i = i+2){
			int temp = (int) instantneousDistance(i);
			distanceList.add(temp);
			System.out.println(temp);
			
		}
		System.out.println("========");
		return distanceList;
	}
	
	
	 
	
	
	public double instantneousDistance(double t){
		
		double x = matrix3[0][2]  + (matrix3[0][1] * t);
		double y = matrix3[1][2];
		
		double d = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		
		return d;
	}
	

	public LocationModel clone(LocationModel model) {

		LocationModel m = new LocationModel();
		m.setLatitude(model.getLatitude());
		m.setLongitude(model.getLongitude());
		m.setVelocity(model.getVelocity());

		return m;
	}

	public void changeLocation(LocationModel model) {
		Random ra = new Random();
		int num = ra.nextInt(9999);

		int num2 = ra.nextInt(9999);

		double n1 = num / 100000000;
		double n2 = num2 / 100000000;

		model.setLatitude(model.getLatitude() + n1);
		model.setLongitude(model.getLongitude() + n2);
	}

	public static double[][] multiply(double a[][], double b[][]) {
		   
		  int aRows = a.length,
		      aColumns = a[0].length,
		      bRows = b.length,
		      bColumns = b[0].length;
		   
		  if ( aColumns != bRows ) {
		    throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		  }
		   
		  double[][] resultant = new double[aRows][bColumns];
		   
		  for(int i = 0; i < aRows; i++) { // aRow
		    for(int j = 0; j < bColumns; j++) { // bColumn
		      for(int k = 0; k < aColumns; k++) { // aColumn
		        resultant[i][j] += a[i][k] * b[k][j];
		      }
		    }  
		  }
		   
		  return resultant;
		}
	
	
	public static void main(String[] args) {
		LocationModel model1 = new LocationModel();
		
		LocationModel model2 = new LocationModel();
		model1.setLatitude(43.10260284);
		model1.setLongitude(-77.63261139);
		model1.setVelocity(0.0);
		
		model2.setLatitude(43.10260375);
		model2.setLongitude(-77.63261603);
		model2.setVelocity(1.0);
		
		ContactVolumePrediction obj = new ContactVolumePrediction();
		obj.predictContactVolume(model1, model2);
	}
}

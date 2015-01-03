package com.example.pcv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import com.example.datarate.DataRateDAO;
import com.example.datarate.DataRateModel;

public class DatabaseToText {

	DataRateDAO dao;
	public DatabaseToText (DataRateDAO dao){
		this.dao = dao;
	}
	
	
	public void writebject(){
		dao.open();
		List<DataRateModel> list = dao.getAllEntries();
		
		dao.close();
		for(int i = 0; i < list.size(); i++){
			DataRateModel model = list.get(i);
			System.out.println(model.getId()+ " , "+model.getDistance() + ", "+model.getRate());
		}
		
	}
		public void writeToText(){
			dao.open();
			List<DataRateModel> list = dao.getAllEntries();
			
			dao.close();
			
			BufferedWriter writer = null;
			
			File file = new File("datarate.txt");
			
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				
				for(int i = 0; i < list.size(); i++){
					DataRateModel model = list.get(i);
					
					writer.append(model.getId()+ " , "+model.getDistance() + " "+model.getDistance() +"\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				}
			}
					
		}
}

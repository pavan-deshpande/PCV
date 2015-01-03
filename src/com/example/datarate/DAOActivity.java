package com.example.datarate;

import java.util.List;

import com.example.pcv.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DAOActivity extends ListActivity{

	private DataRateDAO dao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);
		
		dao = new DataRateDAO(this);
		dao.open();
		List<DataRateModel> dataRates = dao.getAllEntries();
		  ArrayAdapter<DataRateModel> adapter = new ArrayAdapter<DataRateModel>(this,
	                android.R.layout.simple_list_item_1, dataRates);
	        setListAdapter(adapter);
	}
	
	  @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);
	        DataRateModel model = (DataRateModel) l.getItemAtPosition(position);
	        String toast = model.toString();
	        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	    }
	
	@Override
    protected void onResume() {
        super.onResume();
        dao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dao.close();
    }
}

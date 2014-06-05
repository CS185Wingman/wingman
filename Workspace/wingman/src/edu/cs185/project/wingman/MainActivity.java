package edu.cs185.project.wingman;

import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.os.Build;
import android.content.res.AssetManager;
import java.io.InputStream;
import android.graphics.*;


public class MainActivity extends ActionBarActivity implements
		OnItemClickListener {

	Dialog dialog;
	double BACValue = 0.0;
	TextView BACNumberLabel;
	Drawable BACChartDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		BACNumberLabel = (TextView) findViewById(R.id.BACNumber);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void BACClick(View v) {
		dialog = new Dialog(this);

		// set title
		dialog.setTitle("Drink Selection");
		dialog.setContentView(R.layout.chart_popup);
		
		ImageView iv = (ImageView) dialog.findViewById(R.id.chartIV);
		AssetManager am = getAssets();
		InputStream is = null;
		try{
			is = am.open("res/drawable-hdpi/bac_chart.jpg");
			System.out.println("Found the file");
		}
		catch(IOException e){
			e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(is);
		iv.setImageBitmap(bm);

		LayoutInflater li = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View vi = li.inflate(R.layout.chart_popup, null);
		View view = vi.getRootView();
		
		dialog.setContentView(vi);
		dialog.setCancelable(true);
		
		dialog.show();
		/*
		Log.d("OnClick", "BAC Clicked");
		System.out.println(BACNumberLabel.getText());
		// BACChart.setBackground(background);
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
				.create();
		alertDialog.setMessage("This is where the chart will go");
		// alertDialog.addContentView(BACChart, new LayoutParams(null, null));
		alertDialog.setTitle("BAC Chart");
		// alertDialog.
		alertDialog.show();*/
	}

	//Updates the number of the label
	public void updateBACLabel(double value){
		BACNumberLabel.setText("" + value);
		if (value == 0){
			BACNumberLabel.setTextColor(getResources().getColor(R.color.green));
		}
		else if (value <= 0.07){
			BACNumberLabel.setTextColor(getResources().getColor(R.color.orange));
		}
		else{
			BACNumberLabel.setTextColor(getResources().getColor(R.color.red));
			AlertDialog warning = new AlertDialog.Builder(MainActivity.this).create();
			warning.setMessage("You have a very high BAC. Consider drinking water and holding off drinking more alcohol.");
			warning.setTitle("Warning: High BAC Level");
		}
		//do check here for if value is getting too high
		//if so, change the color to orange/yellow
		//If necessary, also notify the user with an alert that they shouldn't drink more
	}
	
	public void selectDrink(View v) {

		dialog = new Dialog(this);

		// set title
		dialog.setTitle("Drink Selection");
		dialog.setContentView(R.layout.select_drink);

		LayoutInflater li = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View vi = li.inflate(R.layout.select_drink, null);
		View view = vi.getRootView();

		dialog.setContentView(vi);
		dialog.setCancelable(true);

		ListView dL = (ListView) dialog.findViewById(R.id.dList);
		dL.setOnItemClickListener(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this.getBaseContext(), android.R.layout.simple_list_item_1,
				DRINKS);

		dL.setAdapter(adapter);

		dialog.show();

	}

	public void onItemClick(AdapterView<?> arg, View arg1, int arg2, long arg3) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String dSelected = DRINKS[arg2];

		TextView drinkLabel = (TextView) findViewById(R.id.noDrinkSelected);
		drinkLabel.setText(dSelected);

		dialog.cancel();

		/**
		 * THIS IS FOR IF WE WANT EXTRA DIALOG FOR DISPLAYING INFO ABOUT DRINK?
		 * builder.setMessage("Selecting " +dSelected).setPositiveButton("OK",
		 * new DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog2, int which) {
		 * 
		 *           TextView drinkLabel = (TextView)
		 *           findViewById(R.id.noDrinkSelected);
		 *           drinkLabel.setText(dSelected);
		 * 
		 *           dialog2.dismiss(); dialog.cancel();
		 * 
		 *           } });
		 * 
		 *           builder.setNegativeButton("Cancel", new
		 *           DialogInterface.OnClickListener(){
		 * @Override public void onClick(DialogInterface dialog2, int which) {
		 *           dialog2.dismiss(); dialog.cancel();
		 * 
		 *           }
		 * 
		 * 
		 *           });
		 **/

		// AlertDialog alert = builder.create();
		// alert.show();
	}

	private static final String[] DRINKS = new String[] { "Beer", "Wines",
			"etc" };

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}

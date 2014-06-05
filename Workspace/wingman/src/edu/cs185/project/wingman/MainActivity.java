package edu.cs185.project.wingman;

import java.io.InputStream;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

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
		//BitmapFactory bf = new BitmapFactory();
		//bf.decodeFile("asset/PBSA-BAC-Chart.jpg");
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

	public void BACClick(View v) {
		
		//ImageView BACChart= (ImageView)findViewById(R.id.imageView1);  
		//int resID = getResources().getIdentifier("PBSA-BAC-Chart", "drawable",  getPackageName());
		//BACChart.setImageResource(resID);
		Log.d("OnClick", "BAC Clicked");
		System.out.println(BACNumberLabel.getText());
		//BACChart.setBackground(background);
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
				.create();
		alertDialog.setMessage("This is where the chart will go");
		//alertDialog.addContentView(BACChart, new LayoutParams(null, null));
		alertDialog.setTitle("BAC Chart");
		//alertDialog.
		alertDialog.show();
	}

}

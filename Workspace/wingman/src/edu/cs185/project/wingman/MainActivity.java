package edu.cs185.project.wingman;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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
	/* To Calculate Blood Alcohol Levels */
	Dialog dialog;
	double BACValue = 0.0;
	TextView BACNumberLabel;
	Drawable BACChartDrawable;
	static int DRUNK, /* State there in, status to go along with BAC */
	BUZZING_HARD, BUZZING, ON_A_GOOD_ONE, HEAD_CHANGE, PRACTICALLY_SOBER,
			SOBER;

	double BAC, LastHour, LastMinute, numDrinks;
	/* Some constants for the BAC formula */
	double STANDARD_DRINKS, BODY_WATER_CONSTANT, /* .806 */
	METABOLISM_CONSTANT, /* .017 */
	BODY_WATER_GENDER, WEIGHT, /*
								 * In kilograms, conversion eminent because
								 * America.
								 */
	SWEDISH_CONVERTER; /* 1.2 */
	double DRINKING_PERIOD[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BODY_WATER_GENDER = .58; /* default male */
		WEIGHT = 85; /* default weight, will be changed eventually */
		LastHour = LastMinute = 0.0; /* initial values */
		DRINKING_PERIOD = new double[2];
		numDrinks=0;
		METABOLISM_CONSTANT=0.017;
		SWEDISH_CONVERTER=1.2;
		BODY_WATER_CONSTANT=.806;
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
		android.app.ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.teal)));
		bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayShowTitleEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			launchSettings();
		}
		else if(id == R.id.action_help){
			launchHelp();
		}
		else if(id == R.id.action_drink_list){
			launchPastDrinks();
		}
		return super.onOptionsItemSelected(item);
	}

	public void BACClick(View v) {
		dialog = new Dialog(this);
		dialog.setTitle("BAC Chart");
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.offwhite)));

		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.bac_chart);
		dialog.setCancelable(true);
		dialog.setContentView(iv);
		dialog.show();
	}

	// Updates the number of the label
	public void updateBAC(double value) {
		BACNumberLabel.setText("" + value);
		if (value == 0) {
			BACNumberLabel.setTextColor(getResources().getColor(R.color.green));
		} else if (value <= 0.07) {
			BACNumberLabel
					.setTextColor(getResources().getColor(R.color.orange));
		} else {
			BACNumberLabel.setTextColor(getResources().getColor(R.color.red));
			AlertDialog warning = new AlertDialog.Builder(MainActivity.this)
					.create();
			warning.setMessage("You have a very high BAC. Consider drinking water and holding off drinking more alcohol.");
			warning.setTitle("Warning: High BAC Level");
		}
		// do check here for if value is getting too high
		// if so, change the color to orange/yellow
		// If necessary, also notify the user with an alert that they shouldn't
		// drink more
	}

	//adds a new drink
	public void addDrink(View v){
		numDrinks++;
		System.out.println("New Drink Added (Number "+numDrinks+" of the night), BAC="+tequilaMockingbird(numDrinks, 60));
	}
	
	//calculates BAC value
	public double tequilaMockingbird(double drink_number, double drinking_time) {
		double top, bottom, right;
		top = BODY_WATER_CONSTANT * drink_number * SWEDISH_CONVERTER;
		bottom = BODY_WATER_GENDER * WEIGHT;
		right = METABOLISM_CONSTANT * drinking_time;
		BAC = (top / bottom) - right;
		System.out.println("BWC="+BODY_WATER_CONSTANT+" Swed="+SWEDISH_CONVERTER+" Metab="+METABOLISM_CONSTANT+" time="+drinking_time);
		System.out.println("Top="+top+" Bottom="+bottom+" right="+right);
		updateBAC(BAC);//update label
		return BAC;

	}

	public void selectDrink(View v) {

		dialog = new Dialog(this);

		// set title
		dialog.setTitle("Drink Selection");
		dialog.setContentView(R.layout.select_drink);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.offwhite)));

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
		double mHour, mMinute;
		TextView drinkLabel = (TextView) findViewById(R.id.noDrinkSelected);
		drinkLabel.setText(dSelected);
		
		System.out
				.println("LastHour:" + LastHour + " LastMinute:" + LastMinute);
		if (DRINKING_PERIOD[0] == 0.0) {
			DRINKING_PERIOD[0] = 1.0;
			DRINKING_PERIOD[1] = 0.0;
			final Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			mHour = date.getHours();
			mMinute = date.getMinutes();
		} else {
			final Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			mHour = date.getHours();
			mMinute = date.getMinutes();

			if (LastHour != 0 && LastHour == mHour) {
				double time_diff = mMinute - LastMinute;
				DRINKING_PERIOD[1] += time_diff; // Sets appropriate Minute 
				if (DRINKING_PERIOD[1] >= 60) {// if minute > 60 
					DRINKING_PERIOD[0]++;
					DRINKING_PERIOD[1] -= 60;
				}
			} else if (LastHour != 0 && LastHour != mHour) {
				if (LastMinute > mMinute) {
					mHour--;
					mMinute += 60;
					mMinute *= -1;
					DRINKING_PERIOD[0] += (mHour - LastHour);
					DRINKING_PERIOD[1] += (mMinute - LastMinute);
					if (DRINKING_PERIOD[1] >= 60) { // If minute is > 60
						DRINKING_PERIOD[0]++;
						DRINKING_PERIOD[1] -= 60;
					}

				} else {
					DRINKING_PERIOD[0] += (mHour - LastHour);
					DRINKING_PERIOD[1] += (mMinute - LastMinute);
					if (DRINKING_PERIOD[1] >= 60) { // If minute is > 60 
						DRINKING_PERIOD[0]++;
						DRINKING_PERIOD[1] -= 60;
					}

				}
			}

			System.out.println("Hour:" + mHour + ", Minute:" + mMinute);
			System.out.println("DP Hour:" + DRINKING_PERIOD[0] + " DP Minute:"
					+ DRINKING_PERIOD[1]);
		}
		STANDARD_DRINKS++;
		LastHour = mHour;
		LastMinute = mMinute;

		
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

	//Action Bar Functions
	public void launchSettings() {
		dialog = new Dialog(this);

		// set title
		dialog.setTitle("User Settings");
		dialog.setContentView(R.layout.user_settings);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.teal)));
		dialog.getWindow().setTitleColor(getResources().getColor(R.color.white));

		LayoutInflater li = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View vi = li.inflate(R.layout.user_settings, null);
		View view = vi.getRootView();

		dialog.setContentView(vi);
		dialog.setCancelable(true);

		dialog.show();
	}

	public void launchHelp() {
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
				.create();
		alertDialog.setTitle("Help");
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.teal)));
		alertDialog
				.setMessage("As you drink, log the drinks you consume and Wingman will keep track of how much alcohol is in your system. \n "
						+ "\n Select the type of drink you are consuming, and click the +1 button \n"
						+ "Your blood-alcohol level will be calculated and you will be able to see if you can safely drink any more. \n \n"
						+ "Click on the BAC number for a chart displaying the safe ranges \n \n"
						+ "If your BAC is too high, it is unsafe to continue drinking alcohol.");
		alertDialog.show();
	}
	
	public void launchPastDrinks() {
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
				.create();
		alertDialog.setTitle("Past Drinks");
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.teal)));
		alertDialog
				.setMessage("This is where a list of previously-consumed drinks and the time consumed will be");
		alertDialog.show();
	}
	
	public void saveUserSettings(View v){
		dialog.dismiss();
		System.out.println("Settings saved");
	}
	
	private static final String[] DRINKS = new String[] { "Light Beer", "Dark Beer", "Wine",
			"Shot", "Mixed Drink" };

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

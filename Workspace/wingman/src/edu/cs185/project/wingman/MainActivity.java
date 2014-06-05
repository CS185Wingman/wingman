package edu.cs185.project.wingman;

import java.util.Calendar;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.os.Build;


public class MainActivity extends ActionBarActivity implements OnItemClickListener {
    /* To Calculate Blood Alcohol Levels */
	Dialog dialog;
	static int DRUNK, /*State there in, status to go along with BAC */
		       BUZZING_HARD,
			   BUZZING,
		       ON_A_GOOD_ONE,
			   HEAD_CHANGE,
		       PRACTICALLY_SOBER,
			   SOBER;
	
					 
	double BAC, LastHour, LastMinute;
	/* Some constants for the BAC formula */
	double STANDARD_DRINKS,
		  BODY_WATER_CONSTANT, /*.806*/
		  METABOLISM_CONSTANT, /*.017*/
		  BODY_WATER_GENDER,
		  WEIGHT, /* In kilograms, conversion eminent because America. */
		  SWEDISH_CONVERTER;  /*1.2*/ 
	 double DRINKING_PERIOD[];
	
	public double tequilaMockingbird(double drink_number, 
			double drinking_time){
		double top, bottom, right;
		top = BODY_WATER_CONSTANT*drink_number*SWEDISH_CONVERTER;
		bottom = BODY_WATER_GENDER*WEIGHT;
		right = METABOLISM_CONSTANT*drinking_time;
		BAC = (top/bottom) -right;
		return BAC;
		
		
	}
		
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BODY_WATER_GENDER = .58; /*default make */
        WEIGHT = 85; /*default weight, will be changed eventually */
        LastHour = LastMinute = 0.0; /* initial values */
        DRINKING_PERIOD = new double[2];
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
	 public void selectDrink(View v){
		 
		 dialog = new Dialog(this);
		 
		 //set title
		 dialog.setTitle("Drink Selection");
		 dialog.setContentView(R.layout.select_drink);
		 
		 LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
		 View vi = li.inflate(R.layout.select_drink,null);
		 View view = vi.getRootView();
		 
		 dialog.setContentView(vi);
		 dialog.setCancelable(true);
		 
		 ListView dL = (ListView) dialog.findViewById(R.id.dList);
		 dL.setOnItemClickListener(this);
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_list_item_1,DRINKS);
		 
		 dL.setAdapter(adapter);	 
		 
		 dialog.show();
		 
	 }
	 
	 public void onItemClick(AdapterView<?> arg, View arg1, int arg2, long arg3){
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 final String dSelected = DRINKS[arg2];
		 double mHour, mMinute;
		 TextView drinkLabel = (TextView) findViewById(R.id.noDrinkSelected);
		 drinkLabel.setText(dSelected);
		 System.out.println("LastHour:"+LastHour+" LastMinute:"+LastMinute);
		 if(DRINKING_PERIOD[0]==0.0){
			 DRINKING_PERIOD[0] = 1.0;
		 	 DRINKING_PERIOD[1] = 0.0;
		 	 final Calendar cal = Calendar.getInstance();
			 Date date = cal.getTime();
		 	 mHour = date.getHours();
			 mMinute = date.getMinutes();		
		 }
		 else{
			final Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			mHour = date.getHours();
			mMinute = date.getMinutes();
			
			if(LastHour!=0 && LastHour==mHour){
				double time_diff = mMinute - LastMinute;
				DRINKING_PERIOD[1]+=time_diff; /*Sets appropriate Minute */
				if(DRINKING_PERIOD[1]>=60){/* if minute > 60*/
					DRINKING_PERIOD[0]++; 
					DRINKING_PERIOD[1]-=60;
				}
			}
			else if(LastHour!=0 && LastHour!=mHour){
				   if (LastMinute > mMinute){
					   mHour--;
					   mMinute+=60;
					   mMinute*=-1;
					   DRINKING_PERIOD[0]+= (mHour - LastHour);
					   DRINKING_PERIOD[1]+=(mMinute -LastMinute);
					   if(DRINKING_PERIOD[1]>=60){ /* If minute is > 60 */ 
							DRINKING_PERIOD[0]++;
							DRINKING_PERIOD[1]-=60;
						}
					   
					   
				   }
				   else{
					   DRINKING_PERIOD[0]+=(mHour- LastHour);
					   DRINKING_PERIOD[1]+=(mMinute -LastMinute);
					   if(DRINKING_PERIOD[1]>=60){ /* If minute is > 60 */ 
							DRINKING_PERIOD[0]++;
							DRINKING_PERIOD[1]-=60;
						}
					   
				   }
			}
			
			
			
			System.out.println("Hour:"+mHour+", Minute:"+mMinute);
			System.out.println("DP Hour:"+DRINKING_PERIOD[0]
					+" DP Minute:"+DRINKING_PERIOD[1]);
		 }
		 STANDARD_DRINKS++;
		 LastHour = mHour;
		 LastMinute = mMinute;
		 
			 
		 dialog.cancel();
		 
		 /** THIS IS FOR IF WE WANT EXTRA DIALOG FOR DISPLAYING INFO ABOUT DRINK?
		 builder.setMessage("Selecting " +dSelected).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog2, int which) {
				
				TextView drinkLabel = (TextView) findViewById(R.id.noDrinkSelected);
				drinkLabel.setText(dSelected);
				
				dialog2.dismiss();
				dialog.cancel();
				
			}
		});
		 
		 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog2, int which) {
				dialog2.dismiss();
				dialog.cancel();
				
			}
			 
			 
		 });
		 **/
		 
		 //AlertDialog alert = builder.create();
		 //alert.show();
	 }
	 
	 private static final String[] DRINKS = new String[]{
		 "Beer", "Wines", "etc"
	 };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}

package com.stc.weatherapp.demo.widget;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.stc.weatherapp.demo.R;
import com.stc.weatherapp.demo.main.MainActivity;

/**
 * The configuration screen for the {@link WeatherAppWidget WeatherAppWidget} AppWidget.
 */
public class WeatherWidgetConfigureActivity extends Activity {


	public static final int  WEATHER_JOB_ID = 254;
	public static final String MY_WEATHER_PREFS = "com.stc.task.fironet.MY_WEATHER_PREFS";
	public static final String QUERY_LAT = "QUERY_LAT";
	public static final String QUERY_LON = "QUERY_LON";
	public static final String CURRENT_WEATHER_TEXT = "CURRENT_WEATHER_TEXT";
	public static final String CURRENT_WEATHER_ICON_URL = "CURRENT_WEATHER_ICON_URL";
	public static final int REQUEST_GET_LOCATION = 618;
	public static final String EXTRA_WIDGET_ID = "EXTRA_WIDGET_ID";
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	TextView textSelectedLocation;
	FloatingActionButton fab;
	private static final String TAG = "NewAppWidgetAc";
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Set the result to CANCELED.  This will cause the widget host to cancel
		// out of the widget placement if the user presses the back button.
		setResult(RESULT_CANCELED);
		setContentView(R.layout.new_app_widget_configure);
		textSelectedLocation = (TextView) findViewById(R.id.text_selected_location);
		fab=(FloatingActionButton) findViewById(R.id.fab);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
			return;
		}
		updateUi(hasSavedLocation(this));
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult: ");
		if(requestCode==REQUEST_GET_LOCATION){
			if (RESULT_OK==resultCode){
				Log.d(TAG, "onActivityResult: ok");
			}else {
				Log.e(TAG, "onActivityResult: ne ok" );
			}
		}
		updateUi(hasSavedLocation(this));
	}
	void updateUi(boolean locationSet){
		fab.setImageResource(locationSet ? android.R.drawable.ic_input_add : android.R.drawable.ic_dialog_map );
		fab.setOnClickListener(locationSet ? mOnAddWidgetClickListener : mOnSelectLocationClickListener);
		textSelectedLocation.setText(locationSet ? getString(R.string.location_set) : getString(R.string.location_not_set));
	}

	View.OnClickListener mOnAddWidgetClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			final Context context = WeatherWidgetConfigureActivity.this;
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			WeatherAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
		}
	};
	View.OnClickListener mOnSelectLocationClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			final Context context = WeatherWidgetConfigureActivity.this;
			Intent i=new Intent(context, MainActivity.class);
			i.setAction(MainActivity.ACTION_SELECT_LOCATION);
			startActivityForResult(i, REQUEST_GET_LOCATION);
		}
	};

	public static boolean hasSavedLocation(Context context){
		SharedPreferences prefs = context.getSharedPreferences(MY_WEATHER_PREFS, MODE_PRIVATE);
		String lat = prefs.getString(QUERY_LAT, null);
		String lon = prefs.getString(QUERY_LON, null);
		return lat!=null && lon!=null;
	}


	public WeatherWidgetConfigureActivity() {
		super();
	}

	public static void cancelJob(Context context) {
		JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

		tm.cancel(WEATHER_JOB_ID);
	}
	static void scheduleJob(Context context,
	                            int appWidgetId) {
		ComponentName mServiceComponent = new ComponentName(context, WeatherJobService.class);
		JobInfo.Builder builder = new JobInfo.Builder(WEATHER_JOB_ID, mServiceComponent);

		int period = 60*60*1000;
		builder.setPeriodic(period);

		SharedPreferences prefs = context.getSharedPreferences(MY_WEATHER_PREFS, MODE_PRIVATE);
		String lat = prefs.getString(QUERY_LAT, null);
		String lon = prefs.getString(QUERY_LON, null);
		PersistableBundle bundle = new PersistableBundle();
		bundle.putString(QUERY_LAT, lat);
		bundle.putString(QUERY_LON, lon);
		bundle.putInt(EXTRA_WIDGET_ID, appWidgetId);
		builder.setExtras(bundle);
		Log.d(TAG, "Scheduling job");
		JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		tm.schedule(builder.build());
	}








}


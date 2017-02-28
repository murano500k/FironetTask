package com.stc.task.fironet.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.telemetry.MapboxEventManager;
import com.mikepenz.materialize.util.KeyboardUtil;
import com.squareup.picasso.Picasso;
import com.stc.task.fironet.R;

public class MainActivity extends AppCompatActivity implements WeatherContract.View{
	private static final String TAG = "MainActivity";
	SearchView searchView;
	Toolbar toolbar;
	private WeatherContract.Presenter presenter;
	private MapboxMap mMap;
	private MapView mapView;
	private View infoLayout;
	private TextView weatherDataText;
	private ImageView weatherIcon;
	private ProgressBar progress;
	public static final float ZOOM_DEFAULT = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		mapView=(MapView) findViewById(R.id.mapview);
		infoLayout=findViewById(R.id.info_view);
		weatherDataText=(TextView)findViewById(R.id.weather_info_text);
		weatherIcon=(ImageView) findViewById(R.id.weather_info_image);
		progress=(ProgressBar)findViewById(R.id.progress);
		setSupportActionBar(toolbar);
		setTitle("");
		searchView=new SearchView(this);
		searchView.setIconified(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				KeyboardUtil.hideKeyboard(MainActivity.this);
				if(presenter==null){
					showError("Not ready yet");
					return true;
				}
				else {
					presenter.citySelected(query);
					return true;
				}
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
		searchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@Override
			public boolean onClose() {
				KeyboardUtil.hideKeyboard(MainActivity.this);
				return false;
			}
		});
		Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Gravity.END);
		toolbar.addView(searchView, layoutParams);
		MapboxAccountManager.start(this, getString(R.string.map_access_token));
		MapboxEventManager.getMapboxEventManager().initialize(this, getString(R.string.map_access_token));
		MapboxEventManager.getMapboxEventManager().setTelemetryEnabled(true);
		mapView.onCreate(savedInstanceState);
		start();
	}
	private void start() {
		Log.d(TAG, "start: ");
		if(mMap==null) {
			updateProgress(true);
			mapView.getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(MapboxMap mapboxMap) {

					Log.d(TAG, "onMapReady: ");
					//updateProgress(false);
					mMap=mapboxMap;
					mMap.getUiSettings().setLogoEnabled(false);
					mMap.getUiSettings().setAllGesturesEnabled(true);
					start();
				}
			});
			return;
		}
		updateProgress(true);
		new WeatherPresenter(this);
	}
	@Override
	public void showSelectedCityOnMap(LatLng mapPoint,String t) {
		MarkerViewOptions markerOpts=new MarkerViewOptions();
		markerOpts.title(t);
		markerOpts.position(mapPoint);
		mMap.addMarker(markerOpts);
		CameraPosition cameraPosition= new CameraPosition.Builder()
				.bearing(mMap.getCameraPosition().bearing)
				.tilt(mMap.getCameraPosition().tilt)
				.zoom(ZOOM_DEFAULT)
				.target(mapPoint)
				.build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000);
	}

	@Override
	public void showWeatherIcon(String url) {
		Picasso.with(this).load(Uri.parse(url)).fit().into(weatherIcon);
	}

	@Override
	public void showWeatherData(String data) {
		weatherDataText.setText(data+"");
	}

	@Override
	public void showError(String msg) {
		Log.e(TAG, "Error: "+msg );
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void updateProgress(boolean visible) {
		progress.setVisibility(visible ? View.VISIBLE : View.GONE);
		infoLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
	}

	@Override
	public void setPresenter(WeatherContract.Presenter p) {
		this.presenter= p;
	}
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		if(MapboxAccountManager.getInstance().isConnected()) MapboxAccountManager.getInstance().setConnected(false);
	}

	@Override
	public String getWeatherApiKey() {
		return getString(R.string.weather_api_key);
	}

	@Override
	public String getWeatherBaseUrl() {
		return getString(R.string.weather_base_url);
	}

	public static void hideKeyboardFrom(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
}

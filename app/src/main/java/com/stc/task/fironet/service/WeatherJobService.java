package com.stc.task.fironet.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.util.Log;

import com.stc.task.fironet.json.WeatherDataForLocation;
import com.stc.task.fironet.net.WeatherHelper;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.stc.task.fironet.WeatherWidgetConfigureActivity.CURRENT_WEATHER_ICON_URL;
import static com.stc.task.fironet.WeatherWidgetConfigureActivity.CURRENT_WEATHER_TEXT;
import static com.stc.task.fironet.WeatherWidgetConfigureActivity.MY_WEATHER_PREFS;
import static com.stc.task.fironet.WeatherWidgetConfigureActivity.QUERY_LAT;
import static com.stc.task.fironet.WeatherWidgetConfigureActivity.QUERY_LON;

/**
 * Created by artem on 3/3/17.
 */


public class WeatherJobService extends JobService {
	private static final String TAG = "WeatherJobService";
	@Override
	public boolean onStartJob(JobParameters params) {
		String lat = params.getExtras().getString(QUERY_LAT);
		String lon = params.getExtras().getString(QUERY_LON);
		WeatherHelper weatherHelper=new WeatherHelper();
		Observable.fromCallable(new Callable<WeatherDataForLocation>() {
			@Override
			public WeatherDataForLocation call() throws Exception {
				return weatherHelper.loadWeather(lat,lon);
			}
		}).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Observer<WeatherDataForLocation>() {
			@Override
			public void onSubscribe(Disposable d) {
				Log.d(TAG, "onSubscribe: ");
			}

			@Override
			public void onNext(WeatherDataForLocation weatherDataForLocation) {
				Log.d(TAG, "onNext: "+weatherDataForLocation);
				String iconUrl = weatherHelper.getIconUrl(weatherDataForLocation.getWeather());
				String city = weatherDataForLocation.getName();
				String weatherText=weatherHelper.buildWeatherText(weatherDataForLocation.getMain(), city);
				if(weatherText!=null && iconUrl!=null) saveWeatherData(weatherText, iconUrl);
				else Log.e(TAG, "onNext: no data" );
			}

			@Override
			public void onError(Throwable e) {
				Log.e(TAG, "onError: ",e );
				WeatherJobService.this.onStopJob(null);
			}

			@Override
			public void onComplete() {
				Log.d(TAG, "onComplete: ");
				WeatherJobService.this.onStopJob(null);
			}
		});
		return true;
	}

	private void saveWeatherData(String weatherText, String iconUrl) {
		SharedPreferences prefs=getSharedPreferences(MY_WEATHER_PREFS, MODE_PRIVATE);
		prefs.edit().putString(CURRENT_WEATHER_TEXT, weatherText)
		.putString(CURRENT_WEATHER_ICON_URL, iconUrl)
				.apply();
		Log.d(TAG, "saveWeatherData: text = "+weatherText);
		Log.d(TAG, "saveWeatherData: icon_url = "+iconUrl);
	}



	@Override
	public boolean onStopJob(JobParameters params) {
		return false;
	}

}

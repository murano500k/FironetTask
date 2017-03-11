package com.stc.task.fironet.main;

import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.stc.task.fironet.json.Coord;
import com.stc.task.fironet.json.WeatherData;
import com.stc.task.fironet.json.WeatherDataForLocation;
import com.stc.task.fironet.net.WeatherHelper;

import java.util.concurrent.Callable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by artem on 2/28/17.
 */

public class MainPresenter implements MainContract.Presenter {
	private static final String TAG = "MainPresenter";
	MainContract.View view;
	WeatherHelper weatherHelper;
	public MainPresenter(MainContract.View view) {
		this.view=view;
		view.setPresenter(this);
		view.updateProgress(false);
		weatherHelper=new WeatherHelper();
	}

	@Override
	public void citySelected(String city) {
		Log.d(TAG, "citySelected: "+city);
		view.updateProgress(true);
		io.reactivex.Observable.fromCallable(new Callable<WeatherData>() {
			@Override
			public WeatherData call() throws Exception {
				return weatherHelper.loadWeather(city);
			}
		}).subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new io.reactivex.Observer<WeatherData>() {
					@Override
					public void onSubscribe(Disposable d) {
						view.updateProgress(true);
					}

					@Override
					public void onNext(WeatherData weatherData) {
						updateWeather(weatherData);
					}

					@Override
					public void onError(Throwable e) {
						view.showError(e.getMessage());
						view.updateProgress(false);
					}

					@Override
					public void onComplete() {
						view.updateProgress(false);
					}
				});
	}

	@Override
	public void locationSelected(String lat, String lon) {
		Log.d(TAG, "locationSelected: lat="+lat+"lng="+lon);
		view.updateProgress(true);
		io.reactivex.Observable.fromCallable(new Callable<WeatherDataForLocation>() {
			@Override
			public WeatherDataForLocation call() throws Exception {
				return weatherHelper.loadWeather(lat, lon);
			}
		}).subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new io.reactivex.Observer<WeatherDataForLocation>() {
					@Override
					public void onSubscribe(Disposable d) {
						view.updateProgress(true);
					}

					@Override
					public void onNext(WeatherDataForLocation weatherData) {
						updateWeatherForLocation(weatherData);
					}

					@Override
					public void onError(Throwable e) {
						view.showError(e.getMessage());
						view.updateProgress(false);
					}

					@Override
					public void onComplete() {
						view.updateProgress(false);
					}
				});
	}

	private void updateMap(Coord c, String name){
		view.showSelectedCityOnMap(new LatLng(c.getLat(),c.getLon()), name);
	}
	private void updateWeather(WeatherData weatherData){
			String iconUrl = weatherHelper.getIconUrl(weatherData.getWeather());
			String city = weatherData.getName();
			if(iconUrl!=null) view.showWeatherIcon(iconUrl);

			String weatherText=weatherHelper.buildWeatherText(weatherData.getMain(), city);

			if(weatherText!=null) view.showWeatherData(weatherText);

			else view.showWeatherData("no data");
			updateMap(weatherData.getCoord(),city);
		}

	private void updateWeatherForLocation(WeatherDataForLocation weatherData){
		String iconUrl = weatherHelper.getIconUrl(weatherData.getWeather());
		String city = weatherData.getName();
		if(iconUrl!=null) view.showWeatherIcon(iconUrl);
		String weatherText=weatherHelper.buildWeatherText(weatherData.getMain(), city);
		if(weatherText!=null) view.showWeatherData(weatherText);
		else view.showWeatherData("no data");
		updateMap(weatherData.getCoord(),city);
	}






	@Override
	public void cancel() {
		view.updateProgress(false);
	}


}

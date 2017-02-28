package com.stc.task.fironet.ui;

import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.stc.task.fironet.json.Coord;
import com.stc.task.fironet.json.Main;
import com.stc.task.fironet.json.Weather;
import com.stc.task.fironet.json.WeatherData;
import com.stc.task.fironet.net.WeatherHelper;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by artem on 2/28/17.
 */

public class WeatherPresenter implements WeatherContract.Presenter {
	private static final String TAG = "WeatherPresenter";
	WeatherContract.View view;
	WeatherHelper weatherHelper;
	public WeatherPresenter(WeatherContract.View view) {
		this.view=view;
		view.setPresenter(this);
		view.updateProgress(false);
		weatherHelper=new WeatherHelper(view.getWeatherBaseUrl(),view.getWeatherApiKey());

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
	private void updateMap(Coord c, String name){
		view.showSelectedCityOnMap(new LatLng(c.getLat(),c.getLon()), name);
	}
	private void updateWeather(WeatherData weatherData){

			String iconUrl = getIconUrl(weatherData);
			String city = weatherData.getName();
			if(iconUrl!=null) view.showWeatherIcon(iconUrl);

			String weatherText=buildWeatherText(weatherData.getMain(), city);

			if(weatherText!=null) view.showWeatherData(weatherText);

			else view.showWeatherData("no data");
			updateMap(weatherData.getCoord(),city);
		}


	private String buildWeatherText(Main main, String name) {
		String res="";
		int temp=(int)main.getTemp()-273;
		int humidity=(int)main.getHumidity();
		int pressure=(int)main.getPressure();
		res+="Weather in "+name;
		res+="\ntemperature: "+temp+" C\n"+
				"pressure: "+pressure+" Pa\n"+
				"humidity: "+humidity+" % ";
		Log.d(TAG, "buildWeatherText: "+res);
		return res;
	}

	@Override
	public void cancel() {
		view.updateProgress(false);
	}
	private String getIconUrl(WeatherData weatherData) {
		List<Weather> list = weatherData.getWeather();
		if (list == null || list.size() == 0) {
			view.showError("null weather");
		} else {
			Weather weather = list.get(0);
			if (weather == null) {
				view.showError("null weather");
				return null;
			}
			String res=view.getWeatherBaseUrl() + "img/w/" + weather.getIcon() + ".png";
			Log.d(TAG, "getIconUrl: "+res);
			return res;
		}
		return null;
	}
}

package com.stc.task.fironet.net;

import android.util.Log;

import com.stc.task.fironet.json.Main;
import com.stc.task.fironet.json.Weather;
import com.stc.task.fironet.json.WeatherData;
import com.stc.task.fironet.json.WeatherDataForLocation;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by artem on 2/28/17.
 */

public class WeatherHelper {
	public static final String WEATHER_BASE_URL = "http://api.openweathermap.org/";
	public static final String API_KEY = "94b5cb3b2ef49b9c0bfe5e08ff7d2567";

	private final Retrofit retrofit;
	private static final String TAG = "WeatherHelper";
	public WeatherHelper() {
		retrofit = new Retrofit.Builder()
				.baseUrl(WEATHER_BASE_URL)
				.client(new OkHttpClient())
				.addConverterFactory(GsonConverterFactory.create())
				.build();
	}
	public WeatherData loadWeather(String city) {
		Log.d(TAG, "loadWeather: ");
		WeatherApi service = retrofit.create(WeatherApi.class);
		Call<WeatherData> getDataCall = service.getCityWeather(city, API_KEY);
		Log.d(TAG, "url: " + getDataCall.request().url().toString());

		try {
			Log.d(TAG, "loadWeather: before start");
			Response<WeatherData> responce = getDataCall.execute();
			if (!responce.isSuccessful()) {
				Log.e(TAG, "loadWeather errorBody: "+responce.errorBody().string());
				Log.e(TAG, "loadWeather message: "+responce.message() );
				return null;
			} else {
				Log.d(TAG, "loadWeather: SUCCESS");
				return responce.body();
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: ", e);
			e.printStackTrace();
		}
		return null;
	}

	public WeatherDataForLocation loadWeather(String lat, String lon) {
		Log.d(TAG, "loadWeather: ");
		WeatherApi service = retrofit.create(WeatherApi.class);
		Call<WeatherDataForLocation> getDataCall = service.getCityWeatherForLocation(lat, lon, API_KEY);
		Log.d(TAG, "url: " + getDataCall.request().url().toString());

		try {
			Log.d(TAG, "loadWeather: before start");
			Response<WeatherDataForLocation> responce = getDataCall.execute();
			if (!responce.isSuccessful()) {
				Log.e(TAG, "loadWeather errorBody: "+responce.errorBody().string());
				Log.e(TAG, "loadWeather message: "+responce.message() );
				return null;
			} else {
				Log.d(TAG, "loadWeather: SUCCESS");
				return responce.body();
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: ", e);
			e.printStackTrace();
		}
		return null;
	}

	public String buildWeatherText(Main main, String name) {
		long timestamp = System.currentTimeMillis();
		String humanDate= new Date(timestamp).toString();
		String res=humanDate+"\n";
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

	public String getIconUrl(List<Weather> list) {
		if (list == null || list.size() == 0) {
			Log.e(TAG, "getIconUrl: null");
		} else {
			Weather weather = list.get(0);
			if (weather == null) {
				Log.e(TAG, "getIconUrl: null");
				return null;
			}
			String res=WEATHER_BASE_URL + "img/w/" + weather.getIcon() + ".png";
			Log.d(TAG, "getIconUrl: "+res);
			return res;
		}
		return null;
	}
}

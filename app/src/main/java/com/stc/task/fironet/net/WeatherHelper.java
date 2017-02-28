package com.stc.task.fironet.net;

import android.util.Log;

import com.stc.task.fironet.json.WeatherData;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mapbox.mapboxsdk.constants.MapboxConstants.TAG;

/**
 * Created by artem on 2/28/17.
 */

public class WeatherHelper {
	private final String API_KEY;
	private final Retrofit retrofit;

	public WeatherHelper(String baseUrl, String api_key ) {
		Log.d(TAG, "WeatherHelper: url="+baseUrl+" apikey="+api_key);
		this.API_KEY=api_key;
		retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(new OkHttpClient())
				.addConverterFactory(GsonConverterFactory.create())
				.build();
	}
	public WeatherData loadWeather(String city) {
		Log.d(TAG, "loadFromWeb: ");
		WeatherApi service = retrofit.create(WeatherApi.class);
		Call<WeatherData> getDataCall = service.getCityWeather(city, API_KEY);
		Log.d(TAG, "url: " + getDataCall.request().url().toString());

		try {
			Log.d(TAG, "loadFromWeb: before start");
			Response<WeatherData> responce = getDataCall.execute();
			if (!responce.isSuccessful()) {
				Log.e(TAG, "loadFromWeb: ERROR");
				return null;
			} else {
				Log.d(TAG, "loadFromWeb: SUCCESS");
				return responce.body();
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: ", e);
			e.printStackTrace();
		}
		return null;
	}
}

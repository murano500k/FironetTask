package com.stc.task.fironet.net;

import android.util.Log;

import com.stc.task.fironet.json.Geodata;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by artem on 3/2/17.
 */

public class LocationHelper {

	private final String API_KEY;
	private final Retrofit retrofit;
	private static final String TAG = "WeatherHelper";
	public LocationHelper(String baseUrl, String api_key ) {
		Log.d(TAG, "WeatherHelper: url="+baseUrl+" apikey="+api_key);
		this.API_KEY=api_key;
		retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(new OkHttpClient())
				.addConverterFactory(GsonConverterFactory.create())
				.build();
	}
	public Geodata getGeodata(String query) {
		Log.d(TAG, "loadWeather: ");
		LocationApi service = retrofit.create(LocationApi.class);
		Call<Geodata> getDataCall = service.getGeodata(query, API_KEY);
		Log.d(TAG, "url: " + getDataCall.request().url().toString());

		try {
			Log.d(TAG, "loadWeather: before start");
			Response<Geodata> responce = getDataCall.execute();
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
}

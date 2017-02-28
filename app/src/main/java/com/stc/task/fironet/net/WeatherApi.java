package com.stc.task.fironet.net;

import com.stc.task.fironet.json.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by artem on 2/28/17.
 */

public interface WeatherApi {
	@GET("data/2.5/weather/")
	Call<WeatherData> getCityWeather(@Query("q") String city, @Query("APPID") String appid);
}

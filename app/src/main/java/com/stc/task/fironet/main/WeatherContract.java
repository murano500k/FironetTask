package com.stc.task.fironet.main;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.stc.task.fironet.BasePresenter;
import com.stc.task.fironet.BaseView;

/**
 * Created by artem on 2/28/17.
 */

public interface WeatherContract {
	interface View extends BaseView {
		void showSelectedCityOnMap(LatLng mapPoint, String cityName);

		void showWeatherIcon(String url);

		void showWeatherData(String data);

		String getWeatherBaseUrl();
		String getWeatherApiKey();

	}
	interface Presenter extends BasePresenter {
		void citySelected(String city);

		void locationSelected(String lat, String lng);
	}
}

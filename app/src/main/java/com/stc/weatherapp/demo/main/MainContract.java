package com.stc.weatherapp.demo.main;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.stc.weatherapp.demo.BasePresenter;
import com.stc.weatherapp.demo.BaseView;

/**
 * Created by artem on 2/28/17.
 */

public interface MainContract {
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

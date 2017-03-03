package com.stc.task.fironet.net;

import com.stc.task.fironet.json.Geodata;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by artem on 3/2/17.
 */

public interface LocationApi {
	@GET("/geocoding/v5/mapbox.places/{query}.json")
	Call<Geodata> getGeodata(@Path("query") String query, @Query("access_token") String access_token);
}
//curl "https://api.mapbox.com/geocoding/v5/mapbox.places/-73.989,40.733.json?access_token=your-access-token"

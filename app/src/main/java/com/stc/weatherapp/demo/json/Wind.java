
package com.stc.weatherapp.demo.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private double speed;
    @SerializedName("deg")
    @Expose
    private double deg;
    @SerializedName("var_beg")
    @Expose
    private double varBeg;
    @SerializedName("var_end")
    @Expose
    private double varEnd;

}

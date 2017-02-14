package com.example.wenxi.carmap.VolleyUtils;

/**
 * Created by wenxi on 2016/12/20.
 */

public class Carinfo {

    private String temperature;
    private String humidity;
    private String oilmass;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getOilmass() {
        return oilmass;
    }

    public void setOilmass(String oilmass) {
        this.oilmass = oilmass;
    }

    @Override
    public String toString() {
        return "汽车信息{" +
                "汽车温度='" + temperature + '\'' +
                ", 汽车湿度='" + humidity + '\'' +
                ", 汽车油量='" + oilmass + '\'' +
                '}';
    }
}

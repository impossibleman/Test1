package com.example.test1.dataconstruct;

public class WeatherInfo {
    String status;
    String city;
    String aqi;
    String pm25;
    String temp;
    String weather;
    String wind;
    String weatherimg;
    TomorrowWeatherInfo tomorrowWeatherInfo;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWeatherimg() {
        return weatherimg;
    }

    public void setWeatherimg(String weatherimg) {
        this.weatherimg = weatherimg;
    }

    public TomorrowWeatherInfo getTomorrowWeatherInfo() {
        return tomorrowWeatherInfo;
    }

    public void setTomorrowWeatherInfo(TomorrowWeatherInfo tomorrowWeatherInfo) {
        this.tomorrowWeatherInfo = tomorrowWeatherInfo;
    }
}

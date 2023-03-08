package com.example.ma01_20200942;

public class ForecastItem {

    private String time;
    private String temp;
    private String reh;

    public ForecastItem() {
    }

    public ForecastItem(String time, String temp, String sky, String rain) {
        this.time = time;
        this.temp = temp;
        this.reh = sky;
        this.rain = rain;
    }

    private String rain;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getReh() {
        return reh;
    }

    public void setReh(String reh) {
        this.reh = reh;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        if (rain.equals("강수없음"))
            this.rain = rain;
        else
            this.rain = rain + "mm";
    }

    @Override
    public String toString() {
        return  "시간=" + time +
                "\n 기온=" + temp +
                "˚C\n 습도=" + reh  +
                "%\n 강수량=" + rain;
    }
}
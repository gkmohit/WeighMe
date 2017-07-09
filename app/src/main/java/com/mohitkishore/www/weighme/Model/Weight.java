package com.mohitkishore.www.weighme.Model;

/**
 * Created by AirUnknown on 2017-07-08.
 * TODO Add images too.
 */

public class Weight {

    String date;
    String month;
    String year;
    String time;
    String weight;

    public Weight() {
    }

    public Weight(String date, String month, String year, String time, String weight) {
        this.date = date;
        this.month = month;
        this.year = year;
        this.time = time;
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}

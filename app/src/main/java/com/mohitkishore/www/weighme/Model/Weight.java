package com.mohitkishore.www.weighme.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AirUnknown on 2017-07-08.
 * TODO Add images too.
 */

public class Weight {

    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String TIME = "time";
    public static final String WEIGHT = "weight";
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

    public Weight(HashMap<String, Object> weightMap){
        this((String) weightMap.get(DATE),
                (String)weightMap.get(MONTH),
                (String)weightMap.get(YEAR),
                (String)weightMap.get(TIME),
                (String)weightMap.get(WEIGHT));
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(DATE, date);
        result.put(MONTH, month);
        result.put(YEAR, year);
        result.put(TIME, time);
        result.put(WEIGHT, weight);

        return result;
    }

    @Override
    public String toString(){
        String returnVal = String.format("Date : %s, Month : %s, Year : %s, Time : %s, Weight : %s", date, month, year, time, weight);
        return returnVal;
    }
}

package org.hansung.ansime.dto;

import android.content.Context;
import android.content.SharedPreferences;

import org.hansung.ansime.MainActivity;

/**
 * Created by 호영 on 2016-10-15.
 * App Data Class.
 * 변경을 했을 때 유지 되어야하는 값들을 처리하는 클래스.
 */
public class AppData {

    private SharedPreferences appData;

    // Alarm Fragment
    private int volume;
    private String phoneNumber;

    // Map Fragment
    private int makerNum;

    // 이전 좌표
    private double lat;
    private double lon;

    // Type
    private int type;

    public AppData(Context context) {
        appData = context.getSharedPreferences("AppData", 0);
        load();
    }


    public int getVolume() {
        return volume;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getMakerNum() {
        return makerNum;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getType() {
        return type;
    }


    public void setVolume(int volume) {
        this.volume = volume;
        save("VOLUME");
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        save("PHONENUMBER");
    }

    public void setMakerNum(int makerNum) {
        this.makerNum = makerNum;
        save("MAKERNUM");
    }

    public void setType(int type) {
        SharedPreferences.Editor editor = appData.edit();
        editor.putInt("TYPE", type);
        editor.apply();
    }

    public void setLocation() {
        save("LOCATION");
    }

    private void load() {
        volume = appData.getInt("VOLUME", 7);
        phoneNumber = appData.getString("PHONENUMBER", null);
        makerNum = appData.getInt("MAKERNUM", 10);
        lat = Double.parseDouble(appData.getString("LAT", "0"));
        lon = Double.parseDouble(appData.getString("LON", "0"));
        type = appData.getInt("TYPE", 0);
    }

    public void save(String str) {
        SharedPreferences.Editor editor = appData.edit();
        switch (str) {
            case "VOLUME":
                editor.putInt("VOLUME", volume);
                break;
            case "PHONENUMBER":
                editor.putString("PHONENUMBER", phoneNumber);
                break;
            case "MAKERNUM":
                editor.putInt("MAKERNUM", makerNum);
                break;
            case "LOCATION":
                editor.putString("LAT", Double.toString(MainActivity.GPS.getCur().getLatitude()));
                editor.putString("LON", Double.toString(MainActivity.GPS.getCur().getLongitude()));
                break;
        }
        editor.apply();
    }

    @Override
    public String toString() {
        return "AppData{" +
                "appData=" + appData +
                ", volume=" + volume +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", makerNum=" + makerNum +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}


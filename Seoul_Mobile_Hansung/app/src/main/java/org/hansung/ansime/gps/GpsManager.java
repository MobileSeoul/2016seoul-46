package org.hansung.ansime.gps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.skp.Tmap.TMapPoint;

import org.hansung.ansime.MainActivity;

/**
 * Created by 호영 on 2016-09-13.
 * Gps Manager Class.
 * Gps 기능을 관리하는 Class.
 */
public class GpsManager implements LocationListener {

    private LocationManager mLocationManager; // Gps 관련 변수
    private Context context;

    public GpsManager(Context context) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void start() {
        // 퍼미션이 없을 때
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this); // 위치 찾기 시작
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, this); // 위치 찾기 시작
    }

    public void stop() {
        // 퍼미션이 없을 때
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 리스너 제거
        mLocationManager.removeUpdates(this);
    }


    public LocationManager getmLocationManager() {
        return mLocationManager;
    }

    @Override
    public void onLocationChanged(Location location) {
        // 검색이 되면 GpsData 변수에 저장
        MainActivity.GPS.setCur(new TMapPoint(location.getLatitude(), location.getLongitude())); // 검색된 위치를 저장
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
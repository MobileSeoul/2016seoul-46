package org.hansung.ansime.dto;

import com.skp.Tmap.TMapPoint;

/**
 * Created by 호영 on 2016-09-20.
 * Maker Data Class.
 * Tmap에 사용될 marker 데이터를 관리하는 Class.
 */
public class MarkerData {
    private String title;
    private double latitude, longitude;
    private double dist;

    public String getTitle() {
        return title;
    }

    public MarkerData setTitle(String title) {
        this.title = title;
        return this;
    }

    MarkerData setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    MarkerData setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Double getDist() {
        return dist;
    }

    public MarkerData setDist(double dist) {
        this.dist = dist;
        return this;
    }

    public TMapPoint getTMapPoint() {
        return new TMapPoint(latitude, longitude);
    }
}

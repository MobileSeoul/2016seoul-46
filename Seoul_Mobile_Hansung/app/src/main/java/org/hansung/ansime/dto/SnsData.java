package org.hansung.ansime.dto;

import android.support.v4.content.ContextCompat;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import org.hansung.ansime.MainActivity;
import org.hansung.ansime.R;
import org.hansung.ansime.fragment.SnsFragment;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by 호영 on 2016-10-05.
 * Sns Data Class.
 * ListView에 추가시킬 Sns 데이터를 관리하는 Class.
 */

public class SnsData {
    private double latitude, longitude, distance;
    private int type;
    private String contents;
    private String address = null;

    public SnsData(double lat, double lon, int type, String contents, double dist) {
        this.latitude = lat;
        this.longitude = lon;
        this.type = type;
        this.contents = contents;
        this.distance = dist;

        new Thread() {
            public void run() {
                try {
                    address = new TMapData().convertGpsToAddress(latitude, longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                while (true) {
                    if (address != null) {
                        SnsFragment.ADD_THREAD_CNT();
                        break;
                    }
                }
            }
        }.start();
    }

    public int getType() {
        return type;
    }

    public String getContents() {
        return contents;
    }

    public double getDistance() {
        return distance;
    }

    public String getAddress() {
        String[] addressArray = this.address.split(" ");
        return addressArray[0] + " " + addressArray[1];
    }
}


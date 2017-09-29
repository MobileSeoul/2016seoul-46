package org.hansung.ansime.dto;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import org.hansung.ansime.MainActivity;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by 호영 on 2016-10-06.
 * Gps Data Class.
 * 좌표 데이터를 관리하는 클래스.
 */
public class GpsData {
    private TMapPoint cur = null; // 가장 최근에 검색한 현재 좌표
    private TMapPoint target = null; // 가장 최근에 검색한 여성 안심지킴이집 좌표
    private String address; // 가장 최근에 검색한 현재 좌표의 주소
    private String targetAddress; // 가장 최근에 검색한 여성 안심지킴이집의 주소

    public TMapPoint getCur() {
        return cur;
    }

    public TMapPoint getTarget() {
        return target;
    }

    public String getAddress() {
        return address;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setCur(TMapPoint cur) {
        this.cur = cur;
        convertGpsToAddress(cur);
    }

    public void setTarget(TMapPoint target) {
        this.target = target;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    // 좌표를 입력했을 때 주소로 변환 시켜주는 함수
    // Tmap Api 에서 주소로 변환시키는 과정에서 Web 통신을 하기 때문에 Thread로 구현
    public void convertGpsToAddress(final TMapPoint point) {
        new Thread() {
            public void run() {
                try {
                    address = new TMapData().convertGpsToAddress(point.getLatitude(), point.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

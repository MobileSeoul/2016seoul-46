package org.hansung.ansime.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.hansung.ansime.MainActivity;
import org.hansung.ansime.R;
import org.hansung.ansime.dialog.LoadingDialogManager;
import org.hansung.ansime.dto.MarkerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by 호영 on 2016-09-13.
 * Map Fragment.
 * Tmap Api 를 이용해서 구현한 Fragment
 */
public class MapFragment extends BaseFragment implements TMapView.OnClickListenerCallback, View.OnClickListener {

    private final int view_id = R.layout.fragment_map;
    private final String api_key = "5e16fa28-d0df-31c5-9fc0-482a7b71eb6c";
    private TMapView tMapView; // tmap 지도 뷰
    private ImageButton myPositionBtn, shortLoadBtn;
    private LoadingDialogManager dialogManager;
    private ArrayList<MarkerData> markerDatas;
    private ArrayList<Double> distArray;
    private HashMap<Double, TMapPoint> distMap;
    private TMapData tmapData;
    private TMapPoint cur_point;
    private View.OnClickListener mClickListener = this;
    private int cnt = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setView(inflater.inflate(view_id, container, false));

        init();
        init_Event();

        return getView();
    }

    @Override
    void init() {
        RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.tmap_layout);

        // gps 관련 변수
        distArray = new ArrayList<Double>();
        distMap = new HashMap<Double, TMapPoint>();
        dialogManager = new LoadingDialogManager(getActivity());

        // tMap 관련 변수
        markerDatas = ((MainActivity) getActivity()).getMarkerArray();
        tMapView = new TMapView(getActivity());
        tMapView.setSKPMapApiKey(api_key); // ApiKey 설정
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN); // 언어 설정
        // tmapview.setIconVisibility(true); // 현재 위치로 표시될 아이콘 표시 여부 설정
        tMapView.setZoomLevel(16); // 지도 축척 레벨을 설정 (7~19) 숫자가 높을 수록 확대
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD); // 지도 타입 설정 (일반지도)
        tMapView.setCompassMode(false); // 나침판 모드 설정
        tMapView.setTrackingMode(true); // 화면 중심으로 단말의 현재 위치로 이동
        relativeLayout.addView(tMapView);
        tmapData = new TMapData();

        myPositionBtn = (ImageButton) getView().findViewById(R.id.myPosition);
        shortLoadBtn = (ImageButton) getView().findViewById(R.id.shortLoad);

        // 최초 실행시 현재 위치가 검색될 때까지 쓰레드를 이용해서 기다려줌
        // 검색되었을 시에 최단 경로를 화면에 출력
        dialogManager.Loading("최단거리 탐색 중...");
        new Thread() {
            public void run() {
                int count = 0;
                while (true) {
                    if (MainActivity.isSearch()) {
                        addShortestTMapPolyLine();
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }.start();
    }

    @Override
    void init_Event() {
        myPositionBtn.setOnClickListener(this);
        // shortLoadBtn.setOnClickListener(this);
        tMapView.setOnClickListenerCallBack(this);
    }

    // 매개 변수 point 좌표 값을 화면 중아앙으로 이동 시켜주는 함수
    public void setTmapLocationPoint(TMapPoint point, int zoomLevel) {
        makeMarker(point); // 마커 등록
        // 현재 위치 마커 생성
        if (tMapView.getMarkerItemFromID("myPoint") != null) {
            tMapView.getMarkerItemFromID("myPoint").setTMapPoint(point);
        } else {
            // 최초 현재 위치 마커 생성 시
            TMapMarkerItem tItem = new TMapMarkerItem();
            tItem.setTMapPoint(point);
            tItem.setName("myPoint");
            tItem.setVisible(TMapMarkerItem.VISIBLE);
            Bitmap myPoint = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_user_position_24dp_d);
            tItem.setIcon(myPoint);
            tMapView.addMarkerItem("myPoint", tItem);
        }

        tMapView.setLocationPoint(point.getLongitude(), point.getLatitude()); // 현재 위치 설정
        tMapView.setZoomLevel(zoomLevel); // 줌 레벨 설정
        tMapView.setTrackingMode(true); // 현재 위치를 화면 중앙으로 설정
    }

    // tmap에 현재 좌표와 타겟 좌표를 이용해서 polyLine을 그려주는 함수
    public void addTMapPolyLine(TMapPoint cur_point, TMapPoint target_point) {
        tmapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, cur_point, target_point, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineWidth(20.0f);
                polyLine.setLineColor(Color.parseColor("#A48363"));
                polyLine.setOutLineColor(Color.parseColor("#A48363"));
                tMapView.addTMapPolyLine("short", polyLine);
            }
        });
    }

    // 최단거리를 구해주는 함수
    public void addShortestTMapPolyLine() {
        //Log.v("test", "최단거리 찾기 시작");
        distArray.clear(); // 연산에 이용되는 리스트
        distMap.clear(); // 연산에 이용되는 맵
        if (MainActivity.isSearch()) {
            // 위치 검색 o
            // 연산중에 현재 위치가 변할 수도 있기 때문에 저장시킨 후 연산
            cur_point = MainActivity.GPS.getCur();

            // 현재 위치로 이동
            // 현재 위치로 이동하는 과정에서 마커를 생성하게 되는데
            // 이때 현재 위치 기준 직선거리를 오름차순으로 정렬해줌
            setTmapLocationPoint(cur_point, 16);

            // 직선 거리중 가까운 5개중 도보 기준 최단 거리를 구함
            // findPathDataWithType 함수가 스레드로 구성되 있기 때문에 cnt 값으로 제어
            for (int i = 0; i < 5; i++) {
                final TMapPoint target_point = markerDatas.get(i).getTMapPoint();
                new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, cur_point, target_point, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        cnt++;
                        distArray.add(polyLine.getDistance());
                        distMap.put(polyLine.getDistance(), target_point);
                        if (cnt == 5) {
                            // 5개의 스레드가 모두 완료 되었을 때
                            double minDist = Collections.min(distArray); // 최소 값을 구해줌
                            TMapPoint target_point = distMap.get(minDist);
                            addTMapPolyLine(cur_point, target_point);
                            MainActivity.GPS.setTarget(target_point);
                            // MainActivity.GPS.setTargetAddress(tMapView.getAllMarkerItem2());
                            for (int i = 0; i < MainActivity.APPDATA.getMakerNum(); i++) {
                                if (tMapView.getMarkerItemFromID("" + i).getTMapPoint().equals(target_point)) {
                                    MainActivity.GPS.setTargetAddress(tMapView.getMarkerItemFromID("" + i).getName());
                                    break;
                                }
                            }
                            cnt = 0; // cnt 초기화
                            dialogManager.LoadingEnd(); // 다이얼로그 끝
                            shortLoadBtn.setOnClickListener(mClickListener); // 리스너 설정 ( 버튼이 더블클릭 되는 것을 방지하기 위함 )
                        }
                    }
                });
            }
        } else {
            // 위치 검색 x
            dialogManager.LoadingEnd(); // 다이얼로그 끝
            shortLoadBtn.setOnClickListener(mClickListener); // 리스너 설정 ( 버튼이 더블클릭 되는 것을 방지하기 위함 )
        }
    }

    // 현재 위치와 선택된 마커 간의 직선 거리를 반환
    public double calDistance(TMapPoint cur, TMapPoint target) {
        double lon1 = cur.getLongitude();
        double lat1 = cur.getLatitude();
        double lon2 = target.getLongitude();
        double lat2 = target.getLatitude();
        double theta, dist;

        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }

    // 마커 클릭 이벤트
    @Override
    public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        if (arrayList.size() != 0) {
            // 클릭된 마커의 point를 MainActivity의 target_point에 저장
            MainActivity.GPS.setTarget(new TMapPoint(arrayList.get(0).latitude, arrayList.get(0).longitude));
            MainActivity.GPS.setTargetAddress(arrayList.get(0).getName());
            // Log.v("test", arrayList.get(0).getID());
            // 현재 위치와 클릭된 마커의 도보 기준 경로를 그려줌
            addTMapPolyLine(MainActivity.GPS.getCur(), MainActivity.GPS.getTarget());
        }
        return false;
    }

    @Override
    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        return false;
    }

    // 버튼 클릭 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myPosition:
                // Log.v("test", "gps 버튼 1 클릭");
                ((MainActivity) getActivity()).checkGPS();
                setTmapLocationPoint(MainActivity.GPS.getCur(), 17);
                break;
            case R.id.shortLoad:
                // 리스너 제거 ( 버튼이 더블클릭 되는 것을 방지하기 위함 )
                shortLoadBtn.setOnClickListener(null);
                // Log.v("test", "gps 버튼 2 클릭");
                ((MainActivity) getActivity()).checkGPS();
                dialogManager.Loading("최단거리 탐색 중..."); // 다이얼로그 시작
                addShortestTMapPolyLine();
                break;
        }
    }

    // 오름차순
    public class Ascending implements Comparator<MarkerData> {
        @Override
        public int compare(MarkerData o1, MarkerData o2) {
            return o1.getDist().compareTo(o2.getDist());
        }
    }

    public void makeMarker(TMapPoint cur_point) {
        // 마커 등록
        if (MainActivity.isSearch()) {
            // 현재 위치와 마커데이터의 직선거리를 구함
            for (int i = 0; i < markerDatas.size(); i++) {
                TMapPoint target_point = markerDatas.get(i).getTMapPoint();
                markerDatas.get(i).setDist(calDistance(cur_point, target_point));
            }

            // 오름차순 정렬
            Ascending ascending = new Ascending();
            Collections.sort(markerDatas, ascending);

            // 직선거리 기준 가까운 순으로 Setting에 설정된 개수만큼 그려줌
            for (int i = 0; i < MainActivity.APPDATA.getMakerNum(); i++) {
                TMapMarkerItem tItem = tMapView.getMarkerItemFromID("" + i);

                // 최초 마커 생성시
                if (tItem == null) {
                    tItem = new TMapMarkerItem();
                }

                MarkerData d = markerDatas.get(i);
                tItem.setName(d.getTitle());
                TMapPoint point = d.getTMapPoint();
                tItem.setTMapPoint(point);
                tItem.setVisible(TMapMarkerItem.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_map_pin_48dp);
                tItem.setIcon(bitmap);
                tItem.setPosition(0.5f, 1.0f);
                tMapView.addMarkerItem("" + i, tItem);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (MainActivity.GPS.getCur() != null) MainActivity.APPDATA.setLocation();
    }
}
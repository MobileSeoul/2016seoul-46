package org.hansung.ansime.fragment;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.skp.Tmap.TMapPoint;

import org.hansung.ansime.MainActivity;
import org.hansung.ansime.R;
import org.hansung.ansime.adapter.SnsListViewAdapter;
import org.hansung.ansime.dialog.LoadingDialogManager;
import org.hansung.ansime.dialog.SnsDialogManager;
import org.hansung.ansime.dto.SnsData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 해든 on 2016-10-04.
 * Sns Fragment.
 * Android - PHP - MySQL 통신으로 데이터를 가져와 출력
 */
public class SnsFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private final int view_id = R.layout.fragment_sns;
    private ListView listView;
    private static final String TAG_RESULT = "result";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_TYPE = "type";
    private static final String TAG_CONTENTS = "contents";
    private static final String TAG_DISTANCE = "distance";
    private double dist;
    private LoadingDialogManager dialogManager;
    private static int THREAD_CNT = 0;
    public static AlertDialog SNS_DIALOG;
    private SnsListViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setView(inflater.inflate(view_id, container, false));

        init();

        return getView();
    }

    @Override
    void init() {
        ((MainActivity) getActivity()).checkGPS();
        final ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.dist, android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) getView().findViewById(R.id.distSpinner);
        spinner.setAdapter(sAdapter);
        spinner.setOnItemSelectedListener(this);

        // Adapter 생성
        SnsListViewAdapter adapter = new SnsListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView) getView().findViewById(R.id.listview);
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.attachToListView(listView);
        fab.setOnClickListener(this);
        // dialogManager 생성
        dialogManager = new LoadingDialogManager(getActivity());
    }

    @Override
    void init_Event() {
    }

    // 현재 위치 기준 SNS 데이터 검색해주는 함수
    public void searchSNS(double dist) {
        dialogManager.Loading("현재 위치 기준 SNS 검색중..");
        TMapPoint cur_point = MainActivity.GPS.getCur();
        String url = String.format("http://codeman.ivyro.net/getData.php?lat=%s&lon=%s&dist=%s", cur_point.getLatitude(), cur_point.getLongitude(), dist);
        System.out.println(url);
        getData(url);
    }

    // php에서 가져온 json 데이터를 list에 적용 시키는 함수
    protected void showList(String json) {
        try {
            // adpater 생성
            adapter = new SnsListViewAdapter();
            // adapter 적용
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            JSONObject jsonObj = new JSONObject(json);
            // json 배열의 이름을 검색
            JSONArray snsDatas = jsonObj.getJSONArray(TAG_RESULT);
            final List<SnsData> itmeList = new ArrayList<>();
            // Thread 제어를 위한 변수
            final int tmpLength = snsDatas.length();
            for (int i = 0; i < snsDatas.length(); i++) {
                JSONObject c = snsDatas.getJSONObject(i);
                double latitude = Double.parseDouble(c.getString(TAG_LATITUDE));
                double longitude = Double.parseDouble(c.getString(TAG_LONGITUDE));
                int type = Integer.parseInt(c.getString(TAG_TYPE));
                String contents = c.getString(TAG_CONTENTS);
                double dist = Double.parseDouble(c.getString(TAG_DISTANCE));
                itmeList.add(new SnsData(latitude, longitude, type, contents, dist));
            }

            // address 변환으로 인한 시간차를 Thread 이용으로 해결
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (THREAD_CNT == tmpLength) {
                            // 아이템 추가.
                            for (int i = 0; i < itmeList.size(); i++) {
                                if (itmeList.get(i).getType() == 0) { //아이템의 type가 0이라면
                                    adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.warning), itmeList.get(i).getAddress(), itmeList.get(i).getDistance(), itmeList.get(i).getContents());
                                } else {
                                    adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.prevent), itmeList.get(i).getAddress(), itmeList.get(i).getDistance(), itmeList.get(i).getContents());
                                }
                            }
                            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.logo), " 자신의 위치에 대한 정보를 공유해주세요.", -1, "");
                            adapter.notifyDataSetChanged();
                            dialogManager.LoadingEnd();
                            THREAD_CNT = 0;
                            break;
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // php의 Data를 가져오는 함수
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Integer, String> {
            @Override
            protected String doInBackground(String... urls) {
                StringBuilder jsonHtml = new StringBuilder();
                try {
                    // 연결 url 설정
                    URL url = new URL(urls[0]);
                    // 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 연결되었으면.
                    if (conn != null) {
                        conn.setConnectTimeout(5000);
                        conn.setUseCaches(false);
                        // 연결되었음 코드가 리턴되면.
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            for (; ; ) {
                                // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                                String line = br.readLine();
                                if (line == null) break;
                                // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                                jsonHtml.append(line).append("\n");
                            }
                            br.close();
                        }
                        conn.disconnect();
                    }
                } catch (Exception ex) {
                    dialogManager.LoadingEnd();
                    Toast.makeText(getActivity(), "Sns를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                return jsonHtml.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                showList(result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    // 주소로 변환 시켜주는 타이밍을 맞추기 위한 함수
    public static synchronized void ADD_THREAD_CNT() {
        THREAD_CNT++;
    }

    public double getDist() {
        return dist;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // 500m
                dist = 0.5;
                searchSNS(dist);
                break;
            case 1:
                // 1000m
                dist = 1.0;
                searchSNS(dist);
                break;
            case 2:
                // 1500m
                dist = 1.5;
                searchSNS(dist);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                SNS_DIALOG = new SnsDialogManager(getActivity(), this).setIcon(R.drawable.logo).setTitle("SNS 보내기").show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == adapter.getCount() - 1) {
            SNS_DIALOG = new SnsDialogManager(getActivity(), this).setIcon(R.drawable.logo).setTitle("SNS 보내기").show();
        }
    }
}

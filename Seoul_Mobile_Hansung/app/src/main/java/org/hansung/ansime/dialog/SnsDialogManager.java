package org.hansung.ansime.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.hansung.ansime.MainActivity;
import org.hansung.ansime.R;
import org.hansung.ansime.fragment.SnsFragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 호영 on 2016-09-15.
 * Sns Dialog Manager Class.
 * Tmap api 를 이용하여 좌표를 지정해서 사용장의 입력값을 받는 Dialog 생성해주는 Class.
 * Android - PHP - MySQL 통신으로 데이터를 입력.
 */
public class SnsDialogManager extends AlertDialog.Builder implements View.OnClickListener {

    private EditText contents;
    private final String api_key = "5e16fa28-d0df-31c5-9fc0-482a7b71eb6c";
    private TMapView tMapView;
    private boolean preBtnClickFlag = false;
    private SnsFragment fragment;

    public SnsDialogManager(final Context context, final SnsFragment fragment) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.fragment_sns_dialog, null, false);

        this.fragment = fragment;

        // Tmap 적용 부분
        tMapView = new TMapView(getContext());
        tMapView.setSKPMapApiKey(api_key); // ApiKey 설정
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN); // 언어 설정
        // tmapview.setIconVisibility(true); // 현재 위치로 표시될 아이콘 표시 여부 설정
        tMapView.setZoomLevel(16); // 지도 축척 레벨을 설정 (7~19) 숫자가 높을 수록 확대
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD); // 지도 타입 설정 (일반지도)
        tMapView.setCompassMode(false); // 나침판 모드 설정
        tMapView.setTrackingMode(true); // 화면 중심으로 단말의 현재 위치로 이동
        // 이전 좌표가 있을 시에
        if (MainActivity.APPDATA.getLat() != 0 && MainActivity.APPDATA.getLon() != 0) {
            preBtnClickFlag = true;
            TMapPoint p = new TMapPoint(MainActivity.APPDATA.getLat(), MainActivity.APPDATA.getLon());
            setTmapLocationPoint(p, 16); // p의 위치를 화면 중앙으로 이동
        }
        RelativeLayout tMap_layout = (RelativeLayout) viewDialog.findViewById(R.id.tmap_layout);
        contents = (EditText) viewDialog.findViewById(R.id.contents);
        tMap_layout.addView(tMapView);

        // 이미지 뷰를 지도 위에 보여주기 위함 ( myPoint가 아래에 덮히는 것을 해결 )
        ImageView myPoint = (ImageView) viewDialog.findViewById(R.id.point);
        myPoint.bringToFront();

        // 버튼 선언
        Button preBtn = (Button) viewDialog.findViewById(R.id.preBtn);
        Button curBtn = (Button) viewDialog.findViewById(R.id.curBtn);
        Button cancelBtn = (Button) viewDialog.findViewById(R.id.cancelBtn);
        Button sendBtn = (Button) viewDialog.findViewById(R.id.sendBtn);

        // 버튼 이벤트 등록
        preBtn.setOnClickListener(this);
        curBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        this.setCancelable(true);
        this.setView(viewDialog);
    }

    // 매개 변수로 넘어온 point를 화면 중앙으로 위치 시켜주는 함수
    private void setTmapLocationPoint(TMapPoint point, int zoomLevel) {
        tMapView.setLocationPoint(point.getLongitude(), point.getLatitude()); // 현재 위치 설정
        tMapView.setZoomLevel(zoomLevel); // 줌 레벨 설정
        tMapView.setTrackingMode(true); // 현재 위치를 화면 중앙으로 설정
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preBtn:
                if (MainActivity.APPDATA.getLat() != 0 && MainActivity.APPDATA.getLon() != 0) {
                    preBtnClickFlag = true;
                    TMapPoint p = new TMapPoint(MainActivity.APPDATA.getLat(), MainActivity.APPDATA.getLon());
                    setTmapLocationPoint(p, 16);
                }
                break;
            case R.id.curBtn:
                preBtnClickFlag = false;
                setTmapLocationPoint(MainActivity.GPS.getCur(), 16);
                break;
            case R.id.cancelBtn:
                SnsFragment.SNS_DIALOG.dismiss();
                break;
            case R.id.sendBtn:
                TMapPoint p = tMapView.getCenterPoint();
                String urlStr;
                String[] partContents = contents.getText().toString().split(" ");
                String content = "";
                for (String partContent : partContents) {
                    content += partContent + "%20";
                }
                int type = 0;
                if (preBtnClickFlag)
                    type = MainActivity.APPDATA.getType();
                urlStr = String.format("http://codeman.ivyro.net/addData.php?lat=%s&lon=%s&type=%d&contents=%s", p.getLatitude(), p.getLongitude(), type, content);
                addData(urlStr);
                break;
        }
    }

    // 안드로이드의 Data를 저장 함수
    public void addData(String url) {
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
                        conn.setConnectTimeout(1000);
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
                    ex.printStackTrace();
                }
                return jsonHtml.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                SnsFragment.SNS_DIALOG.dismiss();
                if (result.trim().equals("success")) {
                    fragment.searchSNS(fragment.getDist());
                    Toast.makeText(getContext(), "등록 성공", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "등록 실패", Toast.LENGTH_LONG).show();
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}

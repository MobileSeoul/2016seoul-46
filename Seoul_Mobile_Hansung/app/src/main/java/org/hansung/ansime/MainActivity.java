package org.hansung.ansime;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.hansung.ansime.dto.AppData;
import org.hansung.ansime.dto.CsvConverter;
import org.hansung.ansime.dto.GpsData;
import org.hansung.ansime.dto.MarkerData;
import org.hansung.ansime.fragment.AlarmFragment;
import org.hansung.ansime.fragment.MapFragment;
import org.hansung.ansime.fragment.SettingFragment;
import org.hansung.ansime.fragment.SnsFragment;
import org.hansung.ansime.gps.GpsManager;

import java.util.ArrayList;

/**
 * Created by 호영 on 2016-09-04.
 * Main Activity.
 */
public class MainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {
    public static GpsData GPS = new GpsData();
    public static AppData APPDATA;

    private ImageView[] tabImgs = new ImageView[4]; // 탭 메뉴 이미지
    private ArrayList<MarkerData> markerArray; // Tmap에 뿌려줄 마커 데이터 리스트
    private GpsManager gpsManager;

    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APPDATA = new AppData(this); // 설정값을 가져옴
        markerArray = CsvConverter.run(this); // 마커 데이터 리스트를 가져옴
        init_actionBar(); // 액션바 초기화
        gpsManager = new GpsManager(this);

        ChangeFragment(1);

        init_TabMenu(); // 탭메뉴 초기화
    }

    @Override
    protected void onResume() {
        super.onResume();
        gpsManager.start();
    }

    // 액션바 초기화 함수
    private void init_actionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
    }

    // 탭 메뉴 초기화 함수
    private void init_TabMenu() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        this.setNewTab(tabHost, "tab0", R.string.tab_menu_title0, R.drawable.ic_notifications_active_black_24dp, R.id.fragment, 0);
        this.setNewTab(tabHost, "tab1", R.string.tab_menu_title1, R.drawable.ic_location_on_pink_24dp, R.id.fragment, 1);
        this.setNewTab(tabHost, "tab2", R.string.tab_menu_title2, R.drawable.ic_textsms_black_24dp, R.id.fragment, 2);
        this.setNewTab(tabHost, "tab3", R.string.tab_menu_title3, R.drawable.ic_settings_black_24dp, R.id.fragment, 3);

        tabHost.setCurrentTab(1);

        // 탭 리스너 등록
        tabHost.setOnTabChangedListener(this);
    }

    // 탭 메뉴에 탭 추가 함수
    private void setNewTab(TabHost tabHost, String tag, int title, int icon, int contentID, int index) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), title, icon, index));
        tabSpec.setContent(contentID);
        tabHost.addTab(tabSpec);
    }

    // 탭 메뉴에 탭 레이아웃 적용 함수
    private View getTabIndicator(Context context, int title, int icon, int index) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        tabImgs[index] = (ImageView) view.findViewById(R.id.imageView);
        tabImgs[index].setImageResource(icon);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }

    // 탭 메뉴가 선택되었을 때 아이콘 이미지 변경 함수
    private void setSelectTabMenuImage(int index) {
        int[] d_id = {R.drawable.ic_notifications_active_black_24dp, R.drawable.ic_location_on_black_24dp, R.drawable.ic_textsms_black_24dp, R.drawable.ic_settings_black_24dp};
        int[] s_id = {R.drawable.ic_notifications_active_pink_24dp, R.drawable.ic_location_on_pink_24dp, R.drawable.ic_textsms_pink_24dp, R.drawable.ic_settings_pink_24dp};
        for (int i = 0; i < 4; i++) {
            tabImgs[i].setImageResource(d_id[i]);
        }
        tabImgs[index].setImageResource(s_id[index]);
    }

    // 탭 변경 이벤트 함수
    @Override
    public void onTabChanged(String tabId) {
        // String str;
        // str = "onTabChanged : " + tabId;
        // Log.v("test", str);
        switch (tabId) {
            case "tab0":
                setSelectTabMenuImage(0);
                ChangeFragment(0);
                break;
            case "tab1":
                setSelectTabMenuImage(1);
                ChangeFragment(1);
                break;
            case "tab2":
                setSelectTabMenuImage(2);
                ChangeFragment(2);
                break;
            case "tab3":
                setSelectTabMenuImage(3);
                ChangeFragment(3);
                break;
        }

    }

    // Fragment 이동 함수
    public void ChangeFragment(int id) {
        Fragment fragment;
        switch (id) {
            default:
            case 0:
                fragment = new AlarmFragment();
                break;
            case 1:
                fragment = new MapFragment();
                break;
            case 2:
                fragment = new SnsFragment();
                break;
            case 3:
                fragment = new SettingFragment();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public ArrayList<MarkerData> getMarkerArray() {
        return markerArray;
    }

    // GPS 가 최초 검색이 되었는 지를 반환해주는 함수
    public static boolean isSearch() {
        return GPS.getCur() != null;
    }

    // GPS 가 켜져있는 지를 체크해주는 함수
    public void checkGPS() {
        if (!gpsManager.getmLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS 기능이 꺼져있습니다.\n이전 탐색된 위치로 검색됩니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsManager.stop();
    }

    @Override
    public void onBackPressed() {
        // Log.v("test", "" + mDrawerLayout.isDrawerOpen(GravityCompat.START));
        // MainFragment일 때
        // 뒤로 가기 두번 누르면 종료 ( 시간 이용 )
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            this.finish();
            toast.cancel();
        }
    }
}


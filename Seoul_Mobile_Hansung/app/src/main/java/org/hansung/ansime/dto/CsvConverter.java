package org.hansung.ansime.dto;

import android.content.Context;

import org.hansung.ansime.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by 호영 on 2016-09-20.
 * Csv Converter Class.
 * csv로 저장된 파일을 ArrayList 형태로 변환 시켜주는 클래스.
 */
public class CsvConverter {
    public static ArrayList<MarkerData> run(Context context) {
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        InputStream inputStream = context.getResources().openRawResource(R.raw.tmaker);
        ArrayList<MarkerData> markerDatas = new ArrayList<MarkerData>();
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));
            int cnt = 1;
            while ((line = br.readLine()) != null) {
                String[] field = line.split(cvsSplitBy);
                MarkerData data = new MarkerData().setTitle(field[0]).setLatitude(Double.parseDouble(field[1])).setLongitude(Double.parseDouble(field[2]));
                markerDatas.add(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return markerDatas;
    }
}

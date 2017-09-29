package org.hansung.ansime.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.hansung.ansime.R;
import org.hansung.ansime.dto.SnsListViewItem;

import java.util.ArrayList;

/**
 * Created by 해든 on 2016-10-04.
 * SnsFragment - ListView Adapter Class.
 */
public class SnsListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<SnsListViewItem> listViewItemList = new ArrayList<SnsListViewItem>();

    // Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // fragment_sns_item Layout을 inflate
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_sns_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 레이아웃에 대한 정의
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.typeIcon);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
        TextView distTextView = (TextView) convertView.findViewById(R.id.dist);
        TextView contentsTextView = (TextView) convertView.findViewById(R.id.contents);

        // listViewItemList에서 position에 위치한 데이터 정의
        SnsListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        distTextView.setText(listViewItem.getDistance());

        //내용이 없다면 contentsTextView를 안보이게 함
        if (listViewItem.getContents().equals("")) {
            contentsTextView.setVisibility(View.GONE);
        } else
            contentsTextView.setText(listViewItem.getContents());

        return convertView;
    }

    // 지정한 위치에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(Drawable icon, String title, double dist, String contents) {
        SnsListViewItem item = new SnsListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        if (dist < 0)
            item.setDistance("");
        else
            item.setDistance(String.format("(%.1fm)", dist * 1000));
        item.setContents(contents);

        listViewItemList.add(item);
    }

}

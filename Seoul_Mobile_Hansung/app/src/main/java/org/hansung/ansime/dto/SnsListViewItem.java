package org.hansung.ansime.dto;

import android.graphics.drawable.Drawable;

/**
 * Created by 해든 on 2016-10-04.
 * Sns List View Item Class
 * Ssn List View 를 구성하는데 사용되는 Class
 */
public class SnsListViewItem {
    private Drawable iconDrawable;
    private String titleStr;
    private String contentsStr;
    private String distance;

    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setContents(String contents) {
        contentsStr = contents;
    }

    public Drawable getIcon() {
        return this.iconDrawable;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getDistance() {
        return this.distance;
    }

    public String getContents() {
        return this.contentsStr;
    }
}

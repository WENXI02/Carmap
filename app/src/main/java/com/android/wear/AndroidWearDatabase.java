package com.android.wear;

import android.graphics.Bitmap;

/**
 * Created by wenxi on 2017/2/14.
 */

public class AndroidWearDatabase {


    private int image,SmallIcon,Color;

    private String ContentTitle,ContentText,dismissAction;

    private Bitmap LargeIcon;

    public int getImage() {
        return image;
    }

    public void setDismissAction(String dismissAction) {
        this.dismissAction = dismissAction;
    }

    public String getDismissAction() {
        return dismissAction;
    }


    public void setImage(int image) {
        this.image = image;
    }

    public int getSmallIcon() {
        return SmallIcon;
    }

    public void setSmallIcon(int smallIcon) {
        SmallIcon = smallIcon;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public String getContentText() {
        return ContentText;
    }

    public void setContentText(String contentText) {
        ContentText = contentText;
    }

    public String getContentTitle() {
        return ContentTitle;
    }

    public void setContentTitle(String contentTitle) {
        ContentTitle = contentTitle;
    }

    public Bitmap getLargeIcon() {
        return LargeIcon;
    }

    public void setLargeIcon(Bitmap largeIcon) {
        LargeIcon = largeIcon;
    }
}

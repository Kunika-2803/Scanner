package com.kunika.kscan.Adapter;

import android.graphics.Bitmap;

public class ClickedImageList {
    private Bitmap images;
    public ClickedImageList(Bitmap images)
    {
        this.images=images;
    }
    public Bitmap getImages()
    {
        return images;
    }
    public void setImages(Bitmap images)
    {
        this.images=images;
    }
}
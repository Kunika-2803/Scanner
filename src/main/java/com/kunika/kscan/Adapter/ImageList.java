package com.kunika.kscan.Adapter;

import android.graphics.Bitmap;

public class ImageList {
    private Bitmap images;
    public ImageList(Bitmap images)
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
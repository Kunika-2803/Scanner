package com.kunika.kscan.Adapter;

import android.graphics.Bitmap;

public class FolderConstants {
    private Bitmap mfolderimgs;
    private String mfoldername;
    private String mnumberOfImages;
    public FolderConstants(Bitmap folderimgs,String foldername,String numberOfImages)
    {
        this.mfolderimgs=folderimgs;
        this.mfoldername=foldername;
        this.mnumberOfImages=numberOfImages;
    }
    public Bitmap getMfolderimgs()
    {
        return mfolderimgs;
    }
    public String getMfoldername()
    {
        return mfoldername;
    }
    public String getMnumberOfImages()
    {
        return mnumberOfImages;
    }
    public void setMfolderimgs(Bitmap folder_image)
    {
        this.mfolderimgs=folder_image;
    }
    public void setMfoldername(String folder_name)
    {
        this.mfoldername=folder_name;
    }
    public void setMnumberOfImages(String numberOfImages)
    {
        this.mnumberOfImages=numberOfImages;
    }
}

package com.kunika.kscan.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.graphics.Bitmap;
import android.os.Environment;

import com.kunika.kscan.Adapter.ClickedImageList;
import com.kunika.kscan.Adapter.FolderConstants;
import com.kunika.kscan.Adapter.ImageList;

import java.io.File;
import java.util.ArrayList;
public class Constants
{
    private Constants()
    {

    }
    public static final String MYPREFS = "mySharedPreferences";
    public static final String SCAN_IMAGE_LOCATION= Environment.getExternalStorageDirectory()+ File.separator+"KScan";
    public static ImageList imageList;
    public static ClickedImageList clickedImageList;
    public static ArrayList<ImageList> mImageList;
    public static ArrayList<ClickedImageList> clickedImageListArrayList;
    public static FolderConstants foldList;
    final public static ArrayList<FolderConstants> arrayFolderList=new ArrayList<>();
    public static Bitmap selectedImageBitmap;
    public static Bitmap bitImages[];
    public static int index;
    public static long[] getCount;
    public static Bitmap[] foldBitmap;
    public static String[] foldName;
}

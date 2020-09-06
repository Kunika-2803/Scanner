package com.kunika.kscan.utils;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utilities {
    private Utilities()
    {

    }
    public static String generateFilename()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return "scan"+sdf.format(new Date());
    }
    public static void convertBitmapToMat(Bitmap bitmap, Mat mat)
    {
        Utils.bitmapToMat(bitmap, mat);
    }
    public static String generatePdfName()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return "Kscanner"+sdf.format(new Date())+".pdf";
    }
}

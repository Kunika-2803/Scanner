package com.kunika.kscan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kunika.kscan.libraries.NativeClass;
import com.kunika.kscan.libraries.PolygonView;
import com.kunika.kscan.utils.Constants;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunika.kscan.utils.Constants.imageList;
import static com.kunika.kscan.utils.Constants.mImageList;
import static com.kunika.kscan.utils.Constants.selectedImageBitmap;

public class ImageCrop extends Activity {

    FrameLayout holderImageCrop;
    ImageView imageView;
    PolygonView polygonView;
    Bitmap selectedImageBitmap;
    public static Button btnImageEnhance;
    String value;

    NativeClass nativeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        Intent i=getIntent();
        value=i.getStringExtra("StringValue");
        initializeElement();
    }

    private void initializeElement() {
        nativeClass = new NativeClass();
        btnImageEnhance = findViewById(R.id.btnImageEnhance);
        holderImageCrop = findViewById(R.id.holderImageCrop);
        imageView = findViewById(R.id.imageView);
        polygonView = findViewById(R.id.polygonView);
        holderImageCrop.post(new Runnable() {
            @Override
            public void run() {
                initializeCropping();
            }
        });
        btnImageEnhance.setOnClickListener(btnImageEnhanceClick);

    }

    private void initializeCropping() {
        selectedImageBitmap = Constants.selectedImageBitmap;
        Constants.selectedImageBitmap = null;

        Bitmap scaledBitmap = scaledBitmap(selectedImageBitmap,holderImageCrop.getWidth(),holderImageCrop.getHeight());
        imageView.setImageBitmap(scaledBitmap);

        Bitmap tempBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);

        int padding = (int) getResources().getDimension(R.dimen.scanPadding);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;

        polygonView.setLayoutParams(layoutParams);
    }

    private View.OnClickListener btnImageEnhanceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //save selected bitmap to our constants
            //this method will save the image to our device memory
            //so set this variable to null after the image is no longer used
            if(value.equals("Camera")) {
                Constants.selectedImageBitmap = getCroppedImage();
                //create new intent to start process image
                Intent intent = new Intent(getApplicationContext(), ImageEnhance.class);
                intent.putExtra("StringValue", "Camera");
                startActivity(intent);
            }
            else
            {
                Constants.selectedImageBitmap = getCroppedImage();
                //create new intent to start process image
                Intent intent = new Intent(getApplicationContext(), ImageEnhance.class);
                intent.putExtra("StringValue", "Gallery");
                startActivity(intent);
            }
        }
    };

    protected Bitmap getCroppedImage() {

        Map<Integer, PointF> points = polygonView.getPoints();

        float xRatio = (float) selectedImageBitmap.getWidth() / imageView.getWidth();
        float yRatio = (float) selectedImageBitmap.getHeight() / imageView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;

        return nativeClass.getScannedBitmap(selectedImageBitmap, x1, y1, x2, y2, x3, y3, x4, y4);
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Log.v("KScan", "scaledBitmap");
        Log.v("KScan", width + " " + height);
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0,bitmap.getWidth(),bitmap.getHeight()), new RectF(0, 0,width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), m, true);
    }
    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        Log.v("KScan", "getEdgePoints");
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        Log.v("KScan", "getContourEdgePoints");
        nativeClass=new NativeClass();
        MatOfPoint2f point2f=new MatOfPoint2f();
        point2f=nativeClass.getPoint(tempBitmap);
        List<PointF> result = new ArrayList<>();
        List<Point> points = Arrays.asList(point2f.toArray());
        for (int i = 0; i < points.size(); i++) {
            result.add(new PointF(((float) points.get(i).x), ((float) points.get(i).y)));
        }
        return result;
    
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Log.v("KScan", "getOutlinePoints");
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Log.v("KScan", "orderedValidEdgePoints");
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }
    private void detectEdges(Bitmap bitmap)
    {
        Mat rgba=new Mat();
        Utils.bitmapToMat(bitmap,rgba);
        Mat edges=new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba,edges,Imgproc.COLOR_RGB2GRAY,4);
        Imgproc.Canny(edges,edges,80,100);


    }
    public void createBitmap()
    {
        Bitmap bitmap= Constants.selectedImageBitmap;
        Bitmap bitmap1=bitmap.copy(bitmap.getConfig(),true);
        Bitmap bitmap2=Bitmap.createBitmap(400,400,bitmap.getConfig());
        Canvas polyCanvas=new Canvas(bitmap2);
        Canvas canvas=new Canvas(bitmap1);
        Paint paint=new Paint();
        paint.setStrokeWidth(9f);
        Path path=new Path();
        Map<Integer, PointF> points = polygonView.getPoints();

        float xRatio = (float) selectedImageBitmap.getWidth() / imageView.getWidth();
        float yRatio = (float) selectedImageBitmap.getHeight() / imageView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;


        path.moveTo(150f,0f);
        path.lineTo(x1,y1);
        path.lineTo(x2,y2);
        path.lineTo(x3,y3);
        path.lineTo(x4,y4);
        path.lineTo(150f,0f);
        polyCanvas.drawPath(path,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        polyCanvas.drawBitmap(bitmap1,0f,0f,paint);
        selectedImageBitmap=bitmap2;

    }
}

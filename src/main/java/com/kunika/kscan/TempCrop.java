package com.kunika.kscan;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;

import com.kunika.kscan.libraries.PolygonView;
import com.kunika.kscan.utils.Constants;

import java.util.List;
import java.util.Map;

import static com.kunika.kscan.utils.Constants.selectedImageBitmap;

public class TempCrop extends AppCompatActivity {
PolygonView polygonView;
ImageView imageView;
Button button;
FrameLayout holderImageCrop;
TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_crop);
        polygonView=findViewById(R.id.polygonView);
        imageView=findViewById(R.id.imageview);
        imageView.setImageBitmap(selectedImageBitmap);
        button=findViewById(R.id.enhance);
        textView=findViewById(R.id.textView);
        holderImageCrop=findViewById(R.id.holderImageCrop);
        holderImageCrop.post(new Runnable() {
            @Override
            public void run() {
                initializeCropping();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBitmap();
            }
        });
    }
    private void initializeCropping() {
       // selectedImageBitmap = Constants.selectedImageBitmap;
      //  Constants.selectedImageBitmap = null;
        Bitmap scaledBitmap = scaledBitmap(selectedImageBitmap,holderImageCrop.getWidth(),holderImageCrop.getHeight());
        imageView.setImageBitmap(scaledBitmap);

        Bitmap tempBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    //    Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
      //  polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);

        int padding = (int) getResources().getDimension(R.dimen.scanPadding);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;

        polygonView.setLayoutParams(layoutParams);

    }
    public void createBitmap()
    {
        Bitmap bitmap= Constants.selectedImageBitmap;
        Bitmap bitmap1=bitmap.copy(bitmap.getConfig(),true);
        Bitmap bitmap2=Bitmap.createBitmap(400,400,bitmap.getConfig());
        Canvas polyCanvas=new Canvas(bitmap2);
        Canvas canvas=new Canvas(bitmap1);
        Paint paint=new Paint();
        paint.setStrokeWidth(2f);
        Path path=new Path();
        Map<Integer, PointF> points = polygonView.getPoints();

        List<PointF> pointFs=polygonView.getPointer();

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

        float x1new=(pointFs.get(0).x)*xRatio;
        float x2new=(pointFs.get(1).x)*xRatio;
        float x3new=(pointFs.get(2).x)*xRatio;
        float x4new=(pointFs.get(3).x)*xRatio;
        float y1new=(pointFs.get(0).y)*yRatio;
        float y2new=(pointFs.get(1).y)*yRatio;
        float y3new=(pointFs.get(2).y)*yRatio;
        float y4new=(pointFs.get(3).y)*yRatio;
        /*  path.moveTo(x1,y1);
        path.lineTo(x2,y2);
        path.lineTo(x3,y3);
        path.lineTo(x4,y4);
        path.lineTo(x1,y1);*/
        path.moveTo(x1new,y1new);
        path.lineTo(x2new,y2new);
        path.lineTo(x3new,y3new);
        path.lineTo(x4new,y4new);
        path.lineTo(x1new,y1new);
      //  path.lineTo(150f,0f);
        textView.setText(x1new+" "+y1new+" "+x3new+" "+y3new);
        polyCanvas.drawPath(path,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        polyCanvas.drawBitmap(bitmap1,0f,0f,paint);
        selectedImageBitmap=bitmap2;
        imageView.setImageBitmap(selectedImageBitmap);
    }
    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Log.v("KScan", "scaledBitmap");
        Log.v("KScan", width + " " + height);
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0,bitmap.getWidth(),bitmap.getHeight()), new RectF(0, 0,width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), m, true);
    }
}

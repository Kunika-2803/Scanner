package com.kunika.kscan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.kunika.kscan.Adapter.ClickedImageAdapter;
import com.kunika.kscan.Adapter.ClickedImageList;
import com.kunika.kscan.Adapter.ImageAdapter;
import com.kunika.kscan.Adapter.ImageList;
import com.kunika.kscan.utils.Constants;

import java.io.IOException;

import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static com.kunika.kscan.utils.Constants.bitImages;
import static com.kunika.kscan.utils.Constants.clickedImageList;
import static com.kunika.kscan.utils.Constants.clickedImageListArrayList;
import static com.kunika.kscan.utils.Constants.imageList;
import static com.kunika.kscan.utils.Constants.mImageList;

public class CameraClicks extends AppCompatActivity {
    private String[] neededPermissions = new String[]{CAMERA};
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private SurfaceHolder surfaceHolder;
    private FaceDetector detector;
    RecyclerView rev_CapturedImages;
    LinearLayoutManager linearLayoutManager;
    ClickedImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_clicks);
        surfaceView = findViewById(R.id.surfaceView);
        detector = new FaceDetector.Builder(this)
                .setProminentFaceOnly(true) // optimize for single, relatively large face
                .setTrackingEnabled(true) // enable face tracking
                .setClassificationType(/* eyes open and smile */ FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE) // for one face this is OK
                .build();
        if (!detector.isOperational()) {
            Log.w("MainActivity", "Detector Dependencies are not yet available");
        } else {
            Log.w("MainActivity", "Detector Dependencies are available");
            if (surfaceView != null) {
                boolean result = checkPermission();
                if (result) {
                    setViewVisibility(R.id.tv_capture);
                    setViewVisibility(R.id.surfaceView);
                    setupSurfaceHolder();
                }
            }
        }

        clickedImageListArrayList=new ArrayList<>();
        imageAdapter=new ClickedImageAdapter(clickedImageListArrayList);
        rev_CapturedImages=findViewById(R.id.rev_CapturedImages);
        //gridLayoutManager=new GridLayoutManager(getApplicationContext(),1,RecyclerView.HORIZONTAL,false);
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rev_CapturedImages.setHasFixedSize(true);
        rev_CapturedImages.setLayoutManager(linearLayoutManager);
        rev_CapturedImages.setAdapter(imageAdapter);

        findViewById(R.id.tv_capture).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickImage();
            }
        });
    }

    private boolean checkPermission() {
        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        for (String permission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission);
            }
        }

        if (!permissionsNotGranted.isEmpty()) {
            boolean shouldShowAlert = false;
            for (String permission : permissionsNotGranted) {
                shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            }

            if (shouldShowAlert) {
                showPermissionAlert(permissionsNotGranted.toArray(new String[0]));
            } else {
                requestPermissions(permissionsNotGranted.toArray(new String[0]));
            }

            return false;
        }

        return true;
    }

    private void showPermissionAlert(final String[] permissions) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission Required");
        alertBuilder.setMessage("Camea permission is required to move forward.");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(permissions);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(CameraClicks.this, permissions, 1001);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(CameraClicks.this, "This permission is required", Toast.LENGTH_LONG).show();
                    checkPermission();
                    return;
                }
            }
            setViewVisibility(R.id.tv_capture);
            setViewVisibility(R.id.surfaceView);
            setupSurfaceHolder();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setViewVisibility(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setupSurfaceHolder() {
        cameraSource = new CameraSource.Builder(this, detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(surfaceHolder);
                    detector.setProcessor(new LargestFaceFocusingProcessor(detector,
                            new Tracker<Face>()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    private void clickImage() {
        if (cameraSource != null) {
            cameraSource.takePicture(/*shutterCallback*/null, new CameraSource.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    int nh=(int)(bitmap.getHeight()*(512.0/bitmap.getWidth()));
                    Bitmap bitmap1=Bitmap.createScaledBitmap(bitmap,512,nh,true);
                    Constants.clickedImageList=new ClickedImageList(bitmap1);
                    clickedImageListArrayList.add(clickedImageList);
                    setViewVisibility(R.id.iv_picture);
                    rev_CapturedImages.setAdapter(imageAdapter);
                    ImageView imageView=findViewById(R.id.iv_picture);
                    imageView.setImageBitmap(bitmap);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(getApplicationContext(),ImageGallery.class);
                            i.putExtra("StringValue","Camera");
                            startActivity(i);
                        }
                    });
                  //  findViewById(R.id.surfaceView).setVisibility(View.GONE);
                  //  findViewById(R.id.tv_capture).setVisibility(View.GONE);
                }
            });
        }
    }
}
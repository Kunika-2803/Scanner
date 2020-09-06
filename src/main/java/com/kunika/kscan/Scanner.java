package com.kunika.kscan;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Scanner extends JavaCameraView implements Camera.PictureCallback {

        private static final String TAG = CameraBridgeViewBase.class.getSimpleName();
        private String mPictureFileName;
        public static int minWidthQuality = 400;
        private Context context;

        public Scanner(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
        }

        public Scanner(Context context, int cameraId) {
            super(context, cameraId);
        }
        public List<String> getEffectList() {

            return mCamera.getParameters().getSupportedColorEffects();
        }

        public boolean isEffectSupported() {
            return (mCamera.getParameters().getColorEffect() != null);
        }

        public String getEffect() {
            return mCamera.getParameters().getColorEffect();
        }

        public void setEffect(String effect) {
            Camera.Parameters params = mCamera.getParameters();
            params.setColorEffect(effect);
            mCamera.setParameters(params);
        }

        public List<Camera.Size> getResolutionList() {
            return mCamera.getParameters().getSupportedPreviewSizes();
        }

        public void setResolution(Camera.Size resolution) {
            disconnectCamera();
            mMaxHeight = resolution.height;
            mMaxWidth = resolution.width;
            connectCamera(mMaxWidth, mMaxHeight);
        }

        public Camera.Size getResolution() {
            return mCamera.getParameters().getPreviewSize();
        }

        public void takePicture(final String fileName) {
            Log.i(TAG, "Taking picture");

            this.mPictureFileName = fileName;
            mCamera.setPreviewCallback(null);
            mCamera.takePicture(null,null, this);
        }

        @SuppressLint("WrongThread")
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "Saving a bitmap to file");
            // The camera preview was automatically stopped. Start it again.
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Uri uri = Uri.parse(mPictureFileName);

            Log.d(TAG, "selectedImage: " + uri);
            Bitmap bm = null;
            bm = rotate(bitmap, 90);

            // Write the image in a file (in jpeg format)
            try {
                FileOutputStream fos = new FileOutputStream(mPictureFileName);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
        }

        private static Bitmap rotate(Bitmap bm, int rotation) {
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                return bmOut;
            }
            return bm;
        }
    public void setFocusMode(Context item, int type){

        Camera.Parameters params = mCamera.getParameters();

        List<String> FocusModes = params.getSupportedFocusModes();

        switch (type){
            case 0:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                else
                    Toast.makeText(item, "Auto Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                else
                    Toast.makeText(item, "Continuous Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_EDOF))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
                else
                    Toast.makeText(item, "EDOF Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                else
                    Toast.makeText(item, "Fixed Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                else
                    Toast.makeText(item, "Infinity Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                else
                    Toast.makeText(item, "Macro Mode not supported", Toast.LENGTH_SHORT).show();
                break;
        }

        mCamera.setParameters(params);
    }

    public void setFlashMode(Context item, int type){

        Camera.Parameters params = mCamera.getParameters();
        List<String> FlashModes = params.getSupportedFlashModes();

        switch (type){
            case 0:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                else
                    Toast.makeText(item, "Auto Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_OFF))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                else
                    Toast.makeText(item, "Off Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_ON))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                else
                    Toast.makeText(item, "On Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_RED_EYE))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_RED_EYE);
                else
                    Toast.makeText(item, "Red Eye Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                else
                    Toast.makeText(item, "Torch Mode not supported", Toast.LENGTH_SHORT).show();
                break;
        }

        mCamera.setParameters(params);
    }


    }
/*
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
public class Scanner extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    public Scanner(Context context,Camera camera) {
        super(context);
        this.mCamera=camera;
        this.mSurfaceHolder=this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
    }
}
*/

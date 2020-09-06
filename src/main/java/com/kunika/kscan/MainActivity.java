package com.kunika.kscan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunika.kscan.Adapter.FolderConstants;
import com.kunika.kscan.Adapter.FoldersList;
import com.kunika.kscan.Adapter.ImageAdapter;
import com.kunika.kscan.Adapter.ImageList;
import com.kunika.kscan.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.kunika.kscan.utils.Constants.MYPREFS;
import static com.kunika.kscan.utils.Constants.SCAN_IMAGE_LOCATION;
import static com.kunika.kscan.utils.Constants.arrayFolderList;
import static com.kunika.kscan.utils.Constants.bitImages;
import static com.kunika.kscan.utils.Constants.foldName;
import static com.kunika.kscan.utils.Constants.getCount;
import static com.kunika.kscan.utils.Constants.imageList;
import static com.kunika.kscan.utils.Constants.mImageList;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    public static RecyclerView rev_folder;
    public static RecyclerView revFolderImages;
    ArrayList<FolderConstants> newFolder;
    Uri selectedImage;
    ClipData mClipData;
    Bitmap bitmap=null;
    GridLayoutManager gridLayoutManager;

    ArrayList<String> imagesEncodedList;
    String imageEncoded;
    FloatingActionButton open_gallery;
    FloatingActionButton open_camera;
    ArrayList<Uri> mArrayUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rev_folder=findViewById(R.id.rev_Folder);

        File file=new File(SCAN_IMAGE_LOCATION+'/');
        File listFile[]=file.listFiles();
        File checkFile=new File(SCAN_IMAGE_LOCATION);

        if(checkFile.exists()) {
            foldName=new String[listFile.length];
            getCount=new long[listFile.length];
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    foldName[i] = listFile[i].getName();
                    File newFile = new File(listFile[i].getPath() + "/");
                    getCount[i] = newFile.length();
                    Constants.foldList = new FolderConstants(bitmap, foldName[i], Long.toString(getCount[i]));
                }
            }
        }
        FoldersList foldersList=new FoldersList(this,arrayFolderList);
        rev_folder.setHasFixedSize(true);
        rev_folder.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences sharedPreferences=getSharedPreferences(MYPREFS,MODE_PRIVATE);
        rev_folder.setAdapter(foldersList);

        ImageAdapter imageAdapter=new ImageAdapter(mImageList);
        revFolderImages=findViewById(R.id.rev_FolderImages);
        gridLayoutManager=new GridLayoutManager(getApplicationContext(),2);
        revFolderImages.setHasFixedSize(true);
        revFolderImages.setLayoutManager(gridLayoutManager);
        revFolderImages.setAdapter(imageAdapter);

        open_gallery=findViewById(R.id.open_gallery);
        open_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Document"),1);
            }
        });

        open_camera=findViewById(R.id.open_camera);
        open_camera.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
               /* Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);*/
                startActivity(new Intent(getApplicationContext(),CameraClicks.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == 1||requestCode==REQUEST_IMAGE_CAPTURE) && resultCode == RESULT_OK && data != null) {
                String[] filePathColumn={MediaStore.Images.Media.DATA};
                imagesEncodedList=new ArrayList<>();
                mImageList=new ArrayList<>();
                if(data.getData()!=null)
                {
                    //Bundle extras=data.getExtras();
                    Uri mImageUri=data.getData();
                    InputStream inputStream=getContentResolver().openInputStream(mImageUri);
                    bitmap=BitmapFactory.decodeStream(inputStream);
                  //  bitmap= (Bitmap) extras.get("data");
                    bitImages=new Bitmap[1];
                    bitImages[0]=bitmap;
                    imageList=new ImageList(bitmap);
                    mImageList.add(imageList);
                    Cursor cursor=getContentResolver().query(mImageUri,filePathColumn,null,null,null);
                    cursor.moveToFirst();
                    int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded=cursor.getString(columnIndex);

                    cursor.close();
                }
                else
                {
                    if(data.getClipData()!=null)
                    {
                        mClipData=data.getClipData();
                        mArrayUri=new ArrayList<>();
                        Constants.mImageList=new ArrayList<>();
                        bitImages=new Bitmap[mClipData.getItemCount()];
                        for(int i=0;i<mClipData.getItemCount();i++)
                        {
                            ClipData.Item item=mClipData.getItemAt(i);
                            Uri uri=item.getUri();
                            mArrayUri.add(uri);
                            Cursor cursor=getContentResolver().query(uri,filePathColumn,null,null,null);
                            cursor.moveToFirst();
                            int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded=cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            // selectedImage=uri;
                            InputStream inputStream=getContentResolver().openInputStream(uri);
                            bitmap=BitmapFactory.decodeStream(inputStream);
                            bitImages[i]=bitmap;
                            imageList=new ImageList(bitmap);
                            mImageList.add(imageList);
                            cursor.close();
                        }
                        Log.v(TAG,"Selected Images"+mArrayUri.size());
                    }
                }
                Intent i=new Intent(getApplicationContext(),ImageGallery.class);
                i.putExtra("StringValue","Gallery");
                startActivity(i);
                // this.loadImage();
            }
            else
            {
                Toast.makeText(this, "You haven't pick image", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}

package com.kunika.kscan;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Optimizer;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.kunika.kscan.Adapter.ClickedImageAdapter;
import com.kunika.kscan.Adapter.FolderConstants;
import com.kunika.kscan.Adapter.ImageAdapter;
import com.kunika.kscan.Adapter.ImageList;
import com.kunika.kscan.libraries.PolygonView;
import com.kunika.kscan.utils.Constants;
import com.kunika.kscan.utils.Utilities;
/*import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;*/

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import static com.kunika.kscan.utils.Constants.SCAN_IMAGE_LOCATION;
import static com.kunika.kscan.utils.Constants.arrayFolderList;
import static com.kunika.kscan.utils.Constants.bitImages;
import static com.kunika.kscan.utils.Constants.clickedImageListArrayList;
import static com.kunika.kscan.utils.Constants.foldList;
import static com.kunika.kscan.utils.Constants.mImageList;
import static com.kunika.kscan.utils.Constants.selectedImageBitmap;

public class ImageGallery extends AppCompatActivity {
    public static RecyclerView rev_images;
    GridLayoutManager gridLayoutManager;
    FloatingActionButton saveImages;
    FloatingActionButton savePdf;
    Bitmap bitmap;
    public static ImageView imageView;
    public static PolygonView polygonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        Intent intent=getIntent();
        final String value=intent.getStringExtra("StringValue");

      //  imageView=findViewById(R.id.imageview);
       // polygonView = findViewById(R.id.polygonView);

        rev_images=findViewById(R.id.rev_images);
        gridLayoutManager=new GridLayoutManager(getApplicationContext(),2);
        ImageAdapter imageAdapter=new ImageAdapter(mImageList);
        rev_images.setHasFixedSize(true);
        rev_images.setLayoutManager(gridLayoutManager);
        ClickedImageAdapter clickedImageAdapter=new ClickedImageAdapter(clickedImageListArrayList);

        if(value.equals("Gallery")) {
            rev_images.setAdapter(imageAdapter);
        }
        else
        {
            rev_images.setAdapter(clickedImageAdapter);
        }

        saveImages=findViewById(R.id.saveImages);
        saveImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input=new EditText(ImageGallery.this);
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(ImageGallery.this);
                alertDialog.setMessage("Folder name:");
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Save", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File file=new File(SCAN_IMAGE_LOCATION +File.separator+input.getText().toString());
                        if(!file.exists()) {
                            file.mkdir();
                        }
                       /* if(value.equals("Gallery")) {
                            foldList = new FolderConstants(bitImages[bitImages.length - 1], input.getText().toString(), Integer.toString(bitImages.length));
                        }
                        else
                        {
                            foldList = new FolderConstants(clickedImageListArrayList.get(clickedImageListArrayList.size()-1).getImages(), input.getText().toString(), Integer.toString(clickedImageListArrayList.size()));
                        }*/
                        arrayFolderList.add(foldList);
                        try {
                            if(value.equals("Gallery"))
                            {
                            for (int i = 0; i < bitImages.length; i++) {
                                FileOutputStream fos = new FileOutputStream(file+""+File.separator + Utilities.generateFilename()+"("+i+")"+".jpg");
                                bitImages[i].compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            }
                            }
                            else {
                                for (int i = 0; i < clickedImageListArrayList.size(); i++) {
                                    FileOutputStream fos = new FileOutputStream(file + "" + File.separator + Utilities.generateFilename() + "(" + i + ")" + ".jpg");
                                    Bitmap bitmap = clickedImageListArrayList.get(i).getImages();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
            }
        });

        savePdf=findViewById(R.id.ConvertPdf);
        savePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertPdf();
            }
        });
    }
    public void convertPdf()
    {
        Document document=new Document();
      //  String directoryPath= Environment.getExternalStorageDirectory().toString();
        String pdfname=SCAN_IMAGE_LOCATION+File.separator+Utilities.generatePdfName();
        File file=new File(pdfname);

        try {
            PdfWriter.getInstance(document,new FileOutputStream(file));
            document.open();
            for(int i=0;i<mImageList.size();i++) {
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                mImageList.get(i).getImages().compress(Bitmap.CompressFormat.JPEG,100,bos);
                Image image = Image.getInstance(bos.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0) / image.getWidth()) * 100;
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                document.add(image);
            }

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(file.exists()) {
            Intent sendFile = new Intent(Intent.ACTION_SEND);
            sendFile.setType("application/pdf");
            Uri uri= FileProvider.getUriForFile(getApplicationContext(),getPackageName()+".fileprovider",file);
            sendFile.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(Intent.createChooser(sendFile,"My File"));
        }
    }
    public void convertToPdf()
    {
     /*   PDDocument document=new PDDocument();
        try {

            for(int i=0;i<10;i++)
            {
                PDPage blankPage=new PDPage();
                document.addPage(blankPage);
            }
            document.save(SCAN_IMAGE_LOCATION+File.separator+Utilities.generatePdfName());
            document.close();

            } catch (IOException e) {
            e.printStackTrace();
        }*/
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

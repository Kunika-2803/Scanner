package com.kunika.kscan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kunika.kscan.ImageCrop;
import com.kunika.kscan.ImageEnhance;
import com.kunika.kscan.ImageGallery;
import com.kunika.kscan.R;
import com.kunika.kscan.TempCrop;
import com.kunika.kscan.utils.Constants;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<ImageList> mImages;
    public ImageAdapter(ArrayList<ImageList> rImages)
    {
        //mImages=new ArrayList<>();
        mImages=rImages;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View imageView=inflater.inflate(R.layout.images_row,parent,false);
        ViewHolder viewHolder=new ViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {

        final ImageList imageList=mImages.get(position);
        ImageView images=holder.rimages;
        images.setImageBitmap(imageList.getImages());
        holder.linearImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ImageGallery.rev_images.setVisibility(View.GONE);
                Constants.selectedImageBitmap=imageList.getImages();
                Constants.index=Constants.mImageList.indexOf(imageList);
               /* ImageGallery.imageView.setVisibility(View.VISIBLE);
                ImageGallery.polygonView.setVisibility(View.VISIBLE);
                ImageGallery.imageView.setImageBitmap(Constants.selectedImageBitmap);
                ImageGallery i=new ImageGallery();
                i.createBitmap();
                ImageGallery.imageView.setVisibility(View.VISIBLE);
                ImageGallery.imageView.setImageBitmap(Constants.selectedImageBitmap);
*/
                Intent i=new Intent(v.getContext(), ImageEnhance.class);
                i.putExtra("StringValue","Camera");
                v.getContext().startActivity(i);
              //  Intent i=new Intent(v.getContext(), ImageCrop.class);
               // i.putExtra("StringValue","Gallery");
                //v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView rimages;
        public FrameLayout linearImages;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rimages=itemView.findViewById(R.id.images);
            linearImages=itemView.findViewById(R.id.LinearImages);
        }
    }
}
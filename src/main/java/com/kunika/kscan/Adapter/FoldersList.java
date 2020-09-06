package com.kunika.kscan.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kunika.kscan.MainActivity;
import com.kunika.kscan.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.kunika.kscan.utils.Constants.MYPREFS;
import static com.kunika.kscan.utils.Constants.SCAN_IMAGE_LOCATION;
import static com.kunika.kscan.utils.Constants.imageList;
import static com.kunika.kscan.utils.Constants.mImageList;


public class FoldersList extends RecyclerView.Adapter<FoldersList.ViewHolder> {

    Context context;
    private List<FolderConstants> mfolder;
    public FoldersList(Context context,List<FolderConstants> folder)
    {
        this.context=context;
        mfolder=folder;
    }

    @NonNull
    @Override
    public FoldersList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View folderView=inflater.inflate(R.layout.folders_row,parent,false);
        ViewHolder viewHolder=new ViewHolder(folderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FoldersList.ViewHolder holder, int position) {
        final FolderConstants folderConstants=mfolder.get(position);
        ImageView foldimgs=holder.rfolder_images;
        foldimgs.setImageBitmap(folderConstants.getMfolderimgs());
        TextView foldname=holder.rfolder_name;
        foldname.setText(folderConstants.getMfoldername());
        TextView numOfImgs=holder.rNumber_Of_Images;
        numOfImgs.setText(folderConstants.getMnumberOfImages());

        holder.LinearImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName=folderConstants.getMfoldername();
                File dir=new File(SCAN_IMAGE_LOCATION+File.separator+folderName+File.separator);
                File[] listFile=dir.listFiles();
                mImageList=new ArrayList<>();
                if(listFile!=null)
                {
                    for(int i=0;i<listFile.length;i++)
                    {
                        Bitmap bitmap=BitmapFactory.decodeFile(listFile[i].getAbsolutePath());
                        imageList=new ImageList(bitmap);
                        mImageList.add(imageList);
                    }
                }
                MainActivity.rev_folder.setVisibility(View.GONE);
                MainActivity.revFolderImages.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(), ""+folderConstants.getMfoldername(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mfolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView rfolder_images;
        public TextView rfolder_name;
        public TextView rNumber_Of_Images;
        public LinearLayout LinearImages;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rfolder_images=itemView.findViewById(R.id.image);
            rfolder_name=itemView.findViewById(R.id.foldername);
            rNumber_Of_Images=itemView.findViewById(R.id.numberOfImages);
            LinearImages=itemView.findViewById(R.id.LinearImages);

            this.loadPreferences();
        }
        public void loadPreferences()
        {
            SharedPreferences sharedPreferences=context.getSharedPreferences(MYPREFS,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            //rfolder_images.setImageBitmap(BitmapFactory.decodeFile());
            editor.putString("folderName",rfolder_name.getText().toString());
            editor.putString("numberOfImages",rNumber_Of_Images.getText().toString());
            editor.apply();
        }
    }

}

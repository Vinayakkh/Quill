package com.example.catchy.adapter;

import static com.example.catchy.ViewStoryActivity.FILE_TYPE;
import static com.example.catchy.ViewStoryActivity.VIDEO_URL_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.catchy.R;
import com.example.catchy.StoryAddActivity;
import com.example.catchy.ViewStoryActivity;
import com.example.catchy.model.StoriesModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoriesHolder>{

    List<StoriesModel> list;
    Activity activity;

    public StoriesAdapter(List<StoriesModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public StoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_layout,parent,false);
        return new StoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesHolder holder, @SuppressLint("RecyclerView") int position) {

        if ((position == 0)) {

            Glide.with(activity)
            .load(activity.getResources().getDrawable(R.drawable.ic_add))
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    activity.startActivity(new Intent(activity, StoryAddActivity.class));
                }
            });
        } else {


            Glide.with(activity)
                    .load(list.get(position).getUrl())
                    .timeout(6500)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(view -> {


                if (position == 0) {

                    Dexter.withContext(activity)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                        activity.startActivity(new Intent(activity, StoryAddActivity.class));

                                    } else {
                                        Toast.makeText(activity, "Please allow permission from settings.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                                }
                            }).check();

                } else {
//                activity.startActivity(new Intent());
                    Intent intent=new Intent(activity, ViewStoryActivity.class);
                    intent.putExtra(VIDEO_URL_KEY,list.get(position).getUrl());
                    intent.putExtra(FILE_TYPE,list.get(position).getType());
                    activity.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class StoriesHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageView;
        public StoriesHolder(@Nullable View itemView){
            super(itemView);

            imageView=(CircleImageView) itemView.findViewById(R.id.imageView);

        }
    }
}

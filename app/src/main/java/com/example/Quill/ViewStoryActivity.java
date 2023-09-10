package com.example.Quill;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;


public class ViewStoryActivity extends AppCompatActivity {

    public static final String VIDEO_URL_KEY = "videoURL";
    public static final String FILE_TYPE = "file type";

    PlayerView exoPlayer;

    ImageView imageView;
//    TextView userNameTv;
//    private List<StoriesModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        init();

        String url = getIntent().getStringExtra(VIDEO_URL_KEY);

        String type = getIntent().getStringExtra(FILE_TYPE);


        if (url == null || url.isEmpty()) {
            finish();
        }
//        int position = 0;
//        userNameTv.setText(username);


        if (type.contains("image")) {
            imageView.setVisibility(View.VISIBLE);
            exoPlayer.setVisibility(View.GONE);



            Glide.with(getApplicationContext()).load(url).into(imageView);

        } else {

            //video

            exoPlayer.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);


            MediaItem item = MediaItem.fromUri(url);

            ExoPlayer player = new ExoPlayer.Builder(this).build();//SimpleExoplayer
            player.setMediaItem(item);

            exoPlayer.setPlayer(player);

            player.play();
        }
    }
    //firebase init functions


    void init() {

        exoPlayer = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
//        userNameTv=findViewById(R.id.nameTV);

    }


}
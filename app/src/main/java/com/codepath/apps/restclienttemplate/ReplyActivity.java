package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class ReplyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        TextView tvReUserName = findViewById(R.id.tvReUserName);
        TextView tvReBody = findViewById(R.id.tvReBody);
        TextView tvRe= findViewById(R.id.tvRe);
        ImageView ivReProfileImage = findViewById(R.id.ivReProfileImage);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvReUserName.setText(tweet.user.name);
        tvReBody.setText(tweet.body);
        tvRe.setText("@"+tweet.user.name.toString());
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivReProfileImage);
    }

}

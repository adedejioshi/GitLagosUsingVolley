package com.example.gsa.gitlagosusingvolley;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gsa.gitlagosusingvolley.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Userprofile_Activity extends AppCompatActivity {

    private TextView username;
    private TextView html_url;
    private CircleImageView profile_pic;
    private ImageView largeImage;
    private Button shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile_);

        // find references to each variable declared.
        username = (TextView) findViewById(R.id.user_name);
        html_url = (TextView) findViewById(R.id.html_url);
        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        largeImage = (ImageView) findViewById(R.id.large_image);
        shareBtn = (Button) findViewById(R.id.shareBtn);

        // Extract the extra information that are send along with the intent
        // that opens this activity.
        Intent intent = getIntent();
        final String dev_username = intent.getStringExtra("username");
        String dev_imageUrl = intent.getStringExtra("image_url");
        final String dev_HtmlUrl = intent.getStringExtra("html_url");

        // set the title of the action bar to the username of each developer.
        setTitle(dev_username + "'s profile");


        android.support.v7.app.ActionBar actionbar =  getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back_arrow);

        // update each profile with the username and html url.
        username.setText(dev_username);
        html_url.setText(dev_HtmlUrl);

        // load and display the profile picture using Picasso library.
        Picasso.with(getApplicationContext())
                .load(dev_imageUrl)
                .noFade()
                .placeholder(R.drawable.big_dummy)
                .into(profile_pic);
        Picasso.with(getApplicationContext())
                .load(dev_imageUrl)
                .placeholder(R.drawable.big_dummy)
                .into(largeImage);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate a share intent which contains the username and the github link as the
                // content when the button is clicked.
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"Check out this awesome developer @" + dev_username + ", " + dev_HtmlUrl );
                startActivity(Intent.createChooser(shareIntent,"Share via"));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

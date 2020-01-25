package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class NewsDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private ImageView imageView;
    private TextView appbar_title , subbar_title , date , time , title ;
    private boolean ishideToolbar = false;
    private FrameLayout date_behaviour;
    private LinearLayout title_appbar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private String mUrl, mImg , mTitle,mDate, mSource, mAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout =findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        date_behaviour = findViewById(R.id.date_behavior);
        title_appbar =findViewById(R.id.title_appbar);
        imageView = findViewById(R.id.backdrop);
        appbar_title= findViewById(R.id.title_on_appbar);
        subbar_title = findViewById(R.id.subtitle_on_appbar);
        date=findViewById(R.id.date);
        time= findViewById(R.id.time);
        title = findViewById(R.id.title);


        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mImg = intent.getStringExtra("img");
        mTitle= intent.getStringExtra("title");
        mDate= intent.getStringExtra("date");
        mSource = intent.getStringExtra("source");
        mAuthor = intent.getStringExtra("author");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utils.getRandomDrawbleColor());

        Glide.with(this)
                .load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        appbar_title.setText(mSource);
        subbar_title.setText(mUrl);
        date.setText(Utils.DateFormat(mDate));
        title.setText(mTitle);

        String author = null ;
        if (mAuthor != null  || mAuthor !="")
        {
            mAuthor = "\u2022" + mAuthor;
        }else
        {author="";}

        time.setText(mSource + author + "\u2022" +Utils.DateToTimeFormat(mDate));
        webView(mUrl);

    }

    private void webView (String Url)
    {

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(Url);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

 int max_scroll = appBarLayout.getTotalScrollRange();
 float precentage =  (float ) Math.abs(i) / (float) max_scroll;

 if (precentage == 1f && ishideToolbar)
 {
     date_behaviour.setVisibility(View.GONE);
     title_appbar.setVisibility(View.VISIBLE);
     ishideToolbar = !ishideToolbar;
 }
 else  {

     date_behaviour.setVisibility(View.VISIBLE);
     title_appbar.setVisibility(View.GONE);
     ishideToolbar = !ishideToolbar;

 }




    }
}

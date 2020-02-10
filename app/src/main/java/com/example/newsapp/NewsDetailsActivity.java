package com.example.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
        //show an arrow to the left indicating the action that will be taken. Set this flag if selecting the 'home' button in the action bar to return up by a single level in your UI rather than back to the top level or front page.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout =findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this); // to be sure that the toolbar is collapsed

        date_behaviour = findViewById(R.id.date_behavior); // frame layout
        title_appbar =findViewById(R.id.title_appbar); // linearlayout
        imageView = findViewById(R.id.backdrop);     // the big image
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_news , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();
        if (id == R.id.web_view){

            // open a link in a browser using an intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mUrl));
            startActivity(intent);
            return true;


        }else if (id == R.id.share_ic){

            try {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plan");

                //subject and body of the mail to be send
                intent.putExtra(Intent.EXTRA_SUBJECT, mSource);
                String body = mTitle+"\n"+mUrl+"\n"+"Share from the news app "+ "\n";
                intent.putExtra(Intent.EXTRA_TEXT , body);

               // In order to display the Android Sharesheet you need to call
                startActivity(Intent.createChooser(intent , "Share with :"));


            }catch(Exception e )
            {
                Toast.makeText(this , "Sorry cannot be share !! " , Toast.LENGTH_SHORT).show();
            }

        }



        return super.onOptionsItemSelected(item);
    }




}

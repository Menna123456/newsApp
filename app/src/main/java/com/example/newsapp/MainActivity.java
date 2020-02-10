package com.example.newsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.Adapter.Adapter;
import com.example.newsapp.Interface.ResponseInterface;
import com.example.newsapp.Model.Articles;
import com.example.newsapp.Model.News;
import com.example.newsapp.api.apiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static final String Api_Key = "3f055d59f0f5405e84905736eaf48b78";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager LayoutManager;
    private List<Articles> articles = new ArrayList<>();
    private com.example.newsapp.Adapter.Adapter adapter;
    private String Tag =MainActivity.class.getSimpleName();


    private RelativeLayout errorLayout;
    private ImageView errorImage;
    private TextView errorTitle, errorMessage ;
    private Button retryButton;
    
    private EditText searchText;
    private ImageButton srchBtn;
    private RecyclerView resultList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errorImage = (ImageView) findViewById(R.id.errorimg);
        errorTitle = (TextView) findViewById(R.id.error_title);
        errorMessage = (TextView) findViewById(R.id.error_message);
        retryButton = (Button)  findViewById(R.id.retrybtn);


        swipeRefreshLayout = findViewById(R.id.swipeLayout);
       //adds a listener to let other parts of the code know when refreshing begins.
       swipeRefreshLayout.setOnRefreshListener(this);
       swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        recyclerView = findViewById(R.id.resultlist);
        // use a linear layout manager
        LayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);



       adapter = new Adapter(articles, MainActivity.this);
       recyclerView.setAdapter(adapter);


        //loadJson("");
        onLoadingSwipeRefresh("");
        
    }




    public void loadJson (final String keyword)
    {
        errorLayout.setVisibility(View.GONE);
        //Refresh the layout
        swipeRefreshLayout.setRefreshing(true);
        ResponseInterface responseInterface = apiClient.getApiClient().create(ResponseInterface.class);

        String country = Utils.getCountry();
        String language = Utils.getLanguage();
        //String source = "techcrunch";

        Call<News> call ;


        if (keyword.length()>0)
        {
            call = responseInterface.getNewsSearch(keyword,language,"publishedAt" , Api_Key);
        }else{


            call = responseInterface.getNews(country,Api_Key);

        }


        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                //Toast.makeText(MainActivity.this , " Response!" , Toast.LENGTH_LONG).show();


                if (response.isSuccessful() && response.body().getArticles()!=null )
                {
                   // Toast.makeText(MainActivity.this , " succ!" , Toast.LENGTH_LONG).show();

                    if (!articles.isEmpty())
                    {

                        articles.clear();
                      //  Toast.makeText(MainActivity.this , " cleared!" , Toast.LENGTH_LONG).show();

                    }

                        articles =response.body().getArticles();
                        adapter = new Adapter(articles,MainActivity.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        init_Listner();

                        swipeRefreshLayout.setRefreshing(false);
                  //  Toast.makeText(MainActivity.this , " Result Found !" , Toast.LENGTH_LONG).show();


                }else{
                    swipeRefreshLayout.setRefreshing(false);

                    String errorCode;
                    switch(response.code())
                    {
                        case  404:
                            errorCode = "404 not found";
                            break;

                        case 500:
                            errorCode="500 server broken";
                            break;

                         default:
                             errorCode="unknown error";
                             break;

                    }

                    showErrorMessage(R.drawable.noresult_ic , "No Result" , "Please Try Again"+"\n" + errorCode);

                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);

                showErrorMessage(R.drawable.noresult_ic , "oops!" , "Network failure, Please Try Again"+"\n"+t.toString());


            }
        });

    }


    private void init_Listner() {

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
              ImageView imageView = view.findViewById(R.id.img);

                Intent intent = new Intent(MainActivity.this , NewsDetailsActivity.class);

                Articles article = articles.get(position);
                intent.putExtra("url" , article.getUrl());
                intent.putExtra("date" , article.getPublishedAt());
                intent.putExtra("author" , article.getAuthor());
                intent.putExtra("source" , article.getSource().getName());
                intent.putExtra("img" , article.getUrlToImage());
                intent.putExtra("title" , article.getTitle());

                Pair<View, String> p1 = Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this , p1);


               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    startActivity(intent,optionsCompat.toBundle());
               }else {
                    startActivity(intent);
               }


            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length()>2){
                   // loadJson(query);
                    onLoadingSwipeRefresh(query);
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

         searchMenuItem.getIcon().setVisible(false,false);

        return true;
    }

    @Override
    public void onRefresh() {
        loadJson("");
    }


    private void onLoadingSwipeRefresh (final String keyword)
    {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadJson(keyword);
                    }
                }
        );
    }


    public void showErrorMessage (int imageView ,  String title , String message )
    {

        if (errorLayout.getVisibility()==View.GONE)
        {
            errorLayout.setVisibility(View.VISIBLE);
        }


        errorImage.setImageResource(imageView);
        errorTitle.setText(title);
        errorMessage.setText(message);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingSwipeRefresh("");
            }
        });


    }



}

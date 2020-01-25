package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.view.View;
import android.widget.SearchView;

import android.app.SearchManager;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

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


    
    private EditText searchText;
    private ImageButton srchBtn;
    private RecyclerView resultList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

       swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
       swipeRefreshLayout.setOnRefreshListener(this);
       swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        recyclerView = (RecyclerView) findViewById(R.id.resultlist);
        LayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);



       adapter = new Adapter(articles, MainActivity.this);
       recyclerView.setAdapter(adapter);


        //loadJson("");
        onLoadingSwipeRefresh("");
        
    }




    public void loadJson (final String keyword)
    {


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

                    Toast.makeText(MainActivity.this , "No Result Found !" , Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);

                Toast.makeText(MainActivity.this , "failed" +
                        "!" , Toast.LENGTH_LONG).show();

            }
        });

    }


    private void init_Listner() {

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this , NewsDetailsActivity.class);

                Articles article = articles.get(position);
                intent.putExtra("url" , article.getUrl());
                intent.putExtra("date" , article.getPublishedAt());
                intent.putExtra("author" , article.getAuthor());
                intent.putExtra("source" , article.getSource().getName());
                intent.putExtra("img" , article.getUrlToImage());
                intent.putExtra("title" , article.getTitle());


               startActivity(intent);

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
}

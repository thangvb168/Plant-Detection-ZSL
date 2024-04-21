package com.example.plansdetection.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.plansdetection.R;
import com.example.plansdetection.adapter.NewsRecycleAdapter;
import com.example.plansdetection.constant.Constant;
import com.example.plansdetection.service.APIService;
import com.example.plansdetection.service.ResponseAPI;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.Source;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ManageFragment";
    RecyclerView news_recycler_view;
    List<Article> articleList = new ArrayList<>();
    NewsRecycleAdapter adapter;
    LinearProgressIndicator progress_bar;
    Button btn1, btn2, btn3, btn4, btn5, btn6;
    LinearLayout llCategories;

    public ManageFragment() {
        // Required empty public constructor
    }

    public static ManageFragment newInstance() {
        ManageFragment fragment = new ManageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addControl(view);
        setupRecyclerView();
//        getNews(getFullCategories());
        getNews(Constant.API_CATEGORY_ALL, "*");
    }

    private String getFullCategories() {
        List<String> categories = new ArrayList<>();

        for(Constant.Category c : Constant.API_CATEGORY) {
            categories.add(c.getEngTitle());
        }
        return String.join(",", categories);
    }

    private void addControl(View view){
        news_recycler_view = view.findViewById(R.id.news_recycler_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);

        llCategories = view.findViewById(R.id.llCategories);
//        SETUP categories
        setupCategories();

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);

    }

    private void setupCategories() {
        llCategories.removeAllViews(); // Clear existing child views

        List<Constant.Category> categories = new ArrayList<>(Constant.API_CATEGORY);

        for (Constant.Category category : categories) {
            Button button = new Button(getContext());
            button.setText(category.getViTitle().toUpperCase());
            button.setTextColor(getResources().getColor(R.color.white));
            button.setBackgroundColor(Color.parseColor("#2EBA90"));
            button.setPadding(10, 0, 10, 0);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(16, 8, 16, 8);
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedCategory = category.getEngTitle();
                    getNews(selectedCategory, category.getSearch());
                }
            });

            llCategories.addView(button);
        }
    }


    void setupRecyclerView(){
        news_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsRecycleAdapter(articleList);
        news_recycler_view.setAdapter(adapter);
    }

    void changeInProgress(boolean show){
        if(show)
            progress_bar.setVisibility(View.VISIBLE);
        else
            progress_bar.setVisibility(View.GONE);
    }
//    REBUILD GET NEWS
    private void getNews(String category, String search) {
        changeInProgress(true);
        APIService.apiService.getAllNews(
                Constant.API_KEY,
                Constant.API_COUNTRY,
                category,
                search
        ).enqueue(new Callback<APIService.AllAPIResponse>() {
            @Override
            public void onResponse(Call<APIService.AllAPIResponse> call, Response<APIService.AllAPIResponse> response) {
                if(response.isSuccessful()) {
                    List<ResponseAPI> newsList = response.body().getResults();
                    articleList.clear();
                    for(ResponseAPI r : newsList) {
                        Article article = new Article();

                        article.setSource(new Source());
                        article.setAuthor("");
                        article.setTitle(r.getTitle());
                        article.setDescription(r.getDescription());
                        article.setUrl(r.getLink());
                        article.setUrlToImage(r.getImageUrl());
                        article.setPublishedAt(r.getPubDate());
                        article.setContent("");
                        articleList.add(article);
                        Log.v(TAG, "Successfully::" + r.getTitle() + "::" + r.getLink());
                    }
                    adapter.notifyDataSetChanged();
                    changeInProgress(false);

                } else {
                    Log.v(TAG, "Call API failed");
                }
            }

            @Override
            public void onFailure(Call<APIService.AllAPIResponse> call, Throwable throwable) {
                Log.v(TAG, "Call API failed :: " + throwable.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage, container, false);
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        String category = btn.getText().toString();
        getNews(category, "*");
    }
}
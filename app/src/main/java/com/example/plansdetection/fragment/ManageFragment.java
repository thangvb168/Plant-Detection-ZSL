package com.example.plansdetection.fragment;

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

import com.example.plansdetection.R;
import com.example.plansdetection.adapter.NewsRecycleAdapter;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

public class ManageFragment extends Fragment implements View.OnClickListener{
    RecyclerView news_recycler_view;
    List<Article> articleList = new ArrayList<>();
    NewsRecycleAdapter adapter;
    LinearProgressIndicator progress_bar;
    Button btn1, btn2, btn3, btn4, btn5, btn6;

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
        getNews("GENERAL");
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

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);

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
    void getNews(String category){
        changeInProgress(true);
        NewsApiClient newsApiClient = new NewsApiClient("6586f7a335994ab1a06bc7bc5ed0415b");
        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .language("en")
                        .category(category)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        requireActivity().runOnUiThread(()->{
                            changeInProgress(false);
                            articleList.clear();
                            articleList.addAll(response.getArticles());
                            adapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT FAILURE", throwable.getMessage());
                    }
                }
        );
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
        getNews(category);
    }
}
package com.example.artownmad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.List;

//import com.kwabenaberko.newsapilib.NewsApiClient;
//import com.kwabenaberko.newsapilib.models.Article;
//import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
//import com.kwabenaberko.newsapilib.models.response.ArticleResponse;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private ListView listView;
    private NewsAdapter newsAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("HomeFragment", "Hoii");

//        dialog = new ProgressDialog(getContext());
//        dialog.setTitle("Fetching news articles...");
//        dialog.show();

        // Fetch news data
        NewsApiHelper newsApiHelper = new NewsApiHelper();
        List<NewsApiHelper.NewsItem> newsList = newsApiHelper.fetchData();

        listView = view.findViewById(R.id.news_listview);
        newsAdapter = new NewsAdapter(requireContext(), newsList);
        listView.setAdapter(newsAdapter);

        return view;
    }

}
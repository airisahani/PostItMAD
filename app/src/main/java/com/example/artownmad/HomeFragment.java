package com.example.artownmad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView recyclerView;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getContext();

//        RequestManager manager = new RequestManager(requireContext());
//        manager.getNewsHeadlines(listener, "general", null);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    private final OnFetchDataListener<NewsApiResponse> listener = new OnFetchDataListener<NewsApiResponse>(){
//
//        @Override
//        public void onFetchData(List<NewsHeadlines> list, String message) {
//            showNews(list);
//            dialog.dismiss();
//
//        }
//
//        @Override
//        public void onError(String message) {
//
//        }
//    };

//    private void showNews(List<NewsHeadlines> list) {
////        recyclerView = view.findViewById(R.id.recycler_main);
////        recyclerView.setHasFixedSize(true);
////        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
//        adapter = new CustomAdapter(getContext(), list, this);
//        recyclerView.setAdapter(adapter);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        recyclerView = view.findViewById(R.id.recycler_main);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Fetching news articles...");
        dialog.show();

        //RequestManager manager = new RequestManager(requireContext());
        //manager.getNewsHeadlines(listener, "general", null);
        return view;
    }


//    @Override
//    public void OnNewsClicked(NewsHeadlines headlines) {
//        startActivity(new Intent(requireContext(), DetailsActivity.class)
//                .putExtra("data", headlines));
//
//    }
}
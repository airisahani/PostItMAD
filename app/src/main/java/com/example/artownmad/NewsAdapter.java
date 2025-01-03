package com.example.artownmad;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsApiHelper.NewsItem> {

    private List<NewsApiHelper.NewsItem> newsList;
    Context context;

    public NewsAdapter(@NonNull Context context, List<NewsApiHelper.NewsItem> newsList) {
        super(context, 0, newsList);
        this.newsList = newsList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_news, parent, false);
        }
        NewsApiHelper.NewsItem newsItem = newsList.get(position);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);

        textViewTitle.setText(newsItem.getTitle());
        textViewDescription.setText(newsItem.getDescription());
        Picasso.get().load(newsItem.getImageUrl()).placeholder(R.drawable.blank_page).into(imageView);

        Log.d("NewsApiHelper", "newsList.get(position).getTitle() = " + newsList.get(position).getTitle());
// Set item click listener
        View finalConvertView = convertView;
        convertView.setOnClickListener(view -> {
            openNewsDetails(newsItem.getUrl(), finalConvertView);
        });
        return convertView;
    }

    private void openNewsDetails(String url, View convertView) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;

            // Create a new instance of NewsDetailsFragment
            NewsDetailsFragment fragment = NewsDetailsFragment.newInstance(url);
            // Replace the current fragment with NewsDetailsFragment
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout, fragment) // Use the ID of your FrameLayout
                    .addToBackStack(null) // Add the transaction to the back stack
                    .commit();
        }
    }
}

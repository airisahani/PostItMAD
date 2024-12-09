package com.example.artownmad.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artownmad.Adapters.HistoryAdapter;
import com.example.artownmad.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private List<HistoryItem> historyList;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewReport);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Load history from SharedPreferences
        historyList = loadHistory();

        adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    private List<HistoryItem> loadHistory() {
        SharedPreferences prefs = getSharedPreferences("HistoryPrefs", MODE_PRIVATE);
        List<HistoryItem> items = new ArrayList<>();
        int size = prefs.getInt("history_size", 0);

        for(int i = 0; i<size; i++){
            String action = prefs.getString("history_" + i, null);
            if(action != null){
                items.add(new HistoryItem(action));
            }
        }
        return items;
    }
}
package com.example.artownmad.Adapters;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.artownmad.Activities.Reports;
import com.example.artownmad.Activities.MapsActivity;
import com.example.artownmad.Activities.Reports;
import com.example.artownmad.MapFragment;
import com.example.artownmad.R;
import java.text.BreakIterator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private List<Reports> reports;
    private Context context;
    public ReportAdapter(Context context, List<Reports> reports) {
        this.context = context;
        this.reports = reports;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_archive_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reports report = reports.get(position);
        TextView emoji2 = holder.emoji;
        String status = report.getStatus();
        if(status.equalsIgnoreCase("resolved")){
            emoji2.setText("✅");
        }
        else{
            emoji2.setText("⌛");
        }
        TextView status2 = holder.status;
        status2.setText(report.getStatus());
        TextView descTextView = holder.descTextView;
        descTextView.setText(report.getDesc());
        // Format and display location
        TextView locationTextView = holder.locationTextView;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault()); // Use device's locale
            List<Address> addresses = geocoder.getFromLocation(report.getLocation().getLatitude(), report.getLocation().getLongitude(), 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String locationName = "";
                if (address.getThoroughfare() != null) {
                    locationName += address.getThoroughfare() + ", ";
                }
                if (address.getSubLocality() != null) {
                    locationName += address.getSubLocality() + ", ";
                }
                String state = address.getAdminArea();
                String country = address.getCountryName();
                if (state != null) {
                    locationName += state + ", ";
                }
                if (country != null) {
                    locationName += country;
                }
                locationName = locationName.trim(); // Remove trailing comma
                locationTextView.setText(locationName);
            } else {
                Log.d("ReportAdapter", "Geocoder returned no addresses"); // Log for debugging
                locationTextView.setText("Unknown Location");
            }
        } catch (IOException e) {
            Log.e("ReportAdapter", "Error retrieving location: " + e.getMessage()); // Log for debugging
            locationTextView.setText("Error retrieving location");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, MapFragment.class); // Assuming your map activity is named MapActivity
                intent.putExtra("latitude", report.getLocation().getLatitude());
                intent.putExtra("longitude", report.getLocation().getLongitude());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return reports.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationTextView, descTextView, userIdTextView, emoji, status;
        public ViewHolder(View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.TVReportLocation);
            descTextView = itemView.findViewById(R.id.TVReportTitle);
            emoji = itemView.findViewById(R.id.textView7);
            status = itemView.findViewById(R.id.textView6);
        }
    }
}
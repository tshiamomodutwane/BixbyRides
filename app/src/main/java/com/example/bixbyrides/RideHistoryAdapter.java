package com.example.bixbyrides;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {
    private List<RideHistoryEntry> rideHistoryList;

    public RideHistoryAdapter(List<RideHistoryEntry> rideHistoryList) {
        this.rideHistoryList = rideHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideHistoryEntry entry = rideHistoryList.get(position);
        holder.startLocationTextView.setText(entry.getStartLocation());
        holder.endLocationTextView.setText(entry.getEndLocation());
        holder.distanceTextView.setText(String.format("%.2f km", entry.getDistance()));
        holder.priceTextView.setText(String.format("R%.2f", entry.getPrice()));
        holder.timestampTextView.setText(entry.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return rideHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView startLocationTextView;
        TextView endLocationTextView;
        TextView distanceTextView;
        TextView priceTextView;
        TextView timestampTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            startLocationTextView = itemView.findViewById(R.id.startLocationTextView);
            endLocationTextView = itemView.findViewById(R.id.endLocationTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}

package com.example.parkin1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private final List<CustomLocation> locationList;
    private final OnItemClickListener onItemClickListener; // Declare the click listener interface

    // Constructor to accept the list of CustomLocation objects and the click listener
    public LocationAdapter(List<CustomLocation> locationList, OnItemClickListener listener) {
        this.locationList = locationList;
        this.onItemClickListener = listener; // Initialize the listener
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        CustomLocation location = locationList.get(position);
        holder.locationNumber.setText(location.getNumber()); // Make sure CustomLocation has getNumber method
        holder.locationName.setText(location.getName());
        holder.locationAddress.setText(location.getAddress());
        holder.locationDistance.setText(location.getDistance());
        holder.locationPrice.setText(location.getPrice());

        // Set the click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position); // Pass the click event to the listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView locationNumber, locationName, locationAddress, locationDistance, locationPrice;

        public LocationViewHolder(View itemView) {
            super(itemView);
            locationNumber = itemView.findViewById(R.id.locationNumber);
            locationName = itemView.findViewById(R.id.locationName);
            locationAddress = itemView.findViewById(R.id.locationAddress);
            locationDistance = itemView.findViewById(R.id.locationDistance);
            locationPrice = itemView.findViewById(R.id.locationPrice);
        }
    }
}

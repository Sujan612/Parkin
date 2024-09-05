package com.example.parkin1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private List<CustomLocation> locationList;

    // Constructor to accept the list of CustomLocation objects
    public LocationAdapter(List<CustomLocation> locationList) {
        this.locationList = locationList;
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
        holder.locationNumber.setText(location.getNumber());
        holder.locationName.setText(location.getName());
        holder.locationAddress.setText(location.getAddress());
        holder.locationDistance.setText(location.getDistance());
        holder.locationPrice.setText(location.getPrice());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView  locationNumber,locationName, locationAddress, locationDistance, locationPrice;

        public LocationViewHolder(View itemView) {
            super(itemView);
            locationNumber=itemView.findViewById(R.id.locationNumber);
            locationName = itemView.findViewById(R.id.locationName);
            locationAddress = itemView.findViewById(R.id.locationAddress);
            locationDistance = itemView.findViewById(R.id.locationDistance);
            locationPrice = itemView.findViewById(R.id.locationPrice);
        }
    }
}

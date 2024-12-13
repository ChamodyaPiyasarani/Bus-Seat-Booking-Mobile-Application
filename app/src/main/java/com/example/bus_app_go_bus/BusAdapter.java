package com.example.bus_app_go_bus;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    private ArrayList<Bus> busList;
    private OnItemClickListener listener;

    public BusAdapter(ArrayList<Bus> busList) {
        this.busList = busList;
    }

    @Override
    public BusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BusViewHolder holder, int position) {
        Bus bus = busList.get(position);
        holder.bind(bus, listener);
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Bus bus);
    }

    public class BusViewHolder extends RecyclerView.ViewHolder {
        public TextView busNumberTextView;
        public TextView routeTextView;

        public BusViewHolder(View itemView) {
            super(itemView);
            busNumberTextView = itemView.findViewById(R.id.busNumberTextView);
            routeTextView = itemView.findViewById(R.id.routeTextView);
        }

        public void bind(Bus bus, OnItemClickListener listener) {
            busNumberTextView.setText(bus.getBusNumber());
            routeTextView.setText(bus.getRoute());

            itemView.setOnClickListener(v -> listener.onItemClick(bus));
        }
    }
}


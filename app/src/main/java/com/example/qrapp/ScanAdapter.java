package com.example.qrapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ScanAdapter  extends FirestoreRecyclerAdapter<scans,ScanAdapter.scanViewHolder> {


    public ScanAdapter(@NonNull FirestoreRecyclerOptions<scans> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull scanViewHolder holder, int position, @NonNull scans scan) {
        holder.history.setText(scans.scan);

    }

    @NonNull
    @Override
    public scanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_scan_item,parent,false);
        return new scanViewHolder(view);
    }

    class scanViewHolder extends RecyclerView.ViewHolder{
        TextView history;

        public scanViewHolder(@NonNull View itemView) {
            super(itemView);
            history = itemView.findViewById(R.id.history);
            history.setTextIsSelectable(true);


        }
    }
}


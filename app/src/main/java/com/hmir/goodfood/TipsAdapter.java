package com.hmir.goodfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipsViewHolder> {

    private List<TipItem> tipsList;
    private Context context;
    public TipsAdapter(Context context, List<TipItem> tipsList) {
        this.context = context;
        this.tipsList = tipsList;
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tip, parent, false);
        return new TipsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
        TipItem tip = tipsList.get(position);
        holder.imageView.setImageResource(tip.getImageResId());
        holder.titleTextView.setText(tip.getTitle());
        holder.descriptionTextView.setText(tip.getDescription());
    }
    @Override
    public int getItemCount() {
        return tipsList.size();
    }

    public static class TipsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public TipsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.tip_image);
            titleTextView = itemView.findViewById(R.id.tip_title);
            descriptionTextView = itemView.findViewById(R.id.tip_description);
        }
    }
}

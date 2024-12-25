package com.hmir.goodfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private Context context;
    private List<Item> items;

    public ResultAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        Item item = items.get(position);
        holder.itemTitle.setText(item.getName());
        holder.itemDescription.setText(item.getDescription());

        // Use Glide to load images
        int imageResourceId = context.getResources().getIdentifier(item.getImageResourceName(), "drawable", context.getPackageName());
        if (imageResourceId != 0) {
            Glide.with(context).load(imageResourceId).into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.food_image_placeholder);  // Fallback image
        }

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        TextView itemDescription;
        ImageView itemImage;

        public ResultViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}

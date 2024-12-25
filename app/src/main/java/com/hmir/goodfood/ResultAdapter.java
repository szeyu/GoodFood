package com.hmir.goodfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, context);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Made static and added binding logic inside ViewHolder
    static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemTitle;
        private final TextView itemDescription;
        private final ImageView itemImage;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemImage = itemView.findViewById(R.id.item_image);
        }

        // Added bind method to handle item binding
        public void bind(Item item, Context context) {
            itemTitle.setText(item.getName());
            itemDescription.setText(item.getDescription());

            // Use Glide to load images
            int imageResourceId = context.getResources().getIdentifier(
                    item.getImageResourceName(),
                    "drawable",
                    context.getPackageName()
            );

            if (imageResourceId != 0) {
                Glide.with(context)
                        .load(imageResourceId)
                        .into(itemImage);
            } else {
                itemImage.setImageResource(R.drawable.food_image_placeholder);
            }
        }
    }
}

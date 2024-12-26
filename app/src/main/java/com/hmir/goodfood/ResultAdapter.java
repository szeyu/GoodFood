package com.hmir.goodfood;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
        holder.itemTitle.setText(item.getName());  // Display only name

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DisplayActivity.class);
            intent.putExtra("recipe_id", item.getRecipeId());  // Pass the recipe reference ID for fetching full details
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;

        public ResultViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
        }
    }
}

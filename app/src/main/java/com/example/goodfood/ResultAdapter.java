package com.example.goodfood;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private Context context;
    private Cursor cursor;

    public ResultAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int titleIndex = cursor.getColumnIndex("title");
            int descriptionIndex = cursor.getColumnIndex("description");

            String title = null;
            String description = null;

            if (titleIndex != -1) {
                title = cursor.getString(titleIndex);
            }

            if (descriptionIndex != -1) {
                description = cursor.getString(descriptionIndex);
            }
            holder.itemTitle.setText(title);
            holder.itemDescription.setText(description);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemDescription;

        public ResultViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);
        }
    }
}

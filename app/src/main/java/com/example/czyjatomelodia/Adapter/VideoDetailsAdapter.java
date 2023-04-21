package com.example.czyjatomelodia.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Model.Item;
import com.example.czyjatomelodia.MyViewHolder;
import com.example.czyjatomelodia.R;
import com.squareup.picasso.Picasso;
public class VideoDetailsAdapter extends RecyclerView.Adapter<MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mListener;
    private int selectedPos = RecyclerView.NO_POSITION;
    private boolean isSelected = false;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private Context context;
    private Item[] items;

    public VideoDetailsAdapter(Context context, Item[] items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
                notifyItemChanged(selectedPos);
                selectedPos = position;
                notifyItemChanged(selectedPos);
            }
        });

        holder.textView.setText(items[position].getSnippet().getTitle());
        Picasso.get().load(items[position].getSnippet().getThumbnails().getMedium().getUrl()).into(holder.imageView);

        if (selectedPos == position) {
            holder.cardBg.setBackgroundColor(Color.parseColor("#9f9cff"));
        } else {
            holder.cardBg.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}

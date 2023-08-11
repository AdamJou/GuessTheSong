package com.example.czyjatomelodia.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Model.Item;
import com.example.czyjatomelodia.SongViewHolder;
import com.example.czyjatomelodia.R;
import com.squareup.picasso.Picasso;
public class VideoDetailsAdapter extends RecyclerView.Adapter<SongViewHolder> {

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
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (items[position].getId().getVideoId() != null) {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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


            String title = android.text.Html.fromHtml(items[position].getSnippet().getTitle(), android.text.Html.FROM_HTML_MODE_COMPACT).toString();




            for (String phrase : holder.unwantedPhrases) {
                title = title.replace(phrase, "");
            }
            holder.textView.setText(title);
            Picasso.get().load(items[position].getSnippet().getThumbnails().getMedium().getUrl()).into(holder.imageView);

            if (selectedPos == position) {
                holder.cardBg.setBackgroundColor(Color.parseColor("#9f9cff"));
                holder.textView.setTypeface(null, Typeface.BOLD);

            } else {
                holder.cardBg.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.textView.setTypeface(null, Typeface.NORMAL);

            }
        } else {
            // Jeśli element nie jest filmem (jest kanałem), ukrywamy go w RecyclerView
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
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

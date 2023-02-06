package com.example.czyjatomelodia.Adapter;

/*
public class VideoDetailsAdapter extends RecyclerView.Adapter<VideoDetailsAdapter.VideoDetailsViewHolder> {
    private Context context;
    private List<Item> videoDetailsList;


    public VideoDetailsAdapter(Context context, List<Item> videoDetailsList) {
        this.context = context;
        this.videoDetailsList = videoDetailsList;
    }

    @NonNull
    @Override
    public VideoDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
       View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

       return new VideoDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoDetailsViewHolder holder, int position) {

        holder.publishetAt.setText(videoDetailsList.get(position).getSnippet().getPublishedAt());
        holder.description.setText(videoDetailsList.get(position).getSnippet().getDescription());
        holder.title.setText(videoDetailsList.get(position).getSnippet().getTitle());

       // Glide.with(context).load(videoDetailsList.get(position).getSnippet().getThumbnails().getMedium().getUrl()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount(){


        return videoDetailsList.size();
    }


    public class VideoDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView publishetAt,title,description,id;
        private ImageView thumbnail;



        public VideoDetailsViewHolder(View itemView){
            super(itemView);

//            thumbnail = itemView.findViewById(R.id.thumbnail);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            publishetAt = itemView.findViewById(R.id.publishedAt);
//            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
*/

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Model.Item;
import com.example.czyjatomelodia.MyViewHolder;
import com.example.czyjatomelodia.R;
import com.squareup.picasso.Picasso;

public class VideoDetailsAdapter extends RecyclerView.Adapter<MyViewHolder>{




    public VideoDetailsAdapter(Context context, Item[] items) {
        this.context = context;
        this.items = items;
    }
    Context context;
    Item[] items;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent,false);


        return new MyViewHolder(itemView);


    }

    @Override
    public void  onBindViewHolder(@NonNull MyViewHolder holder, int position){

        holder.uid.setText(items[position].getId().getVideoId());
       holder.textView.setText(items[position].getSnippet().getTitle());
      Picasso.get().load(items[position].getSnippet().getThumbnails().getMedium().getUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount(){

        return items.length;
    }






}


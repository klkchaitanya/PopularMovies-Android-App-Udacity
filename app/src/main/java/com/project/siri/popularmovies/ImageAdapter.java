package com.project.siri.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lk7rw on 5/11/18.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>
{
    int numItems;
    List<String> listImages;
    Context context;
    ListItemClickListener listItemClickListener;

    public interface ListItemClickListener
    {
        void onItemClick(int position);
    }

    public ImageAdapter(Context context,List<String> listImages,int numItems,ListItemClickListener listItemClickListener)
    {
        this.listImages = listImages;
        this.numItems = numItems;
        this.context = context;
        this.listItemClickListener = listItemClickListener;

    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        boolean shouldAttachtoParentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item,parent,shouldAttachtoParentImmediately);
        ImageViewHolder viewHolder = new ImageViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
            holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return numItems;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView movieImage;

        public ImageViewHolder(final View itemView) {
            super(itemView);
            movieImage = (ImageView)itemView.findViewById(R.id.imageView1);
            movieImage.setOnClickListener(this);

        }

        void bind(int index)
        {
            Picasso.with(context).load(listImages.get(index)).into (movieImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            listItemClickListener.onItemClick(position);
        }
    }

}

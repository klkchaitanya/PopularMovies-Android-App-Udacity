package com.project.siri.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends /*BaseAdapter*/ RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    Context context;
    ArrayList<Reviews> reviews;
    //final ArrayList<String> list_authors;
    //final ArrayList<String> list_content;
    //final ArrayList<String> list_url;
    LayoutInflater layoutInflater;

    public ReviewsAdapter(@NonNull Context context, ArrayList<Reviews> reviews) {
        //super(context, resource);
        this.context = context;
       this.reviews = reviews;

    }



    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean shouldAttachtoParentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_list_item,parent,shouldAttachtoParentImmediately);
        ReviewsViewHolder viewHolder = new ReviewsViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder
    {
        TextView author;
        TextView content;
        TextView url;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tvAuthor);
            content = (TextView) itemView.findViewById(R.id.tvContent);
            url = (TextView) itemView.findViewById(R.id.tvUrl);
        }

        public void bind(int index)
        {
            author.setText(reviews.get(index).getAuthor());
            content.setText(reviews.get(index).getContent());
            url.setText(reviews.get(index).getUrl());

            Log.d("RCU",reviews.get(index).getAuthor()+" "+reviews.get(index).getContent()+" "+reviews.get(index).getUrl());
        }
    }

   /* public int getCount() {
        return reviews.size();
    }


    public Object getItem(int position) {
        return reviews.get(position);
    }

*/

   /* @Override
    public long getItemId(int position) {
        return position;
    }*/


     /*public static class Holder
    {
        TextView author;
        TextView content;
        TextView url;

    }*/

   /* @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("getView", "pos "+position);
        Holder holder =null;
        View view =null;

        if(view==null) {
            holder = new Holder();
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.review_list_item, parent, false);
            holder.author = (TextView) view.findViewById(R.id.tvAuthor);
            holder.content = (TextView) view.findViewById(R.id.tvContent);
            holder.url = (TextView) view.findViewById(R.id.tvUrl);
        }
        else
        {
            holder = (Holder)convertView.getTag();
            view  = convertView;
        }

        holder.author.setText(reviews.get(position).getAuthor());
        holder.content.setText(reviews.get(position).getContent());
        holder.url.setText(reviews.get(position).getUrl());

        //Log.e("aucour",position+" "+list_authors.get(position));
        Log.e("last","pos"+position);

        return view;
    }*/



}

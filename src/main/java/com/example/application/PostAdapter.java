package com.example.application;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.CustomViewHolder>{

    private ArrayList<Post> arrayList;
    private Context context;

    public PostAdapter(ArrayList<Post> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        PostAdapter.CustomViewHolder holder = new PostAdapter.CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.CustomViewHolder holder, int position) {

        //Glide.with(context).load(arrayList.get(position).getImageUrl()).into(holder.iv_postuserimage);
        holder.tv_postnumber.setText(arrayList.get(position).getPostCount()+"");
        holder.tv_posttitle.setText(arrayList.get(position).getPosttitle());
        holder.tv_postuser.setText(arrayList.get(position).getPostuser());
        holder.tv_posttime.setText(arrayList.get(position).getPosttime());

    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //ImageView iv_postuserimage;
        TextView tv_postnumber;
        TextView tv_posttitle;
        TextView tv_postuser;
        TextView tv_posttime;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            //this.iv_postuserimage = itemView.findViewById(R.id.iv_postuserimage);
            this.tv_postnumber = itemView.findViewById(R.id.tv_postnumber);
            this.tv_posttitle = itemView.findViewById(R.id.tv_posttitle);
            this.tv_postuser = itemView.findViewById(R.id.tv_postuser);
            this.tv_posttime = itemView.findViewById(R.id.tv_posttime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = getAdapterPosition();
                    Post post = arrayList.get(currentPos);

                    Intent intent_postview = new Intent(context, PostViewActivity.class);
                    //intent_postview.putExtra("postuserimage", post.getImageUrl());
                    intent_postview.putExtra("postnumber", post.getPostCount()+"");
                    intent_postview.putExtra("postcontents", post.getPostcontents());
                    intent_postview.putExtra("posttitle", post.getPosttitle());
                    intent_postview.putExtra("postuser", post.getPostuser());
                    intent_postview.putExtra("posttime", post.getPosttime());
                    ((PostMainActivity)context).startActivity(intent_postview);
                }
            });

        }

    }

}

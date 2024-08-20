package com.example.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CustomViewHolder> {

    private ArrayList<Comment> arrayList;
    private Context context;

    public CommentAdapter(ArrayList<Comment> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public CommentAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CustomViewHolder holder, int position) {

        holder.tv_commentcontents.setText(arrayList.get(position).getCommentcontents());
        holder.tv_commentuser.setText(arrayList.get(position).getCommentuser());
        holder.tv_commenttime.setText(arrayList.get(position).getCommenttime());

    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_commentcontents;
        TextView tv_commentuser;
        TextView tv_commenttime;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_commentcontents = itemView.findViewById(R.id.tv_commentcontents);
            this.tv_commentuser = itemView.findViewById(R.id.tv_commentuser);
            this.tv_commenttime = itemView.findViewById(R.id.tv_commenttime);

        }
    }
}

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

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.CustomViewHolder> {

    private ArrayList<UserAccount> arrayList;
    private Context context;

    public LeaderboardAdapter(ArrayList<UserAccount> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public LeaderboardAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.CustomViewHolder holder, int position) {
        //프로필 사진
        Glide.with(context).load(arrayList.get(position).getImageUrl()).into(holder.iv_userProfile);
        //랭킹 (레벨 순)
        holder.tv_rank.setText(arrayList.get(position).getUserCount()
                                    - arrayList.get(position).getRankCount()
                                            + 1 + ""); //역순으로 출력됨 [ 랭킹 : 유저 수 - 내 순서 + 1 ]
        //아이디
        holder.tv_userid.setText("ID : " + arrayList.get(position).getEmailId());
        //닉네임
        holder.tv_usernickname.setText("NAME : " + arrayList.get(position).getNickname());
        //레벨
        holder.tv_userlevel.setText(String.valueOf("LEVEL : " + arrayList.get(position).getLevel()));
    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_userProfile;
        TextView tv_rank;
        TextView tv_userid;
        TextView tv_usernickname;
        TextView tv_userlevel;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_userProfile = itemView.findViewById(R.id.iv_userimage);
            this.tv_rank = itemView.findViewById(R.id.tv_rank);
            this.tv_userid = itemView.findViewById(R.id.tv_userid);
            this.tv_usernickname = itemView.findViewById(R.id.tv_usernickname);
            this.tv_userlevel = itemView.findViewById(R.id.tv_userlevel);

        }
    }
}

package com.example.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.service.autofill.UserData;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<ShopItem> arrayList;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;

    int mypoint;

    public CustomAdapter(ArrayList<ShopItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getItemImage())
                .into(holder.iv_itemimage);
        holder.tv_itemplace.setText("사용처 : " + arrayList.get(position).getItemPlace());
        holder.tv_itemname.setText("상품명 : " + arrayList.get(position).getItemName());
        holder.tv_itempoint.setText("포인트 : " + arrayList.get(position).getItemPoint());
    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_itemimage;
        private TextView tv_itemplace;
        private TextView tv_itemname;
        private TextView tv_itempoint;
        private Button btn_buy;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_itemimage = itemView.findViewById(R.id.iv_itemimage);
            this.tv_itemplace = itemView.findViewById(R.id.tv_itemplace);
            this.tv_itemname = itemView.findViewById(R.id.tv_itemname);
            this.tv_itempoint = itemView.findViewById(R.id.tv_itempoint);
            this.btn_buy = itemView.findViewById(R.id.btn_buy);

            mFirebaseAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
            String key = mFirebaseAuth.getUid();

            //여기부터 point 데이터 가져오기
            databaseReference = database.getReference("Application");
            databaseReference.child("UserAccount").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserAccount userAccount = snapshot.getValue(UserAccount.class);
                        mypoint = userAccount.getPoint();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            btn_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = getAdapterPosition();
                    ShopItem shopItem = arrayList.get(currentPos);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("구매");
                    builder.setIcon(R.drawable.ic_baseline_shopping_cart_24);
                    builder.setMessage("선택한 상품 : " + shopItem.getItemName() +
                            "\n\n" +
                            "정말 구매하시겠습니까 ?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mypoint >= shopItem.getItemPoint())
                            {
                                mypoint = mypoint - shopItem.getItemPoint();

                                //여기부터 데이터 업데이트 코드
                                HashMap data = new HashMap();
                                data.put("point",mypoint);
                                databaseReference = database.getReference("Application");
                                databaseReference.child("UserAccount").child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {

                                    }
                                });
                                //화면 새로고침
                                Intent intent = ((Activity)context).getIntent();
                                ((Activity)context).finish(); //현재 액티비티 종료 실시
                                ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
                                ((Activity)context).startActivity(intent); //현재 액티비티 재실행 실시
                                ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
                                Toast.makeText(context,"[" + shopItem.getItemName() + "] 구매하였습니다.",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context,"포인트가 부족합니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    builder.show();
                }
            });
        }

    }
}
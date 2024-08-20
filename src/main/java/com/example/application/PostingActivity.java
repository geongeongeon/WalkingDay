package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PostingActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;

    EditText title, contents;
    Button btn_post, btn_cancle;
    String postuser, postuserimage;
    int postcount;
    int updated_postcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        title = findViewById(R.id.et_title);
        contents = findViewById(R.id.et_contents);

        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Application");
        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        //여기부터 유저닉네임 가져오기
        databaseReference = database.getReference("Application");
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    postuser = userAccount.getNickname();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //여기부터 유저이미지 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    postuserimage = userAccount.getImageUrl();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get 포스트카운트
        databaseReference.child("PostCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    postcount = post.getPostCount();
                    updated_postcount = postcount+1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //현재 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String getTime = dateFormat.format(date);
        String strTitle = title.getText().toString();
        String strContents = contents.getText().toString();

        btn_post = findViewById(R.id.btn_post);
        btn_post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Post post = new Post();
                post.setPostuser(postuser);
                post.setPosttitle(title.getText().toString());
                post.setPostcontents(contents.getText().toString());
                post.setPosttime(getTime);
                post.setPostCount(updated_postcount);
                post.setPostCommentCount(0);
                post.setImageUrl(postuserimage);

                //setValue : 데이터베이스에 넣기
                databaseReference.child("Post").child(updated_postcount+"").setValue(post);

                //update 유저카운트
                HashMap data = new HashMap();
                data.put("postCount",updated_postcount);

                databaseReference.child("PostCount").updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                    }
                });

                //PostMainActivity로 이동
                Intent intent_postmain = new Intent(PostingActivity.this, PostMainActivity.class);
                startActivity(intent_postmain);
                finish(); //현재 액티비티 파괴
            }
        });

        btn_cancle = findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PostMainActivity로 이동
                Intent intent_postmain = new Intent(PostingActivity.this, PostMainActivity.class);
                startActivity(intent_postmain);
                finish(); //현재 액티비티 파괴
            }
        });
    }
}
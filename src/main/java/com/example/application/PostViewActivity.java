package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PostViewActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;

    String postcontents, posttitle, postuser, posttime, postnumber, postuserimage;
    String commentuser;
    TextView read_posttitle, read_postuser, read_posttime, read_postcontents;
    EditText commentcontents;
    Button btn_comment;

    ImageView iv_postuserimage;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Comment> arrayList;

    int commentcount, updated_commentcount;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Intent intent_postview = getIntent();
        postnumber = intent_postview.getExtras().getString("postnumber");
        postcontents = intent_postview.getExtras().getString("postcontents");
        posttitle = intent_postview.getExtras().getString("posttitle");
        postuser = intent_postview.getExtras().getString("postuser");
        posttime = intent_postview.getExtras().getString("posttime");
        //postuserimage = intent_postview.getExtras().getString("postuserimage");

        read_posttitle = findViewById(R.id.read_posttile);
        read_postuser = findViewById(R.id.read_postuser);
        read_posttime = findViewById(R.id.read_posttime);
        read_postcontents = findViewById(R.id.read_postcontents);

        read_posttitle.setText(posttitle);
        read_postuser.setText(postuser);
        read_posttime.setText(posttime);
        read_postcontents.setText(postcontents);

        commentcontents = findViewById(R.id.et_comment);
        btn_comment = findViewById(R.id.btn_comment);

        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Application");
        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        //여기부터 유저닉네임 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    commentuser = userAccount.getNickname();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //여기부터 commentCount 가져오기
        databaseReference.child("Post").child(postnumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    commentcount = post.getPostCommentCount();
                    updated_commentcount = commentcount+1;
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
        String strContents = commentcontents.getText().toString();

        //버튼 누르면 파이어베이스에 데이터 저장
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment();
                comment.setCommentuser(commentuser);
                comment.setCommentcontents(commentcontents.getText().toString());
                comment.setCommenttime(getTime);
                comment.setCommentcount(updated_commentcount);

                //setValue : 데이터베이스에 넣기
                databaseReference.child("Post").child(postnumber).child("Comment").child(updated_commentcount+"").setValue(comment);

                //update commentcount
                HashMap data = new HashMap();
                data.put("postCommentCount",updated_commentcount);

                databaseReference.child("Post").child(postnumber).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                    }
                });
                //새로고침
                finish();//인텐트 종료
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
        });

        //리사이클러뷰
        recyclerView = findViewById(R.id.rcv_comment); //아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); //객체를 담을 어레이 리스트

        //댓글 데이터 가져와서 arrayList에 담기
        databaseReference.child("Post").child(postnumber).child("Comment").orderByChild("commentcount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //반복문으로 데이터 리스트 추출해냄
                    Comment comment = snapshot.getValue(Comment.class); //ShopItem 객체에 데이터를 담는다.
                    arrayList.add(comment); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //db를 가져오던 중 에러 발생 시
                Log.e("PostView" +
                        "Activity", String.valueOf(error.toException())); //에러문 출력
            }
        });

        //리사이클러뷰 CommentAdapter 연동
        adapter = new CommentAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        //툴바
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        mFirebaseAuth = FirebaseAuth.getInstance();

        //네비게이션뷰의 아이템을 클릭했을때
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_walk:
                        Intent intent_walk = new Intent(PostViewActivity.this,MainActivity.class);
                        startActivity(intent_walk);
                        break;
                    case R.id.menu_map:
                        Intent intent_map = new Intent(PostViewActivity.this,MapReadyActivity.class);
                        startActivity(intent_map);
                        break;
                    case R.id.menu_shop:
                        Intent intent_shop = new Intent(PostViewActivity.this,ShopActivity.class);
                        startActivity(intent_shop);
                        break;
                    case R.id.menu_leaderboard:
                        Intent intent_leaderboard = new Intent(PostViewActivity.this,LeaderboardActivity.class);
                        startActivity(intent_leaderboard);
                        break;
                    case R.id.menu_post:
                        Intent intent_post = new Intent(PostViewActivity.this,PostMainActivity.class);
                        startActivity(intent_post);
                        break;
                    case R.id.menu_profile:
                        Intent intent_profile = new Intent(PostViewActivity.this,ProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.menu_logout:
                        Toast.makeText(PostViewActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        Intent intent_logout = new Intent(PostViewActivity.this,LoginActivity.class);
                        startActivity(intent_logout);
                        finish();
                        break;
                }

                //Drawer를 닫기...
                drawerLayout.closeDrawer(navigationView);

                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
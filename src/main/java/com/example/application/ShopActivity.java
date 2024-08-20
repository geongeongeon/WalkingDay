package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ShopItem> arrayList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    int point=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //툴바
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        mFirebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView); //아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //ShopItem 객체를 담을 어레이 리스트

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        //여기부터 point 데이터 가져오기
        databaseReference = database.getReference("Application");
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    point = userAccount.getPoint();
                    TextView tv_mypoint = (TextView)findViewById(R.id.tv_mypoint);
                    tv_mypoint.setText("My Point : " + point);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = database.getReference("Item"); //DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //반복문으로 데이터 리스트 추출해냄
                    ShopItem shopItem = snapshot.getValue(ShopItem.class); //ShopItem 객체에 데이터를 담는다.
                    arrayList.add(shopItem); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //db를 가져오던 중 에러 발생 시
                Log.e("ShopActivity", String.valueOf(error.toException())); //에러문 출력
            }
        });
        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        //네비게이션뷰의 아이템을 클릭했을때
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_walk:
                        Intent intent_walk = new Intent(ShopActivity.this,MainActivity.class);
                        startActivity(intent_walk);
                        break;
                    case R.id.menu_map:
                        Intent intent_map = new Intent(ShopActivity.this,MapReadyActivity.class);
                        startActivity(intent_map);
                        break;
                    case R.id.menu_shop:
                        drawerLayout.closeDrawer(navigationView);
                        break;
                    case R.id.menu_leaderboard:
                        Intent intent_leaderboard = new Intent(ShopActivity.this,LeaderboardActivity.class);
                        startActivity(intent_leaderboard);
                        break;
                    case R.id.menu_post:
                        Intent intent_post = new Intent(ShopActivity.this,PostMainActivity.class);
                        startActivity(intent_post);
                        break;
                    case R.id.menu_profile:
                        Intent intent_profile = new Intent(ShopActivity.this,ProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.menu_logout:
                        Toast.makeText(ShopActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        Intent intent_logout = new Intent(ShopActivity.this,LoginActivity.class);
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
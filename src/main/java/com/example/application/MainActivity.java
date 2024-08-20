package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증

    Intent pedometerService;
    BroadcastReceiver receiver;
    boolean flag = true;
    String serviceData;
    Button startBtn;
    TextView tv;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //달리는 사람 애니메이션 넣기
        ImageView iv_runner = (ImageView) findViewById(R.id.runner);
        Animation anim_running = AnimationUtils.loadAnimation(this, R.anim.running);
        iv_runner.startAnimation(anim_running);
        AnimationDrawable animation_drawable = new AnimationDrawable();
        BitmapDrawable frame1 = (BitmapDrawable)getResources().getDrawable(R.drawable.run1);
        BitmapDrawable frame2 = (BitmapDrawable)getResources().getDrawable(R.drawable.run2);
        BitmapDrawable frame3 = (BitmapDrawable)getResources().getDrawable(R.drawable.run3);
        BitmapDrawable frame4 = (BitmapDrawable)getResources().getDrawable(R.drawable.run4);
        BitmapDrawable frame5 = (BitmapDrawable)getResources().getDrawable(R.drawable.run5);
        BitmapDrawable frame6 = (BitmapDrawable)getResources().getDrawable(R.drawable.run6);
        BitmapDrawable frame7 = (BitmapDrawable)getResources().getDrawable(R.drawable.run7);
        BitmapDrawable frame8 = (BitmapDrawable)getResources().getDrawable(R.drawable.run8);
        animation_drawable.addFrame(frame1, 200);
        animation_drawable.addFrame(frame2, 200);
        animation_drawable.addFrame(frame3, 200);
        animation_drawable.addFrame(frame4, 200);
        animation_drawable.addFrame(frame5, 200);
        animation_drawable.addFrame(frame6, 200);
        animation_drawable.addFrame(frame7, 200);
        animation_drawable.addFrame(frame8, 200);
        iv_runner.setBackgroundDrawable(animation_drawable);
        animation_drawable.start();

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //여기부터 닉네임 가져오기
        TextView tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        myRef = database.getReference("Application");
        //읽기
        myRef.child("UserAccount").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount userAccount = snapshot.getValue(UserAccount.class);
                tv_nickname.setText(userAccount.getNickname());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tv_nickname.setText("error: " + error.toException());
            }
        });

        //여기부터 레벨 가져오기
        TextView tv_level = (TextView)findViewById(R.id.tv_level);
        myRef = database.getReference("Application");
        //읽기
        myRef.child("UserAccount").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount userAccount = snapshot.getValue(UserAccount.class);
                tv_level.setText(userAccount.getLevel()+"레벨");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tv_level.setText("error: " + error.toException());
            }
        });

        //툴바
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        mFirebaseAuth = FirebaseAuth.getInstance();

        pedometerService = new Intent(this, StepCountService.class);
        receiver = new StartReceiver(); // 방송 수신자

        startBtn = (Button)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag) { // true 상태면
                    try {
                        // false로 토글되면서
                        startBtn.setText("멈추기");
                        IntentFilter intentFilter = new IntentFilter("com.example.application");

                        registerReceiver(receiver, intentFilter); // receiver 객체와 IntentFilter 객체를 파라미터로 해서 방송 수신자를 등록함
                        startService(pedometerService);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else { // false 상태면
                    // true로 토글되면서
                    startBtn.setText("시작");
                    try {
                        unregisterReceiver(receiver); // 방송 수신자 해제
                        stopService(pedometerService);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                flag = !flag; // 버튼을 누를 때마다 상태 토글
            }
        });

        tv = (TextView) findViewById(R.id.tv);
        Button resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetConfirm();
            }
        });

        //네비게이션뷰의 아이템을 클릭했을때
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_walk:
                        drawerLayout.closeDrawer(navigationView);
                        break;
                    case R.id.menu_map:
                        Intent intent_map = new Intent(MainActivity.this,MapReadyActivity.class);
                        startActivity(intent_map);
                        break;
                    case R.id.menu_shop:
                        Intent intent_shop = new Intent(MainActivity.this,ShopActivity.class);
                        startActivity(intent_shop);
                        break;
                    case R.id.menu_leaderboard:
                        Intent intent_leaderboard = new Intent(MainActivity.this,LeaderboardActivity.class);
                        startActivity(intent_leaderboard);
                        break;
                    case R.id.menu_post:
                        Intent intent_post = new Intent(MainActivity.this,PostMainActivity.class);
                        startActivity(intent_post);
                        break;
                    case R.id.menu_profile:
                        Intent intent_profile = new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.menu_logout:
                        Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        Intent intent_logout = new Intent(MainActivity.this,LoginActivity.class);
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

    class StartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 서비스 클래스에서 Intent를 받아와서 TextView에 출력
            serviceData = intent.getStringExtra("stepCountService");
            tv.setText(serviceData);
        }
    }

    //초기화
    public void resetConfirm() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("걸음수 초기화");
        builder.setMessage("걸음수를 정말 초기화하시겠습니까 ?");
        builder.setIcon(R.drawable.warning);
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag) { // true 상태면
                            // 초기화
                            StepCount.Step = 0;
                            tv.setText(0+"");
                        } else { // fasle 상태면
                            Toast.makeText(getApplicationContext(), "먼저 만보기를 멈춰주세요. ", Toast.LENGTH_SHORT).show();
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



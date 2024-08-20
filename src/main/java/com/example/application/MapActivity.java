package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private TextView txtDistance;
    private GoogleMap mMap;
    private double x, y;
    int count = 0;
    int deleteMarker = 0;
    int upt = 0;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    private FirebaseAuth mFirebaseAuth;
    int point;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //툴바
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        mFirebaseAuth = FirebaseAuth.getInstance();
        Button btn_t = (Button) findViewById(R.id.btn_t);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btn_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_location = getIntent();
                double latitude = intent_location.getDoubleExtra("latitude", 0);
                double longitude = intent_location.getDoubleExtra("longitude", 0);
                LatLng place = new LatLng(latitude,longitude);
                getRandomLocation(place, 300); //랜덤위치 범위 설정 (radius:반지름)
                if(count<1) {
                    upt=1;
                    count++;
                    LatLng RANDOM = new LatLng(x, y);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(RANDOM);
                    markerOptions.title("lat:"+RANDOM.latitude+"long:"+RANDOM.longitude);
                    markerOptions.getIcon();
                    BitmapDrawable bitmapdDawable = (BitmapDrawable)getResources().getDrawable(R.drawable.giftbox);
                    Bitmap bitmap = bitmapdDawable.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap,99,99,false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(RANDOM, 17));
                    if ( Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions( MapActivity.this, new String[] {
                                android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                    }
                    else{
                        // 위치정보를 원하는 시간, 거리마다 갱신해준다.
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                1500, //시간마다 (1000=1초)
                                1, //거리마다 (1=1m)
                                gpsLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                1500,
                                1,
                                gpsLocationListener);
                    }
                    txtDistance = (TextView)findViewById(R.id.txt_distance);
                    txtDistance.setText("남은 거리가 표시됩니다.");

                }
                else {
                    Toast.makeText(MapActivity.this, "지도에 표시된 장소로 이동해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //네비게이션뷰의 아이템을 클릭했을때
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_walk:
                        Intent intent_walk = new Intent(MapActivity.this,MainActivity.class);
                        startActivity(intent_walk);
                        break;
                    case R.id.menu_map:
                        drawerLayout.closeDrawer(navigationView);
                        break;
                    case R.id.menu_shop:
                        Intent intent_shop = new Intent(MapActivity.this,ShopActivity.class);
                        startActivity(intent_shop);
                        break;
                    case R.id.menu_leaderboard:
                        Intent intent_leaderboard = new Intent(MapActivity.this,LeaderboardActivity.class);
                        startActivity(intent_leaderboard);
                        break;
                    case R.id.menu_post:
                        Intent intent_post = new Intent(MapActivity.this,PostMainActivity.class);
                        startActivity(intent_post);
                        break;
                    case R.id.menu_profile:
                        Intent intent_profile = new Intent(MapActivity.this,ProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.menu_logout:
                        Toast.makeText(MapActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        Intent intent_logout = new Intent(MapActivity.this,LoginActivity.class);
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
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        Intent intent_location = getIntent();
        double latitude = intent_location.getDoubleExtra("latitude", 0);
        double longitude = intent_location.getDoubleExtra("longitude", 0);

        LatLng place = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 10));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }

        //여기부터 point 데이터 가져오기
        myRef = database.getReference("Application");
        myRef.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    point = userAccount.getPoint();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public LatLng getRandomLocation(LatLng c, int radius) {
        double d2r = Math.PI / 180;
        double r2d = 180 / Math.PI;
        double earth_rad = 6378000f; //지구 반지름 근사값

        double r = new Random().nextInt(radius) + new Random().nextDouble();
        double rlat = (r / earth_rad) * r2d;
        double rlng = rlat / Math.cos(c.latitude * d2r);

        double theta = Math.PI * (new Random().nextInt(2) + new Random().nextDouble());
        y = c.longitude + (rlng * Math.cos(theta));
        x = c.latitude + (rlat * Math.sin(theta));
        return new LatLng(x, y);
    }

    //권한요청
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermissionWithRationale() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("위치정보")
                        .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    //안드로이드 - 두지점(위도,경도) 사이의 거리
    public double DistanceByDegreeAndroid(double _latitude1, double _longitude1, double _latitude2, double _longitude2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        double distance = startPos.distanceTo(endPos);

        return distance;
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

                //여기부터 위치 리스너
                double latitude = location.getLatitude(); // 경도
                double longitude = location.getLongitude(); // 위도
                LatLng RANDOM = new LatLng(x, y);
                int dist = (int) DistanceByDegreeAndroid(latitude, longitude, RANDOM.latitude, RANDOM.longitude);
                if (dist <= 10) //m 단위
                {
                    if(upt==1) {
                        Random random_point = new Random();
                        int num = random_point.nextInt(3) + 1; //포인트를 랜덤으로 줌
                        point = point + num;
                        txtDistance.setText("주변에 도착했습니다!");
                        Toast.makeText(MapActivity.this, num + "포인트가 지급되었습니다.", Toast.LENGTH_SHORT).show();
                        count = 0;
                        deleteMarker = 1;
                        upt = 0;
                    }
                    //여기부터 데이터 업데이트 코드
                    String key = mFirebaseAuth.getUid();
                    HashMap data = new HashMap();
                    data.put("point", point);
                    myRef = database.getReference("Application");
                    myRef.child("UserAccount").child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {

                        }
                    });

                    if (deleteMarker == 1) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear();
                                deleteMarker = 0;
                            }
                        },1000); //1초 후에 마커 삭제
                        //새로고침
                        finish();//인텐트 종료
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                        Intent intent = getIntent(); //인텐트
                        startActivity(intent); //액티비티 열기
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                    }

                } else {
                    txtDistance.setText(dist + "m");
                }

    }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        public void onProviderEnabled(String provider) {

        }
        public void onProviderDisabled(String provider) {

        }

    };
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
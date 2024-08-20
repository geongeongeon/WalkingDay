package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.application.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private FirebaseAuth auth; //파이어베이스 인증
    ActivityProfileBinding binding;
    String nickname, password;
    DatabaseReference databaseReference;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseUser user;

    ImageView profileImage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileImage = findViewById(R.id.iv_profileImage);

        //여기부터 프로필 이미지
        StorageReference profileRef = storageReference.child("userProfiles/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //데이터 가져오기
        databaseReference = database.getReference("Application");

        databaseReference.child("UserAccount").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserAccount userData = snapshot.getValue(UserAccount.class);
                    binding.emailId.setText(userData.getEmailId());
                    binding.nickname.setText(userData.getNickname());
                    binding.password.setText(userData.getPassword());
                    binding.level.setText(userData.getLevel()+""); //setText(string)형식으로 써야함
                    binding.point.setText(userData.getPoint()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.eUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.eUpdate.setText("완료");
            }
        });
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.button3.setText("완료");
            }
        });

        //저장버튼 클릭시
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.eUpdate.setText("변경");
                binding.button3.setText("변경");
                nickname = binding.nickname.getText().toString().trim();
                password = binding.password.getText().toString().trim();
                String key = auth.getUid();
                //아래부터 데이터 업데이트 코드
                HashMap data = new HashMap();
                data.put("nickname",nickname);
                data.put("password",password);
                databaseReference = database.getReference("Application");
                databaseReference.child("UserAccount").child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(ProfileActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent_main = new Intent(ProfileActivity.this,MainActivity.class);
                        startActivity(intent_main);
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }
    private void uploadImageToFirebase(Uri uri) {
        //파이어베이스 스토리지에 이미지 업로드
        String key = auth.getUid();
        final StorageReference fileRef = storageReference.child("userProfiles/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String profileUrl = uri.toString();
                        //프로필 이미지 업데이트
                        HashMap data = new HashMap();
                        data.put("imageUrl",profileUrl);
                        //databaseReference.child("UserAccount").child(key).setValue(model);
                        databaseReference.child("UserAccount").child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                            }
                        });
                        Picasso.get().load(uri).into(profileImage);
                        Toast.makeText(getApplicationContext(), "성공하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
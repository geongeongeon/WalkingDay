package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private TextInputLayout mEtEmail, mEtPwd, mEtNickname; //회원가입 입력필드
    private Button mBtnRegister; //회원가입 버튼
    private int count; //유저수 담을 변수
    private int ucount;

    @Override
    protected void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Application");

        //get 유저카운트
        mDatabaseRef.child("UserCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserAccount userCount = snapshot.getValue(UserAccount.class);
                    count = userCount.getUserCount();
                    ucount = count+1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtNickname = findViewById(R.id.et_nickname);

        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리 시작
                String strEmail = mEtEmail.getEditText().getText().toString();
                String strPwd = mEtPwd.getEditText().getText().toString();
                String strNickname =  mEtNickname.getEditText().getText().toString();

                //Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                            //[리얼타임 데이타베이스]
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);
                            account.setNickname(strNickname);
                            account.setLevel(1);
                            account.setPoint(0);
                            account.setImageUrl("https://firebasestorage.googleapis.com/v0/b/application-8c782.appspot.com/o/no_profile_pic.png?alt=media&token=c77b5276-9a1f-4d60-b6a3-7b6d4673a63f");

                            //setValue : 데이터베이스에 넣기
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                            //회원가입 성공
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                            //update 유저카운트
                            HashMap data = new HashMap();
                            data.put("userCount",ucount);

                            mDatabaseRef.child("UserCount").updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {

                                }
                            });

                            //LoginActivity로 이동
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); //현재 액티비티 파괴

                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}
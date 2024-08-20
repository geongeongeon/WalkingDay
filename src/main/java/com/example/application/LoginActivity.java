package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private TextInputLayout mEtEmail, mEtPwd; //회원가입 입력필드
    private FirebaseUser currentUser; //현재 로그인 된 유저 정보를 담을 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Application");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);

        //로그인
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //로그인 요청
                String strEmail = mEtEmail.getEditText().getText().toString();
                String strPwd = mEtPwd.getEditText().getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        //로그인 성공
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            //MainActivity로 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); //현재 액티비티 파괴
                        }
                        //로그인 실패
                        else {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        //회원가입
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //RegisterActivity로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    //로그아웃 안했으면, 즉 로그인 되어있으면 자동으로 메인페이지로 이동시키기 [자동로그인]
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
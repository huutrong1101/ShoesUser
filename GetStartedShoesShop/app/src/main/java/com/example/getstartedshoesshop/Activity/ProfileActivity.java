package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.SharedPrefManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView avatar;

    TextView btnLogOut,name,email,password,tvCart,tvEdit, nameAvatar;

    ImageView imvBack;

    boolean isDoubleClicked = false;

    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        anhXa();

        getData();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                //Actions when Single Clicked
                isDoubleClicked = false;
            }
        };

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDoubleClicked){
                    startActivity(new Intent(ProfileActivity.this, UploadImageActivity.class));
                    isDoubleClicked=false;
                    handler.removeCallbacks(r);
                }else{
                    isDoubleClicked=true;
                    handler.postDelayed(r,500);
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        tvCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, CartActivity.class));
            }
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getData(){
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {

            if (user.getImage() != null){
                Glide.with(getApplicationContext()).load(user.getImage()).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.avatar_default);
            }
            name.setText(user.getUsername());
            email.setText(user.getEmail());
            password.setText(user.getPassword());
            nameAvatar.setText(user.getUsername());
        }
    }

    private void anhXa(){
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogOut = findViewById(R.id.btnLogOut);
        imvBack = findViewById(R.id.imvBack);
        tvCart = findViewById(R.id.tvCart);
        tvEdit = findViewById(R.id.tvEdit);
        nameAvatar = findViewById(R.id.nameAvatar);
    }
}
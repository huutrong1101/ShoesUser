package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Request.LoginRequest;
import com.example.getstartedshoesshop.Request.RegisterRequest;
import com.example.getstartedshoesshop.Response.LoginResponse;
import com.example.getstartedshoesshop.Response.RegisterResponse;
import com.example.getstartedshoesshop.Response.UserResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import org.mindrot.jbcrypt.BCrypt;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextUsername,editTextEmail,editTextNewPassword,editTextOldPassword;

    private APIService apiService;

    private String username, email, password,oldPassword;

    private Button buttonSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiService = RetrofitClient.getClient().create(APIService.class);

        anhXa();

        getData();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
    }

    private void editProfile(){
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        username = editTextUsername.getText().toString();
        email = editTextEmail.getText().toString();
        password = editTextNewPassword.getText().toString();
        oldPassword = editTextOldPassword.getText().toString();

        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        } else {
            RegisterRequest request = new RegisterRequest(username, email,password,"user");
            Call<UserResponse> call = apiService.updateProfile(user.getId(),request);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Cập nhật tài khoản thành công. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                            SharedPrefManager.getInstance(getApplicationContext()).logout();
                            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Cập nhật tài khoản thất bại 1", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Cập nhật tài khoản thất bại 2", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getData(){
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        editTextUsername.setText(user.getUsername());
        editTextEmail.setText(user.getEmail());
    }


    private void anhXa(){
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextOldPassword= findViewById(R.id.editTextOldPassword);
        buttonSave = findViewById(R.id.buttonSave);
    }
}
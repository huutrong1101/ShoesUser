package com.example.getstartedshoesshop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Request.RegisterRequest;
import com.example.getstartedshoesshop.Response.RegisterResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private APIService apiService;

    EditText username_edit_text, email_edit_text, password_edit_text, repassword_edit_text;

    AppCompatButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        apiService = RetrofitClient.getClient().create(APIService.class);

        username_edit_text = findViewById(R.id.username_edit_text);
        email_edit_text = findViewById(R.id.email_edit_text);
        password_edit_text = findViewById(R.id.password_edit_text);
        repassword_edit_text = findViewById(R.id.repassword_edit_text);
        btnRegister = findViewById(R.id.register_button);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String username = username_edit_text.getText().toString();
        final String email = email_edit_text.getText().toString();
        final String password = password_edit_text.getText().toString();
        final String rePassword = repassword_edit_text.getText().toString();

        RegisterRequest request = new RegisterRequest(username, email, password, "user");
        Call<RegisterResponse> call = apiService.register(request);

        if (TextUtils.isEmpty(username)) {
            username_edit_text.setError("Vui lòng nhập tên");
            username_edit_text.requestFocus();
            return;
        } else if (TextUtils.isEmpty(email)) {
            email_edit_text.setError("Vui lòng nhập email");
            email_edit_text.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password)) {
            password_edit_text.setError("Vui lòng nhập mật khẩu");
            password_edit_text.requestFocus();
            return;
        } else if (TextUtils.isEmpty(rePassword)) {
            repassword_edit_text.setError("Vui lòng nhập lại mật khẩu");
            repassword_edit_text.requestFocus();
            return;
        } else if (!password.equals(rePassword)) {
            Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
        } else {
            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);

//                             //Tạo đối tượng User mới và gán thông tin
//                                User newUser = response.body().getUser();
//
//                                // Gán đường dẫn hình ảnh avatar mặc định
//                                newUser.setImage("avatar_default.jpg");
//
//                                // Cập nhật thông tin người dùng trong Shared Preferences
//                                SharedPrefManager.getInstance(SignUpActivity.this).userLogin(newUser);
//
//                                // Hiển thị thông báo thành công hoặc thực hiện các xử lý khác
//                                Toast.makeText(SignUpActivity.this, "Tải lên avatar mặc định thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    try {
//                        if (response.isSuccessful()) {
//                            RegisterResponse registerResponse = response.body();
//                            if (registerResponse != null && registerResponse.isSuccess()) {
//                                Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                                startActivity(intent);
//
//                                // Tạo đối tượng User mới và gán thông tin
//                                User newUser = registerResponse.getUser();
//
//                                // Gán đường dẫn hình ảnh avatar mặc định
//                                newUser.setImage("avatar_default.jpg");
//
//                                // Cập nhật thông tin người dùng trong Shared Preferences
//                                SharedPrefManager.getInstance(SignUpActivity.this).userLogin(newUser);
//
//                                // Hiển thị thông báo thành công hoặc thực hiện các xử lý khác
//                                Toast.makeText(SignUpActivity.this, "Tải lên avatar mặc định thành công", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thất bại", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thất bại", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Lỗi:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
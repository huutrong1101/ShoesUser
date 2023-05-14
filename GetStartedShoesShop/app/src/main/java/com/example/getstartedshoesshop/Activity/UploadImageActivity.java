package com.example.getstartedshoesshop.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.RealPathUtil;
import com.example.getstartedshoesshop.Response.UserResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageActivity extends AppCompatActivity {

    private APIService apiService;
    Button btnChoose, btnUpload;
    ImageView imageViewChoose;
    EditText editTextUserName;

    ImageView imvBack;
    TextView title;
    private Uri mUri;
    private User user;
    private ProgressDialog mProgessDialog;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = UploadImageActivity.class.getName();

    // khai báo một mảng kiểu String chứa các quyền truy cập bộ nhớ trong của ứng dụng trong Android
    public static String[] storge_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // quyền ghi vào bộ nhớ ngoài
            Manifest.permission.READ_EXTERNAL_STORAGE // quyền đọc từ bộ nhớ ngoài
    };


    // yêu cầu phiên bản API của Android tối thiểu là Tiramisu (API level 33).
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)

    //một mảng chuỗi storge_permissions_33 chứa các quyền truy cập vào bộ nhớ trong
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES, // đọc hình ảnh
            Manifest.permission.READ_MEDIA_AUDIO, // đọc âm thanh
            Manifest.permission.READ_MEDIA_VIDEO // đọc video
    };

    // Phương thức này được sử dụng để trả về một mảng các quyền được yêu cầu để truy cập bộ nhớ trong thiết bị.
    public static String[] permissions() {
        String[] p;
        // Kiểm tra phiên bản SDK của thiết bị
        // nếu SDK lớn hơn hoặc bằng phiên bản TIRAMISU (API level 33), phương thức sẽ trả về mảng các quyền mới nhất được hỗ trợ,
        // được lưu trữ trong biến "storge_permissions_33"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        }
        // Phương thức sẽ trả về mảng các quyền cũ hơn, được lưu trữ trong biến "storge_permissions".
        else {
            p = storge_permissions;
        }
        return p;
    }


    // kiểm tra phiên bản
    private void CheckPermissions() {
        // Nếu phiên bản SDK của thiết bị là dưới M (Marshmallow), nó sẽ gọi hàm openGallery
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
        }
        // nếu phiên bản SDK là M hoặc cao hơn, nó sẽ yêu cầu quyền truy cập thư mục lưu trữ (Storage)
        // bằng cách gọi phương thức requestPermissions()
        else {
            requestPermissions(permissions(), MY_REQUEST_CODE);
        }
    }


    // kiểm tra người dùng đã cấp quyền cho ứng dụng để truy cập vào bộ nhớ của thiết bị hay chưa
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            // kiểm tra cấp quyền => nếu có thì thực hiện hàm openGallery();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }


    // Mở ứng dụng Gallery trên thiết bị, cho phép người dùng chọn một hình ảnh từ bộ sưu tập của mình.
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLaucher.launch(Intent.createChooser(intent, "Select Picture"));
    }


    // Lấy một hình ảnh từ bộ nhớ hoặc từ các ứng dụng khác.
    private ActivityResultLauncher<Intent> mActivityResultLaucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imageViewChoose.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        AnhXa();

        // Lấy đối tượng User từ SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        apiService = RetrofitClient.getClient().create(APIService.class);

        // Sử dụng đối tượng User
        if (SharedPrefManager.getInstance(this).isLoggedIn()){
            if (user.getImage() != null){
                Glide.with(getApplicationContext()).load(user.getImage()).into(imageViewChoose);
            } else {
                imageViewChoose.setImageResource(R.drawable.avatar_default);
            }
        }

        mProgessDialog = new ProgressDialog(UploadImageActivity.this);
        mProgessDialog.setMessage("Please wait upload ...");

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermissions();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUri !=null){
                    UploadImage1();
                }
            }
        });

        title.setText("Cập nhật hình ảnh");

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void UploadImage1() {
        // hiển thị quá trình tải
        mProgessDialog.show();

        // lấy thông tin người dùng được lưu trữ trong SharedPreferences
        User user1 = SharedPrefManager.getInstance(this).getUser();

        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), user1.getId());

        // MultipartBody.Part để lưu trữ thông tin hình ảnh được chọn
        String IMAGE_PATH = RealPathUtil.getRealPath(this, mUri);
        File file = new File(IMAGE_PATH);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part partbodyImages = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


        // gọi phương thức upload APIService để tạo và gửi yêu cầu lên api
        Call<UserResponse> call = apiService.updateUser(id, partbodyImages);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    mProgessDialog.dismiss();
                    Toast.makeText(UploadImageActivity.this, "Thành công", Toast.LENGTH_SHORT).show();

                    SharedPrefManager.getInstance(getApplicationContext()).logout();
                    startActivity(new Intent(UploadImageActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(UploadImageActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                mProgessDialog.dismiss();
                Toast.makeText(UploadImageActivity.this, "Gọi API Thất bại", Toast.LENGTH_LONG).show();
            }
        });
    }

    // ánh xạ các đối tượng có trong trang
    private void AnhXa() {
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imvBack = findViewById(R.id.imvBack);
        title = findViewById(R.id.title);
        imageViewChoose = findViewById(R.id.imgChoose);
    }
}

package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Adapter.BannerAdapter;
import com.example.getstartedshoesshop.Adapter.CategoryAdapter;
import com.example.getstartedshoesshop.Adapter.ProductAdapter;
import com.example.getstartedshoesshop.Model.Cart;
import com.example.getstartedshoesshop.Model.Category;
import com.example.getstartedshoesshop.Response.CategoryResponse;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.Response.ListProductResponse;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.SharedPrefManager;
import com.example.getstartedshoesshop.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerView2;
    private ProductAdapter adapter;

    private CategoryAdapter categoryAdapter;
    private APIService apiService;

    private TextView userName;
    private ImageView avatar;

    private List<Product> productList;

    private List<Category> categoryList;

    Handler handler=new Handler();

    private EditText etUserName;

    private ViewPager2 viewPager;
    private BannerAdapter bannerAdapter;
    private List<Drawable> images;

    private ViewFlipper viewFlipperBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getClient().create(APIService.class);

        etUserName = findViewById(R.id.etUserName);

        // Viewflipper
        viewFlipperBanner = findViewById(R.id.viewFlipperBanner);
        ActionViewFlipperMain();

        // Lấy đối tượng User từ SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // Sử dụng đối tượng User

        if (SharedPrefManager.getInstance(this).isLoggedIn()){
            userName = findViewById(R.id.userName);
            avatar = findViewById(R.id.avatar);

            userName.setText("Hi "+ user.getUsername());

            if (user.getImage() != null){
                Glide.with(getApplicationContext()).load(user.getImage()).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.avatar_default);
            }
        }

        recyclerViewPopular();

        recyclerViewCategory();

        bottomNavigation();

        ActionViewFlipperMain();


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        etUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
//        viewPager = findViewById(R.id.viewPager);
//
//        // Khởi tạo dữ liệu cho Adapter (có thể là Drawable hoặc URL của hình ảnh)
//        images = new ArrayList<>();
//        images.add(getResources().getDrawable(R.drawable.adidas_questar_shoes));
//        images.add(getResources().getDrawable(R.drawable.big_shoe));
//        images.add(getResources().getDrawable(R.drawable.small_shoe4));
//        // Khởi tạo và thiết lập Adapter cho ViewPager
//        bannerAdapter = new BannerAdapter(images);
//        viewPager.setAdapter(bannerAdapter);

    }

        private void recyclerViewPopular() {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView = findViewById(R.id.recyclerView2);
            recyclerView.setLayoutManager(linearLayoutManager);

            //Get API -> sử dụng Retrofit để lấy dữ liệu từ api
            Call<ListProductResponse> call = apiService.getAllProduct();
            call.enqueue(new Callback<ListProductResponse>() {
                @Override
                public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                    if (response.isSuccessful()) {
                        productList = response.body().getData();

                        SharedPrefManager.getInstance(getApplicationContext()).saveData(productList);

                        adapter = new ProductAdapter(productList,MainActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ListProductResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lấy dữ liệu thất bại 2", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void recyclerViewCategory() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2 = findViewById(R.id.recyclerView);
        recyclerView2.setLayoutManager(linearLayoutManager);

        //Get API -> sử dụng Retrofit để lấy dữ liệu từ api
        Call<CategoryResponse> call = apiService.getAllCategory();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body().getData();
                    categoryAdapter = new CategoryAdapter(categoryList,MainActivity.this);
                    recyclerView2.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lấy dữ liệu thất bại 2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomNavigation() {
        FloatingActionButton floatingActionButton = findViewById(R.id.card_btn);
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.searchBtn);
        LinearLayout billBtn = findViewById(R.id.billBtn);
        LinearLayout chatBtn = findViewById(R.id.chatBtn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        billBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BillActivity.class));
            }
        });
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });
    }
    private void ActionViewFlipperMain(){
        List<String> arrayListFlipper = new ArrayList<>();
        arrayListFlipper.add("https://i.pinimg.com/originals/af/a4/8b/afa48b452d6fba2422d3dc8b95b159e8.jpg");
        arrayListFlipper.add("https://bizweb.dktcdn.net/100/413/756/collections/jordan-2.jpg?v=1617462460240");
        arrayListFlipper.add("https://i.pinimg.com/originals/38/fb/24/38fb24becd9a52b0278f6e2a8f5d2315.jpg");
        arrayListFlipper.add("https://www.tiendauroi.com/wp-content/uploads/2019/06/b85c2fbf5af2b6199160e22f8629015019c12b10_2_690x218.jpeg");
        for(int i =0;i<arrayListFlipper.size();i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(arrayListFlipper.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipperBanner.addView(imageView);
        }
        viewFlipperBanner.setFlipInterval(3000);
        viewFlipperBanner.setAutoStart(true);
        //thiết lập annotation cho flipper
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_right);
        viewFlipperBanner.setInAnimation(slide_in);
        viewFlipperBanner.setOutAnimation(slide_out);
    }
}
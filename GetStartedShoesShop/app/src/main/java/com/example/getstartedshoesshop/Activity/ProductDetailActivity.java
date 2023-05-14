package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.Model.ProductInCart;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Request.AddToCartRequest;
import com.example.getstartedshoesshop.Response.CartResponse;
import com.example.getstartedshoesshop.Response.ListProductResponse;
import com.example.getstartedshoesshop.Response.ProductResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private APIService apiService;

    Product product;
    TextView tvNameProduct, tvPrice, tvCount, tvDescription;
    ImageView ivImage, ivBack, ivLike;
    Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        anhXa();

        LoadProductDetail();

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish the current activity to remove it from the back stack

            }
        });


    }

    private void addToCart() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String userID = user.getId();

        String productID = getIntent().getStringExtra("id");

        List<ProductInCart> products = new ArrayList<>();
        products.add(new ProductInCart(1, productID));
        AddToCartRequest request = new AddToCartRequest(userID, products);

        apiService = RetrofitClient.getClient().create(APIService.class);
        Call<CartResponse> call = apiService.addToCart(request);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý kết quả trả về khi request thành công
                    Toast.makeText(getApplicationContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadProductDetail() {
        // lấy giá trị id từ Intent
        String id = getIntent().getStringExtra("id");


        apiService = RetrofitClient.getClient().create(APIService.class);

        Call<ProductResponse> call = apiService.getProductById(id);

        // xử dụng retrofit để lấy dữ liệu sản phẩm theo id
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    ProductResponse productResponse = response.body();
                    product = productResponse.getData();

                    // gắn lại các giá trị tvNameProduct,tvPrice,ivImage,tvDescription
                    tvNameProduct.setText(product.getName());
                    tvPrice.setText(String.valueOf(product.getPrice()));
                    Glide.with(getApplicationContext()).load(product.getImage()).into(ivImage);
                    tvDescription.setText(product.getDescription());
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lấy dữ liệu thất bại 2", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void anhXa() {
        tvNameProduct = findViewById(R.id.prod_name);
        tvPrice = findViewById(R.id.prices);
        tvDescription = findViewById(R.id.detailsText);
        ivImage = findViewById(R.id.big_image);
        ivBack = findViewById(R.id.back_button);
        ivLike = findViewById(R.id.like_button);
        btnAddToCart = findViewById(R.id.buy_button);
    }
}
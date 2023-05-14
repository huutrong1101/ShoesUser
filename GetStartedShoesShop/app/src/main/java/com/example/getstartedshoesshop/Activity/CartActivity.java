package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Adapter.CartAdapter;
import com.example.getstartedshoesshop.Adapter.CategoryAdapter;
import com.example.getstartedshoesshop.Adapter.ProductAdapter;
import com.example.getstartedshoesshop.Model.Cart;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.Model.ProductInCart;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Response.CartResponse;
import com.example.getstartedshoesshop.Response.ListProductResponse;
import com.example.getstartedshoesshop.Response.ProductResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private APIService apiService;

    private CartAdapter adapter;

    private List<Product> productList;

    private List<ProductInCart> listProductCart;

    TextView cartActivityTotalPriceTv;

    private Button cartActivityCheckoutBtn;

    ImageView imvBack;

    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        anhXa();

        getItemInCart();

        cartActivityCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        title.setText("Giỏ hàng");

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void anhXa(){
        cartActivityTotalPriceTv = findViewById(R.id.cartActivityTotalPriceTv);
        cartActivityCheckoutBtn = findViewById(R.id.cartActivityCheckoutBtn);
        imvBack = findViewById(R.id.imvBack);
        title = findViewById(R.id.title);
    }


    private void getItemInCart() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        List<Product> productList = SharedPrefManager.getInstance(getApplicationContext()).getData();

        String userID = user.getId();

        //Get API -> sử dụng Retrofit để lấy dữ liệu từ api
        apiService = RetrofitClient.getClient().create(APIService.class);
        Call<CartResponse> call = apiService.getCart(userID);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    listProductCart = response.body().getData().getProducts();

                    Integer totalPrice = 0;

                    List<ProductInCart> productInCart = new ArrayList<>();

                    if (listProductCart != null && productList != null) {
                        for (Product p : productList) {
                            for (ProductInCart pCart : listProductCart) {
                                if (p.getId().equals(pCart.getProductId())) {
                                    productInCart.add(pCart);
                                    totalPrice += pCart.getQuantity() * p.getPrice();
                                    break;
                                }
                            }
                        }
                    }
                    
                    // lấy giá trị của totalPrice
                    adapter = new CartAdapter(productInCart, CartActivity.this,cartActivityTotalPriceTv);
                    recyclerView.setAdapter(adapter);

                    DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
                    String formattedTotalPrice = decimalFormat.format(totalPrice);
                    cartActivityTotalPriceTv.setText(formattedTotalPrice);

                } else {
                    Toast.makeText(CartActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }
}
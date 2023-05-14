package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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

import com.bumptech.glide.Glide;
import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Adapter.CartAdapter;
import com.example.getstartedshoesshop.Adapter.OrderAdapter;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.Model.ProductInCart;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Request.AddToCartRequest;
import com.example.getstartedshoesshop.Request.AddToOrderRequest;
import com.example.getstartedshoesshop.Response.CartResponse;
import com.example.getstartedshoesshop.Response.DeleteCartResponse;
import com.example.getstartedshoesshop.Response.OrderResponse;
import com.example.getstartedshoesshop.Response.ProductResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private APIService apiService;

    private OrderAdapter adapter;

    private List<ProductInCart> listProductCart;

    private TextView cartActivityTotalPriceTv, cartActivityNameEditText, cartActivityAddressEditText,cartActivityPhoneEditText;

    AppCompatButton cartActivityCheckoutBtn;

    ImageView imvBack;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        anhXa();

        getListItemInCart();

        cartActivityCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToOrder();
            }
        });

    }

    public void anhXa(){
        cartActivityTotalPriceTv = findViewById(R.id.cartActivityTotalPriceTv);
        cartActivityNameEditText = findViewById(R.id.cartActivityNameEditText);
        cartActivityAddressEditText = findViewById(R.id.cartActivityAddressEditText);
        cartActivityPhoneEditText = findViewById(R.id.cartActivityPhoneEditText);
        cartActivityCheckoutBtn = findViewById(R.id.cartActivityCheckoutBtn);

        imvBack = findViewById(R.id.imvBack);

        title = findViewById(R.id.title);

        title.setText("Thanh toán đơn hàng");

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void deleteCart(){
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        String userID = user.getId();

        apiService = RetrofitClient.getClient().create(APIService.class);
        Call<DeleteCartResponse> call = apiService.deleteCart(userID);

        call.enqueue(new Callback<DeleteCartResponse>() {
            @Override
            public void onResponse(Call<DeleteCartResponse> call, Response<DeleteCartResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý kết quả trả về khi request thành công
                } else {
                    Toast.makeText(getApplicationContext(), "Xóa cart thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteCartResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Xóa cart thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToOrder() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        List<Product> productList = SharedPrefManager.getInstance(getApplicationContext()).getData();

        String userID = user.getId();
        List<ProductInCart> productInCart = new ArrayList<>();

        String shippingAddress = cartActivityAddressEditText.getText().toString();
        String name = cartActivityNameEditText.getText().toString();
        String phone = cartActivityPhoneEditText.getText().toString();

        Map<String, List<ProductInCart>> shopProductMap = new HashMap<>();

        for (ProductInCart pCart : listProductCart) {
            String shopId = "";
            for (Product p : productList){
                if (p.getId().equals(pCart.getProductId())){
                    shopId = p.getShopId();
                }
            }
            if (shopProductMap.containsKey(shopId)) {
                List<ProductInCart> productListInCart = shopProductMap.get(shopId);
                productListInCart.add(pCart);
            } else {
                List<ProductInCart> productListInCart = new ArrayList<>();
                productListInCart.add(pCart);
                shopProductMap.put(shopId, productListInCart);
            }
        }

        for (Map.Entry<String, List<ProductInCart>> entry : shopProductMap.entrySet()) {
            String shopId = entry.getKey();
            List<ProductInCart> productInCartList = entry.getValue();

            // Tạo đơn hàng cho cửa hàng với shopId và danh sách sản phẩm tương ứng
            AddToOrderRequest request = new AddToOrderRequest(shippingAddress, name, phone, userID, productInCartList);

            apiService = RetrofitClient.getClient().create(APIService.class);
            Call<OrderResponse> call = apiService.addOrder(request);

            call.enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    if (response.isSuccessful()) {
                        // Xử lý kết quả trả về khi request thành công
                        Toast.makeText(getApplicationContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                        deleteCart();
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getListItemInCart(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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
                    adapter = new OrderAdapter(productInCart, OrderActivity.this,cartActivityTotalPriceTv);
                    recyclerView.setAdapter(adapter);

                    DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
                    String formattedTotalPrice = decimalFormat.format(totalPrice);
                    cartActivityTotalPriceTv.setText(formattedTotalPrice);

                } else {
                    Toast.makeText(OrderActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }
}
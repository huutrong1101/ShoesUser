package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Adapter.OrderAdapter;
import com.example.getstartedshoesshop.Model.Order;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.Model.ProductInCart;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Response.CartResponse;
import com.example.getstartedshoesshop.Response.OrderResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BillDetailActivity extends AppCompatActivity {

    private APIService apiService;

    private OrderAdapter adapter;

    ImageView imvBack;

    TextView title,name,phone,shippingAddress,time,status,cartActivityTotalPriceTv;

    private RecyclerView recyclerView;

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        anhXa();

        getListItemInOrder();
    }

    private void anhXa(){
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        shippingAddress = findViewById(R.id.shippingAddress);
        time = findViewById(R.id.time);
        status = findViewById(R.id.status);
        imvBack = findViewById(R.id.imvBack);
        title = findViewById(R.id.title);
        cartActivityTotalPriceTv = findViewById(R.id.cartActivityTotalPriceTv);

        title.setText("Chi tiết hóa đơn");

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    public void getListItemInOrder(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.billDetailRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        // lấy giá trị id từ Intent
        String id = getIntent().getStringExtra("id");
        List<Product> productList = SharedPrefManager.getInstance(getApplicationContext()).getData();

        //Get API -> sử dụng Retrofit để lấy dữ liệu từ api
        apiService = RetrofitClient.getClient().create(APIService.class);
        Call<OrderResponse> call = apiService.getOrderByID(id);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    order = response.body().getData();

                    // lấy các giá trị text
                    String dateTimeString = order.getDateOrdered();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

                    try {
                        Date date = inputFormat.parse(dateTimeString);
                        String formattedDateTime = outputFormat.format(date);

                        time.setText(formattedDateTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    name.setText(order.getName());
                    phone.setText(order.getPhone());
                    shippingAddress.setText(order.getShippingAddress());
                    status.setText(order.getStatus());

                    Integer totalPrice = 0;

                    List<ProductInCart> productInCart = new ArrayList<>();

                    if (order.getOrderItems() != null && productList != null) {
                        for (Product p : productList) {
                            for (ProductInCart pCart : order.getOrderItems()) {
                                if (p.getId().equals(pCart.getProductId())) {
                                    productInCart.add(pCart);
                                    totalPrice += pCart.getQuantity() * p.getPrice();
                                    break;
                                }
                            }
                        }
                    }

                    adapter = new OrderAdapter(productInCart, BillDetailActivity.this,cartActivityTotalPriceTv);
                    recyclerView.setAdapter(adapter);

                    DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
                    String formattedTotalPrice = decimalFormat.format(totalPrice);
                    cartActivityTotalPriceTv.setText(formattedTotalPrice);
                } else {
                    Toast.makeText(BillDetailActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }
}
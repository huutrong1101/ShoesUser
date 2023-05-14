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
import com.example.getstartedshoesshop.Adapter.BillAdapter;
import com.example.getstartedshoesshop.Adapter.CartAdapter;
import com.example.getstartedshoesshop.Model.Order;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.Model.ProductInCart;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Response.CartResponse;
import com.example.getstartedshoesshop.Response.ListOrderResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private APIService apiService;

    private List<Order> listOrder;

    private BillAdapter adapter;

    ImageView imvBack;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        imvBack = findViewById(R.id.imvBack);

        title = findViewById(R.id.title);
        
        title.setText("Lịch sử giao dịch");

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getListOrder();
    }

    private void getListOrder(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.unpaid_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String userID = user.getId();

        apiService = RetrofitClient.getClient().create(APIService.class);
        Call<ListOrderResponse> call = apiService.getListOrderByUserID(userID);

        call.enqueue(new Callback<ListOrderResponse>() {
            @Override
            public void onResponse(Call<ListOrderResponse> call, Response<ListOrderResponse> response) {
                if (response.isSuccessful()) {
                    listOrder = response.body().getData();


                    Collections.reverse(listOrder);
                    // lấy giá trị của totalPrice
                    adapter = new BillAdapter(BillActivity.this, listOrder);
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(BillActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListOrderResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }
}
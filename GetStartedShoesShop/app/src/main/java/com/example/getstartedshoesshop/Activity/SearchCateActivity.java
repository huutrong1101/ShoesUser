package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Adapter.ProductAdapter;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Response.ListProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchCateActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private APIService apiService;
    private RecyclerView bestSellerRecyclerView;

    private TextView title;

    private ProductAdapter adapter;

    List<Product> newReleaseList;
    List <Product> bestSellerList;

    private String cateID;

    private String titleName;

    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_cate);

        apiService = RetrofitClient.getClient().create(APIService.class);

        Intent intent = getIntent();
        cateID = intent.getStringExtra("cateID");
        titleName = intent.getStringExtra("title");

        anhXa();

        title.setText("Welcome to\n"+titleName+ " Store");

        showBestSeller();

        showNewReleaseList();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchCateActivity.this, SearchActivity.class);
                intent.putExtra("data", cateID);
                startActivity(new Intent(SearchCateActivity.this, SearchActivity.class));
            }
        });
    }

    private void anhXa(){
        recyclerView = findViewById(R.id.recyclerView);
        bestSellerRecyclerView = findViewById(R.id.best_seller_recycler);
        title = findViewById(R.id.textView);
        search = findViewById(R.id.search);
    }

    private void showNewReleaseList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Get API -> sử dụng Retrofit để lấy dữ liệu từ api
        Call<ListProductResponse> call = apiService.getProductOfCategory(cateID);
        call.enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    newReleaseList = response.body().getData();

                    adapter = new ProductAdapter(newReleaseList,SearchCateActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(SearchCateActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                Toast.makeText(SearchCateActivity.this, "Lấy dữ liệu thất bại 2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBestSeller(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bestSellerRecyclerView = findViewById(R.id.best_seller_recycler);
        bestSellerRecyclerView.setLayoutManager(linearLayoutManager);

        //Get API -> sử dụng Retrofit để lấy dữ liệu từ api
        Call<ListProductResponse> call = apiService.getListSortByLike(cateID,"-like");

        call.enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    bestSellerList = response.body().getData();

                    adapter = new ProductAdapter(bestSellerList,SearchCateActivity.this);
                    bestSellerRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(SearchCateActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                Toast.makeText(SearchCateActivity.this, "Lấy dữ liệu thất bại 2", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
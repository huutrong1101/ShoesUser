package com.example.getstartedshoesshop.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Adapter.ProductAdapter;
import com.example.getstartedshoesshop.Adapter.SearchItemAdapter;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Response.ListProductResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Product> productList;
    private SearchItemAdapter searchItemAdapter;
    private SearchView searchView;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String cateID = intent.getStringExtra("data");

        if (cateID != null){
            getData(cateID);
        }
        else {
            productList = SharedPrefManager.getInstance(getApplicationContext()).getData();

            searchItemAdapter = new SearchItemAdapter(productList,SearchActivity.this);
            recyclerView.setAdapter(searchItemAdapter);
        }
    }

    private void getData(String cateID) {
        apiService = RetrofitClient.getClient().create(APIService.class);


        //Get API -> sử dụng Retrofit để lấy dữ liệu từ api
        Call<ListProductResponse> call = apiService.getProductOfCategory(cateID);

        call.enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    productList = response.body().getData();
                    searchItemAdapter = new SearchItemAdapter(productList,SearchActivity.this);
                    recyclerView.setAdapter(searchItemAdapter);
                } else {
                    Toast.makeText(SearchActivity.this, "Lấy dữ liệu thất bại 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Lấy dữ liệu thất bại 2", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filterList(String text){
        List<Product> filteredListProduct = new ArrayList<>();
        for (Product p : productList){
            if (p.getName().toLowerCase().contains(text.toLowerCase())){
                filteredListProduct.add(p);
            }
        }
        if (filteredListProduct.isEmpty()){
            Toast.makeText(this, "Không có sản phẩm cần tìm", Toast.LENGTH_SHORT).show();
        }
        else {
            searchItemAdapter.setFilteredList(filteredListProduct);
        }
    }
}
package com.example.getstartedshoesshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.getstartedshoesshop.API.APIService;
import com.example.getstartedshoesshop.API.RetrofitClient;
import com.example.getstartedshoesshop.Activity.CartActivity;
import com.example.getstartedshoesshop.Activity.ProductDetailActivity;
import com.example.getstartedshoesshop.Model.Product;
import com.example.getstartedshoesshop.Model.ProductInCart;
import com.example.getstartedshoesshop.Model.User;
import com.example.getstartedshoesshop.R;
import com.example.getstartedshoesshop.Response.CartResponse;
import com.example.getstartedshoesshop.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHodler> {

    Context context;
    private List<ProductInCart> shoeCartList;

    private TextView cartActivityTotalPriceTv;


    public CartAdapter(List<ProductInCart> shoeCartList, Context context, TextView cartActivityTotalPriceTv) {
        this.shoeCartList = shoeCartList;
        this.context = context;
        this.cartActivityTotalPriceTv = cartActivityTotalPriceTv;
    }

    @NonNull
    @Override
    public CartViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_cart_item, parent, false);
        return new CartViewHodler(view);
    }

    public Product getProductByID(ProductInCart pCart) {
        List<Product> productList = SharedPrefManager.getInstance(context.getApplicationContext()).getData();
        for (Product product : productList) {
            if (product.getId().equals(pCart.getProductId())) {
                return product;
            }
        }
        return null;
    }

    private void updateTotalPrice(List<ProductInCart> listProductCart) {
        double totalPrice = 0;
        List<Product> productList = SharedPrefManager.getInstance(context.getApplicationContext()).getData();
        for (Product p : productList) {
            for (ProductInCart pCart : listProductCart) {
                if (p.getId().equals(pCart.getProductId())) {
                    totalPrice += pCart.getQuantity() * p.getPrice();
                    break;
                }
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        String formattedTotalPrice = decimalFormat.format(totalPrice);
        cartActivityTotalPriceTv.setText(formattedTotalPrice);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHodler holder, int position) {

        ProductInCart pCart = shoeCartList.get(position);

        String quantityString = String.valueOf(pCart.getQuantity());

        Product shoeCart = getProductByID(pCart);

        int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(shoeCart.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(context)
                .load(shoeCart.getImage())
                .into(holder.shoeImageView);

        holder.shoeNameTv.setText(shoeCart.getName());
        holder.shoeBrandNameTv.setText(shoeCart.getBrand());
        holder.shoeQuantity.setText(quantityString);
        holder.shoePriceTv.setText(String.valueOf(shoeCart.getPrice()));


        holder.deleteShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = SharedPrefManager.getInstance(context.getApplicationContext()).getUser();

                String userID = user.getId();
                String productID = shoeCart.getId();

                APIService apiService = RetrofitClient.getClient().create(APIService.class);
                Call<CartResponse> call = apiService.handleQuantity("delete", userID, productID);

                call.enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            List<ProductInCart> listProductCart = response.body().getData().getProducts();
                            // xóa sản phẩm
                            shoeCartList.remove(pCart);
                            notifyDataSetChanged(); // thông báo cho Adapter cập nhật lại giao diện ngay lập tức
                            updateTotalPrice(listProductCart);
                            Toast.makeText(context, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Có lỗi trong quá trình xóa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(context, "Có lỗi trong quá trình xóa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        holder.addQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = SharedPrefManager.getInstance(context.getApplicationContext()).getUser();

                String userID = user.getId();

                String productID = shoeCart.getId();

                int newQuantity = pCart.getQuantity() + 1;

                APIService apiService = RetrofitClient.getClient().create(APIService.class);
                Call<CartResponse> call = apiService.handleQuantity("increase", userID, productID);

                call.enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            List<ProductInCart> listProductCart = response.body().getData().getProducts();

                            pCart.setQuantity(newQuantity);
                            holder.shoeQuantity.setText(String.valueOf(newQuantity));

                            updateTotalPrice(listProductCart);
                            Toast.makeText(context, "Tăng số lượng sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Có lỗi trong quá trình xóa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(context, "Có lỗi trong quá trình xóa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.minusQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = SharedPrefManager.getInstance(context.getApplicationContext()).getUser();

                String userID = user.getId();

                String productID = shoeCart.getId();

                int newQuantity = pCart.getQuantity() - 1;

                APIService apiService = RetrofitClient.getClient().create(APIService.class);
                Call<CartResponse> call = apiService.handleQuantity("decrease", userID, productID);

                call.enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            List<ProductInCart> listProductCart = response.body().getData().getProducts();
                            if (newQuantity <= 0) {
                                shoeCartList.remove(pCart);
                                notifyDataSetChanged(); // thông báo cho Adapter cập nhật lại giao diện ngay lập tức
                                Toast.makeText(context, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                            } else {
                                pCart.setQuantity(newQuantity);
                                holder.shoeQuantity.setText(String.valueOf(newQuantity));

                                updateTotalPrice(listProductCart);
                                Toast.makeText(context, "Giảm số lượng sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Có lỗi trong quá trình xóa 1", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(context, "Có lỗi trong quá trình xóa 2", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (shoeCartList == null) {
            return 0;
        } else {
            return shoeCartList.size();
        }
    }

    public class CartViewHodler extends RecyclerView.ViewHolder {

        private TextView shoeNameTv, shoeBrandNameTv, shoePriceTv, shoeQuantity;
        private ImageView deleteShoeBtn;
        private ImageView shoeImageView;
        private ImageButton addQuantityBtn, minusQuantityBtn;

        public CartViewHodler(@NonNull View itemView) {
            super(itemView);

            shoeNameTv = itemView.findViewById(R.id.eachCartItemName);
            shoeBrandNameTv = itemView.findViewById(R.id.eachCartItemBrandNameTv);
            shoePriceTv = itemView.findViewById(R.id.eachCartItemPriceTv);
            deleteShoeBtn = itemView.findViewById(R.id.eachCartItemDeleteBtn);
            shoeImageView = itemView.findViewById(R.id.eachCartItemIV);
            shoeQuantity = itemView.findViewById(R.id.eachCartItemQuantityTV);
            addQuantityBtn = itemView.findViewById(R.id.eachCartItemAddQuantityBtn);
            minusQuantityBtn = itemView.findViewById(R.id.eachCartItemMinusQuantityBtn);
        }
    }
}

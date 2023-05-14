package com.example.getstartedshoesshop.API;

import com.example.getstartedshoesshop.Request.AddToCartRequest;
import com.example.getstartedshoesshop.Request.AddToOrderRequest;
import com.example.getstartedshoesshop.Request.LoginRequest;
import com.example.getstartedshoesshop.Request.RegisterRequest;
import com.example.getstartedshoesshop.Response.CartResponse;
import com.example.getstartedshoesshop.Response.CategoryResponse;
import com.example.getstartedshoesshop.Response.DeleteCartResponse;
import com.example.getstartedshoesshop.Response.ListOrderResponse;
import com.example.getstartedshoesshop.Response.LoginResponse;
import com.example.getstartedshoesshop.Response.ListProductResponse;
import com.example.getstartedshoesshop.Response.OrderResponse;
import com.example.getstartedshoesshop.Response.ProductResponse;
import com.example.getstartedshoesshop.Response.RegisterResponse;
import com.example.getstartedshoesshop.Response.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    // USER
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/v1/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @Multipart
    @PUT("api/v1/users/uploadImage")
    Call<UserResponse> updateUser(@Part("id") RequestBody userId,
                                  @Part MultipartBody.Part image);

    @Headers({"Content-Type: application/json"})
    @PUT("api/v1/users/edit/{id}")
    Call<UserResponse> updateProfile(@Path("id") String id,
                                     @Body RegisterRequest request);

    // PRODUCT
    @GET("api/v1/product")
    Call<ListProductResponse> getAllProduct();

    @GET("api/v1/product/{id}")
    Call<ProductResponse> getProductById(@Path("id") String id);


    // CATEGORY
    @GET("api/v1/category")
    Call<CategoryResponse> getAllCategory();

    @GET("api/v1/category/{id}")
    Call<ListProductResponse> getProductOfCategory(@Path("id") String id);

    @GET("api/v1/category/{id}")
    Call<ListProductResponse> getListSortByLike(@Path("id") String id, @Query("sort") String sortBy);


    // CART
    @GET("api/v1/cart")
    Call<CartResponse> getAllCart();

    @GET("api/v1/cart/{userId}")
    Call<CartResponse> getCart(@Path("userId") String userId);

    @POST("api/v1/cart/add")
    Call<CartResponse> addToCart(@Body AddToCartRequest request);

    @PUT("api/v1/cart/edit/{type}/{userId}/{pId}")
    Call<CartResponse> handleQuantity(@Path("type") String type,
                                      @Path("userId") String userId,
                                      @Path("pId") String pId);

    @DELETE("api/v1/cart/delete/{userID}")
    Call<DeleteCartResponse> deleteCart(@Path("userID") String userID);

    // ORDER
    @POST("api/v1/order/add")
    Call<OrderResponse> addOrder(@Body AddToOrderRequest order);

    @GET("api/v1/order/{id}")
    Call<ListOrderResponse> getListOrderByUserID(@Path("id") String id);

    @GET("api/v1/order/item/{id}")
    Call<OrderResponse> getOrderByID(@Path("id") String id);

}

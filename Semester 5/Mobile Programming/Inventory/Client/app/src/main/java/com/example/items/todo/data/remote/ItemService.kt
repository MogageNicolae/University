package com.example.items.todo.data.remote

import com.example.items.todo.data.Item
import com.example.items.todo.data.Product
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ItemService {

    @GET("/product")
    suspend fun find(@Query("page") page: Int): Response

    @GET("/product")
    suspend fun findWithAuth(@Header("Authorization") authorization: String): List<Product>

    @GET("/item/{id}")
    suspend fun read(
        @Path("id") itemId: Int?
    ): Product;

    @GET("/item/{id}")
    suspend fun readWithAuth(
        @Header("Authorization") authorization: String,
        @Path("id") itemId: Int?
    ): Product;

    @Headers("Content-Type: application/json")
    @POST("/item")
    suspend fun create(@Body item: Item): Item

    @Headers("Content-Type: application/json")
    @POST("/item")
    suspend fun createWithAuth(@Header("Authorization") authorization: String, @Body product: Product): Product

    @Headers("Content-Type: application/json")
    @PUT("/item/{id}")
    suspend fun update(
        @Path("id") itemId: Int?,
        @Body product: Product
    ): Product

    @Headers("Content-Type: application/json")
    @PUT("/item/{id}")
    suspend fun updateWithAuth(
        @Header("Authorization") authorization: String,
        @Path("id") itemId: Int?,
        @Body product: Product
    ): Product

}
package com.example.orders.core.data.remoteimport com.google.gson.GsonBuilderimport okhttp3.OkHttpClientimport retrofit2.Retrofitimport retrofit2.converter.gson.GsonConverterFactoryobject Api {    private const val url = "10.0.2.2:3000"    private const val httpUrl = "http://$url/"    const val wsUrl = "ws://$url"    private var gson = GsonBuilder().create()    val tokenInterceptor = TokenInterceptor()    val okHttpClient = OkHttpClient.Builder().apply {        this.addInterceptor(tokenInterceptor)    }.build()    val retrofit: Retrofit = Retrofit.Builder()        .baseUrl(httpUrl)        .addConverterFactory(GsonConverterFactory.create(gson))        .build()}
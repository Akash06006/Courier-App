package com.courierdriver.api

// Created by Saira on 03/07/2019.

import retrofit2.Response

interface ApiResponse<T> {
    fun onResponse(mResponse: Response<T>)
    fun onError(mKey: String)
}
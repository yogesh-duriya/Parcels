package com.parcels.util.network

import com.parcels.pojo.CompanyDetailPojo
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface RequestInterface {

    @FormUrlEncoded
    @POST(".")
    abstract fun performServerRequest(@Field("Request") params: String?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST(".")
    abstract fun getLogo(@Field("Request") params: String?): Observable<List<CompanyDetailPojo>>

}
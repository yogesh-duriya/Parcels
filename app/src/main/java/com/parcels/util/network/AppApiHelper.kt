package com.parcels.util.network

import com.parcels.pojo.CompanyDetailPojo
import io.reactivex.Observable

class AppApiHelper {

    val requestInterface = ApiClient.getClient().create(RequestInterface::class.java)


    fun getCompanyLogo(params: String?) : Observable<List<CompanyDetailPojo>> {
        return requestInterface.getLogo(params)
    }


}
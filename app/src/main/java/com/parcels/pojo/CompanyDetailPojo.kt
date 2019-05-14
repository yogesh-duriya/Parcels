package com.parcels.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CompanyDetailPojo(@Expose
                        @SerializedName("Status")
                        var status: String? = null,

                        @Expose
                        @SerializedName("CompanyName")
                        var companyName: String? = null,

                        @Expose
                        @SerializedName("CompanyLogo")
                        var companyLogo: String? = null,

                        @Expose
                        @SerializedName("CopyRight")
                        var copyRight: String? = null,

                        @Expose
                        @SerializedName("Error Description")
                        var errorDescription: String? = null)
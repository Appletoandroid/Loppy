package com.appleto.loppyuser.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoadTypesModel {

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<LoadTypeDatum>? = null

}

class LoadTypeDatum {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("load_type")
    @Expose
    var loadType: String? = null

}
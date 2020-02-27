package com.appleto.loppyuser.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GoodTypesModel {

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<GoodTypeDatum>? = null

}

class GoodTypeDatum {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("good_type")
    @Expose
    var goodType: String? = null

}
package com.appleto.loppyuser.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NotificationListModel {

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<NotificationListModelDatum>? = null

}

class NotificationListModelDatum {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("user_name")
    @Expose
    var userName: String? = null
    @SerializedName("mobile_no")
    @Expose
    var mobileNo: String? = null
    @SerializedName("amount")
    @Expose
    var amount: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null

}
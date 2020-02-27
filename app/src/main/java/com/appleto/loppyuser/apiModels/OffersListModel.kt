package com.appleto.loppyuser.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class OffersListModel {
    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<OfferListDatum>? = null
}

class OfferListDatum {

    @SerializedName("offer_id")
    @Expose
    var offerId: String? = null
    @SerializedName("discount_code")
    @Expose
    var discountCode: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("discount_amount")
    @Expose
    var discountAmount: String? = null

}
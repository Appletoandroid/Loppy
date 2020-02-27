package com.appleto.loppyuser.apiModels

import android.os.Parcel
import android.os.Parcelable.Creator
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TruckListModel : Parcelable {

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<TruckListDatum>? = null

    protected constructor(`in`: Parcel) {
        this.status = `in`.readValue(Int::class.java.classLoader) as Int?
        this.message = `in`.readValue(String::class.java.classLoader) as String?
        `in`.readList(this.data!!, TruckListDatum::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(status)
        dest.writeValue(message)
        dest.writeList(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<TruckListModel> {
        override fun createFromParcel(parcel: Parcel): TruckListModel {
            return TruckListModel(parcel)
        }

        override fun newArray(size: Int): Array<TruckListModel?> {
            return arrayOfNulls(size)
        }
    }
}

class TruckListDatum : Parcelable {

    @SerializedName("truck_id")
    @Expose
    var truckId: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("capacity")
    @Expose
    var capacity: String? = null

    protected constructor(`in`: Parcel) {
        this.truckId = `in`.readValue(String::class.java.classLoader) as String?
        this.name = `in`.readValue(String::class.java.classLoader) as String?
        this.image = `in`.readValue(String::class.java.classLoader) as String?
        this.capacity = `in`.readValue(String::class.java.classLoader) as String?
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(truckId)
        dest.writeValue(name)
        dest.writeValue(image)
        dest.writeValue(capacity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<TruckListDatum> {
        override fun createFromParcel(parcel: Parcel): TruckListDatum {
            return TruckListDatum(parcel)
        }

        override fun newArray(size: Int): Array<TruckListDatum?> {
            return arrayOfNulls(size)
        }
    }
}
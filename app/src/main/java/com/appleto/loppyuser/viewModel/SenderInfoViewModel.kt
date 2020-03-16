package com.appleto.loppyuser.viewModel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appleto.loppyuser.apiModels.GoodTypesModel
import com.appleto.loppyuser.apiModels.LoadTypesModel
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.helper.Utils
import com.appleto.loppyuser.retrofit2.ApiService
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SenderInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService by lazy {
        PrefUtils.getStringValue(application, Const.TOKEN)?.let { ApiService.create(it) }
    }
    var disposable: Disposable? = null
    var responseLoadType = MutableLiveData<LoadTypesModel>()
    var responseGoodType = MutableLiveData<GoodTypesModel>()
    var response = MutableLiveData<JsonObject>()

    fun getLoadType(context: Context) {
        Utils.showProgress(context)
        disposable =
            apiService
                ?.getLoadTypes()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        Utils.hideProgress()
                        if (result.status == 1) {
                            responseLoadType.value = result
                        } else {
                            Toast.makeText(
                                context,
                                "No load types found.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    { error ->
                        Utils.hideProgress()
                        Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                )
    }

    fun getGoodType(context: Context) {
        Utils.showProgress(context)
        disposable =
            apiService
                ?.getGoodType()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        Utils.hideProgress()
                        if (result.status == 1) {
                            responseGoodType.value = result
                        } else {
                            Toast.makeText(
                                context,
                                "No load types found.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    { error ->
                        Utils.hideProgress()
                        Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                )
    }

    fun addRideRequest(context: Context, map: HashMap<String, String>) {
        Utils.showProgress(context)
        disposable =
            apiService
                ?.addRideRequest(map)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        Utils.hideProgress()
                        if (result.has("status") && result.get("status").asInt == 1) {
                            response.value = result
                        } else {
                            Toast.makeText(
                                context,
                                result.get("message").asString,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    { error ->
                        Utils.hideProgress()
                        Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                )
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}
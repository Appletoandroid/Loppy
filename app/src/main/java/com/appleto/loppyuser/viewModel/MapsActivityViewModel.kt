package com.appleto.loppyuser.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.retrofit2.ApiService
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MapsActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService by lazy {
        PrefUtils.getStringValue(application, Const.TOKEN)?.let { ApiService.create(it) }
    }
    var disposable: Disposable? = null
    var response = MutableLiveData<JsonObject>()
    var driverId = ""

    fun getDriverLocation() {
        disposable = Observable.interval(1000, 60000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::callNewOrder, this::onError)
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
    }

    private fun callNewOrder(long: Long) {
        val observable = apiService?.getDriverLocation(driverId)
        observable?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.map { result -> result }
            ?.subscribe(this::handleResults, this::handleErrors)
    }

    private fun handleResults(data: JsonObject) {
        response.value = data
    }

    private fun handleErrors(t: Throwable) {
        t.printStackTrace()
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}
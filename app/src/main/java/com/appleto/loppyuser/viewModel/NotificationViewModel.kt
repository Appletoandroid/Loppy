package com.appleto.loppyuser.viewModel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appleto.loppyuser.apiModels.NotificationListModel
import com.appleto.loppyuser.apiModels.PendingRideModel
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.helper.Utils
import com.appleto.loppyuser.retrofit2.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService by lazy {
        PrefUtils.getStringValue(application, Const.TOKEN)?.let { ApiService.create(it) }
    }
    var disposable: Disposable? = null
    var response = MutableLiveData<NotificationListModel>()

    fun getNotifications(context: Context, userId: String) {
        Utils.showProgress(context)
        disposable =
            apiService
                ?.getNotifications(userId)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        Utils.hideProgress()
                        if (result.status == 1) {
                            response.value = result
                        } else {
                            response.value = result
                            Toast.makeText(
                                context,
                                result.message,
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
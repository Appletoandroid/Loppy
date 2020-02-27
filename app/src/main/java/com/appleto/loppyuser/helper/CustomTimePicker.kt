package com.appleto.loppyuser.helper

import android.app.TimePickerDialog
import android.content.Context
import java.util.*

abstract class CustomTimePicker(
    mContext: Context,
    selectedDate: Calendar?
) {

    internal var cal = Calendar.getInstance()

    init {
        if (selectedDate != null)
            cal = selectedDate

        val fromTimePickerDialog = TimePickerDialog(
            mContext,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal = Calendar.getInstance()
                cal.set(Calendar.HOUR,hourOfDay)
                cal.set(Calendar.MINUTE,minute)
                onTimeChange(cal)
            }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true
        )

        fromTimePickerDialog.show()
    }

    abstract fun onTimeChange(calendar: Calendar)
}
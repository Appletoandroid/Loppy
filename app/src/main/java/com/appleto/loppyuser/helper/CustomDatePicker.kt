package com.appleto.loppyuser.helper

import android.app.DatePickerDialog
import android.content.Context
import java.util.*


abstract class CustomDatePicker(
    mContext: Context,
    selectedDate: Calendar?,
    maxSelection: Calendar?,
    minSelection: Calendar?
) {

    internal var cal = Calendar.getInstance()

    init {
        if (selectedDate != null)
            cal = selectedDate

        val fromDatePickerDialog = DatePickerDialog(
            mContext,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal = Calendar.getInstance()
                cal.set(year, monthOfYear, dayOfMonth)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                onDateChange(cal)
            }, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fromDatePickerDialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
        }*/

        if (minSelection != null)
            fromDatePickerDialog.datePicker.minDate = minSelection.timeInMillis

        if (maxSelection != null)
            fromDatePickerDialog.datePicker.maxDate = maxSelection.timeInMillis

        fromDatePickerDialog.show()
    }

    abstract fun onDateChange(calendar: Calendar)

}
package com.example.wefly.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.example.wefly.R

class ProgressBar(private val context: Context) {

    private lateinit var dialog : Dialog

    fun showProgressBar(){
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun hideProgressBar(){
        dialog.dismiss()
    }

}
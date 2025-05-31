package com.example.mycollections

import android.view.View
interface OnViewClickListener {
    fun onItemClickListener(view: View?, position: Int)
    fun onItemLongClickListener(view: View?, position: Int)
}
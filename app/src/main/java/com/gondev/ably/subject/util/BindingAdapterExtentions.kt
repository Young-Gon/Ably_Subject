package com.gondev.ably.subject.util

import android.view.View


fun interface ItemClickListener<T> {
    fun onItemClicked(view: View, item: T)
}
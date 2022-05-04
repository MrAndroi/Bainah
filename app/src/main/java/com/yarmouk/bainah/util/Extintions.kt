package com.yarmouk.bainah.util

import androidx.lifecycle.MutableLiveData


fun <T> MutableLiveData<T>.mutation(actions: (MutableLiveData<T>) -> Unit) {
    actions(this)
    this.value = this.value
}
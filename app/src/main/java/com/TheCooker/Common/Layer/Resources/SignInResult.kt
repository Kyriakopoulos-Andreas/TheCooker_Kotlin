package com.TheCooker.Common.Layer.Resources




sealed class LoginResults<out T>{

    data class Success <out T>(val data: T): LoginResults<T>()
    data class Error(val exception: Exception?): LoginResults<Nothing>()

}



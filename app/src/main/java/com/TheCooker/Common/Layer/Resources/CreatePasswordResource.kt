package com.TheCooker.Common.Layer.Resources

sealed class CreatePasswordResource<out T>{
    data class Success <out T>(val data: T): CreatePasswordResource<T>()
    data class Error(val exception: Exception?): CreatePasswordResource<Nothing>()
}
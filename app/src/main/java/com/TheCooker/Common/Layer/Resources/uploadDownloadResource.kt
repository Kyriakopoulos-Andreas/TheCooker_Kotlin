package com.TheCooker.Common.Layer.Resources

sealed class uploadDownloadResource <out T> {
    data class Success <out T>(val data: T): uploadDownloadResource<T>()
    data class Error(val exception: Exception?): uploadDownloadResource<Nothing>()
}
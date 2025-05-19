package com.TheCooker.Common.Layer.Resources

import com.google.firebase.firestore.DocumentSnapshot

sealed class uploadDownloadResourceWithPagination <out T>{
    data class Success <out T>(val data: T, val lastVisible: Int?): uploadDownloadResourceWithPagination<T>()
    data class Error(val exception: Exception?): uploadDownloadResourceWithPagination<Nothing>()
}
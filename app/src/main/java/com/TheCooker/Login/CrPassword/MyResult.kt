package com.TheCooker.Login.CrPassword

import java.lang.Exception

sealed class MyResult<out T>{
    data class Success <out T>(val data: T): MyResult<T>()
    data class Error (val exception: Exception): MyResult<Nothing>()
}
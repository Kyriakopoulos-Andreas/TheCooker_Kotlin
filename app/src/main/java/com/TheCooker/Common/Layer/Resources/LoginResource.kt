package com.TheCooker.Common.Layer.Resources

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class LoginResource<out T : Any> {

    data class Success<out T : Any>(val data: T) : LoginResource<T>()
    data class Error(val exception: Exception) : LoginResource<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
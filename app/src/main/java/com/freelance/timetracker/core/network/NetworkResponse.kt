package com.freelance.timetracker.core.network

/**
 * Structured API result. Pattern carried over (flattened) from
 * Drjacky/MVVMTemplate. Wrap Retrofit calls with [safeApiCall].
 */
sealed interface NetworkResponse<out T> {
    data class Success<T>(val data: T) : NetworkResponse<T>
    data class Error(val code: Int?, val message: String) : NetworkResponse<Nothing>
    data object Loading : NetworkResponse<Nothing>
}

inline fun <T> safeApiCall(block: () -> T): NetworkResponse<T> =
    try {
        NetworkResponse.Success(block())
    } catch (e: retrofit2.HttpException) {
        NetworkResponse.Error(e.code(), e.message())
    } catch (e: java.io.IOException) {
        NetworkResponse.Error(null, e.message ?: "Network error")
    }

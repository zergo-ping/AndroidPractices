package com.example.androidpractise.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.androidpractise.domain.error.AppError
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

/**
 * Выполняет сетевой вызов на [dispatcher] и приводит исключения к доменным ошибкам.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    call: suspend () -> T
): Result<T> = withContext(dispatcher) {
    try {
        Result.success(call())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (io: IOException) {
        Result.failure(AppError.Network())
    } catch (http: HttpException) {
        Result.failure(
            if (http.code() == 404) AppError.NotFound() else AppError.Server(http.code())
        )
    } catch (throwable: Throwable) {
        Result.failure(AppError.Unexpected())
    }
}

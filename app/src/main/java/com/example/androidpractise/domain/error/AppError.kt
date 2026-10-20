package com.example.androidpractise.domain.error

/**
 * Доменные ошибки: слой данных переводит исключения сети/парсинга в понятные приложению типы.
 */
sealed class AppError(message: String) : Exception(message) {

    class Network : AppError("Нет соединения с сетью. Проверьте подключение и повторите попытку.")

    class NotFound(message: String = "Данные не найдены") : AppError(message)

    class Server(val code: Int) : AppError("Ошибка сервера ($code). Попробуйте позже.")

    class Unexpected : AppError("Что-то пошло не так. Попробуйте позже.")
}

val Throwable.userMessage: String
    get() = (this as? AppError)?.message ?: AppError.Unexpected().message.orEmpty()

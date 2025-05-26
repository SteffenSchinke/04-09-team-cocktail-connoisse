package de.syntax.institut.projectweek.cocktailconnoisse.data.external

class ApiError(

    val type: ApiErrorType,
    val responseCode: Int? = null,
    val innerMessage: String? = null
) : Exception()

enum class ApiErrorType {

    RESPONSE_FAILED,
    PARSING_FAILED,
    PERSISTENCE_FAILED,
    CATEGORY_FAILED
}

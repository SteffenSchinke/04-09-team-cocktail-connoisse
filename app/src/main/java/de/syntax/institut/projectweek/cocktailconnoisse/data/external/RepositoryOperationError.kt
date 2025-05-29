package de.syntax.institut.projectweek.cocktailconnoisse.data.external

class RepositoryError(

    val type: RepositoryErrorType,
    val responseCode: Int? = null,
    val innerMessage: String? = null
) : Exception()

enum class RepositoryErrorType {

    RESPONSE_FAILED,
    PARSING_FAILED,
    PERSISTENCE_FAILED,
    PERSISTENCE_OPERATION_FAILED,
    CATEGORY_FAILED
}

package de.syntax.institut.projectweek.cocktailconnoisse.data.external

class RepositoryOperationError(

    val type: RepositoryOperationErrorType,
    val responseCode: Int? = null,
    val innerMessage: String? = null
) : Exception()

enum class RepositoryOperationErrorType {

    RESPONSE_FAILED,
    PARSING_FAILED,
    PERSISTENCE_FAILED,
    PERSISTENCE_OPERATION_FAILED,
    CATEGORY_FAILED
}

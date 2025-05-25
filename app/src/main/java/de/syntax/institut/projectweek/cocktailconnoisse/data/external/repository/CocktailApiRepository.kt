package de.syntax.institut.projectweek.cocktailconnoisse.data.external.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.toDomain
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CocktailApiRepository(

    private val api: ApiCocktail
) : CocktailApiRepositoryInterface {

    override fun getRandomCocktail(): Flow<Cocktail?> = flow {

        try {

            val response = api.apiCocktailService.getRandomCocktail()

            if (!response.isSuccessful) {
                throw ApiError(
                    type = ApiErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails?.firstOrNull() ?: throw ApiError(
                type = ApiErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.toDomain())
        } catch (e: ApiError) {

            throw e
        } catch (e: Exception) {

            throw ApiError(
                type = ApiErrorType.RESPONSE_FAILED, innerMessage = e.localizedMessage
            )
        }
    }

    override fun getCocktailsByType(type: String): Flow<List<Cocktail>> = flow {

        try {

            val response = api.apiCocktailService.getCocktailsByType(type)

            if (!response.isSuccessful) {
                throw ApiError(
                    type = ApiErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw ApiError(
                type = ApiErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.map { it.toDomain() })
        } catch (e: ApiError) {

            throw e
        } catch (e: Exception) {

            throw ApiError(
                type = ApiErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override fun getCocktailById(id: String): Flow<Cocktail?> = flow {

        try {

            val response = api.apiCocktailService.getCocktailById(id)

            if (!response.isSuccessful) {
                throw ApiError(
                    type = ApiErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails?.firstOrNull() ?: throw ApiError(
                type = ApiErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.toDomain())
        } catch (e: ApiError) {

            throw e
        } catch (e: Exception) {

            throw ApiError(
                type = ApiErrorType.RESPONSE_FAILED, innerMessage = e.localizedMessage
            )
        }
    }

    override fun getCocktailsByName(name: String): Flow<List<Cocktail>>  = flow {

        try {

            val response = api.apiCocktailService.getCocktailsByName(name)

            if (!response.isSuccessful) {
                throw ApiError(
                    type = ApiErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw ApiError(
                type = ApiErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.map { it.toDomain() })
        } catch (e: ApiError) {

            throw e
        } catch (e: Exception) {

            throw ApiError(
                type = ApiErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }
}
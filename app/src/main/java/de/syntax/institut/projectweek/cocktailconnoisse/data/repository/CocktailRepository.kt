package de.syntax.institut.projectweek.cocktailconnoisse.data.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.toDomain
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.lang.Error
import kotlin.collections.map

class CocktailRepository(

    private val api: ApiCocktail,
    private val cocktailDao: CocktailDao
) : CocktailRepositoryInterface {

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

            val cachedCocktail = cocktailDao.getCachedCocktailById(id).firstOrNull()
            if (cachedCocktail != null) {
                emit(cachedCocktail)
                return@flow
            }

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

            val cocktail = dtoResponse.toDomain()
            cocktailDao.insertCachedCocktailWithIngredients(
                cocktail,
                cocktail.ingredients.map { it.copy(cocktailId = cocktail.id) }
            )
            emit(cocktail)
        } catch (e: ApiError) {

            throw e
        } catch (e: Exception) {

            throw ApiError(
                type = ApiErrorType.RESPONSE_FAILED, innerMessage = e.localizedMessage
            )
        }
    }

    override fun getCocktailsByName(name: String): Flow<List<Cocktail>> = flow {

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

    override fun getAllCategories(): Flow<List<Category>> = flow {

        try {

            val response = api.apiCocktailService.getAllCategories()

            if (!response.isSuccessful) {
                throw ApiError(
                    type = ApiErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.category ?: throw ApiError(
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

    override suspend fun insertCachedCocktailWithIngredients(
        cocktail: Cocktail,
        ingredients: List<Ingredient>
    ) {

        cocktailDao.insertCachedCocktailWithIngredients(cocktail, ingredients)
    }

    override fun getCachedCocktails(): Flow<List<Cocktail>> = flow {

        try {

            cocktailDao.getCachedCocktails().collect { cocktails ->
                if (cocktails.isEmpty()) {
                    throw ApiError(
                        type = ApiErrorType.PERSISTENCE_FAILED,
                        innerMessage = "api_error_persistences"
                    )
                }
                emit(cocktails)
            }
        } catch (e: Exception) {

            if (e is ApiError) {
                throw e
            }
            throw ApiError(
                type = ApiErrorType.PERSISTENCE_FAILED,
                innerMessage = e.localizedMessage
            )
        }
    }

    override fun getCachedCocktailById(id: String): Flow<Cocktail> = flow {

        try {

            cocktailDao.getCachedCocktailById(id).map { cocktailNullable ->
                val cocktail = cocktailNullable ?: throw ApiError(
                        type = ApiErrorType.PERSISTENCE_FAILED,
                        innerMessage = "api_error_persistence"
                    )
                emit(cocktail)
            }
        } catch (e: Exception) {

            if (e is ApiError) {
                throw e
            }
            throw ApiError(
                type = ApiErrorType.PERSISTENCE_FAILED,
                innerMessage = e.localizedMessage
            )
        }
    }

    override suspend fun updateCachedCocktail(cocktail: Cocktail) {

        cocktailDao.updateCachedCocktail(cocktail)
    }

    override suspend fun deleteCachedCocktail(cocktail: Cocktail) {

        cocktailDao.deleteCachedCocktail(cocktail)
    }

    override suspend fun truncateCache() {

        cocktailDao.truncateCache()
    }
}
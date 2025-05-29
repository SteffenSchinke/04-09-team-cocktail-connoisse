package de.syntax.institut.projectweek.cocktailconnoisse.data.repository

import android.util.Log
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.extension.toDomain
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao.CategoryDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao.IngredientDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.enum.CocktailType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CocktailRepository(

    private val cocktailApi: ApiCocktail,
    private val cocktailDao: CocktailDao,
    private val categoryDao: CategoryDao,
    private val ingredientDao: IngredientDao
) : CocktailRepositoryInterface {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {

        scope.launch {
            buildCategoryCash()
        }
    }

    fun isCacheEmpty(): Flow<Boolean> = combine(
        cocktailDao.getCocktailCount(),
        ingredientDao.getIngredientCount(),
    ) { cocktailCount, ingredientCount ->
        cocktailCount == 0 && ingredientCount == 0
    }.catch { e ->
        throw RepositoryError(
            type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
            innerMessage = e.localizedMessage ?: "api_error_unknown"
        )
    }

    override fun getCategories(): Flow<List<Category>> = categoryDao.getCategories()
        .catch { e ->
            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage
            )
        }

    override fun getCocktail(): Flow<Cocktail?> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getCocktail()
            if (!response.isSuccessful) {
                throw RepositoryError(
                    type = RepositoryErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse =
                response.body()?.cocktails?.firstOrNull() ?: throw RepositoryError(
                    type = RepositoryErrorType.PARSING_FAILED,
                    innerMessage = "api_error_parse"
                )

            val cocktail = dtoResponse.toDomain()
            cocktailDao.insertCocktail(cocktail)
            ingredientDao.insertIngredients(cocktail.ingredients)

            emit(cocktail)
        } catch (e: RepositoryError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage
            )
        }
    }

    override fun getCocktail(id: Long): Flow<Cocktail?> = flow {

        try {

            val cocktailFlow = cocktailDao.getCocktail(id.toString()).first()
            cocktailFlow?.let {
                val cocktail = it.cocktail.apply {
                    ingredients = it.ingredients
                }
                emit(cocktail)
                return@flow
            }

            val response = cocktailApi.apiCocktailService.getCocktailById(id.toString())
            if (!response.isSuccessful) {
                throw RepositoryError(
                    type = RepositoryErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val cocktailDto =
                response.body()?.cocktails?.firstOrNull() ?: throw RepositoryError(
                    type = RepositoryErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
                )
            val cocktail = cocktailDto.toDomain()

            if (cocktail.instructions != null && cocktail.modifiedAt != null) {
                cocktailDao.insertCocktail(cocktail)
                ingredientDao.insertIngredients(cocktail.ingredients)
            }

            emit(cocktail)
        } catch (e: RepositoryError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override fun getCocktails(listIds: List<Long>): Flow<List<Cocktail>> = flow {

        // TODO sts 29.0525 - implement
        emit(emptyList<Cocktail>())
    }

    override fun getCocktails(count: Int): Flow<List<Cocktail>> = flow {

        try {

            val cocktailCount = cocktailDao.getCocktailCount().first()
            if (cocktailCount >= count) {
                val cocktails = cocktailDao.getCocktails(count).first()
                val emit = cocktails.map { cocktail ->
                    cocktail.cocktail.apply {
                        ingredients = cocktail.ingredients
                    }
                }
                emit(emit)
                return@flow
            }

            val cocktails: MutableList<Cocktail> = mutableListOf()
            repeat(count) {

                val response = cocktailApi.apiCocktailService.getCocktail()
                if (!response.isSuccessful) {
                    throw RepositoryError(
                        type = RepositoryErrorType.RESPONSE_FAILED,
                        responseCode = response.code(),
                        innerMessage = "api_error_response"
                    )
                }

                val dtoResponse = response.body()?.cocktails ?: throw RepositoryError(
                    type = RepositoryErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
                )

                val cocktail = dtoResponse.firstOrNull()?.toDomain() ?: throw RepositoryError(
                    type = RepositoryErrorType.RESPONSE_FAILED, innerMessage = "api_error_parse"
                )

                cocktailDao.insertCocktail(cocktail)
                ingredientDao.insertIngredients(cocktail.ingredients)
                cocktails.add(cocktail)
            }

            emit(cocktails)
        } catch (e: RepositoryError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override fun getCocktails(type: CocktailType): Flow<List<Cocktail>> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getCocktailsByType(type.toUrlArgument)

            if (!response.isSuccessful) {
                throw RepositoryError(
                    type = RepositoryErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw RepositoryError(
                type = RepositoryErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.map { it.toDomain() })
        } catch (e: RepositoryError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override fun getCocktails(count: Int, type: CocktailType): Flow<List<Cocktail>> = flow {

        try {

            val cocktailCount = cocktailDao.getCocktailCount().first()
            if (cocktailCount >= count) {
                val cocktails = cocktailDao.getCocktails(count).first()
                val emit = cocktails.map { cocktail ->
                    cocktail.cocktail.apply {
                        ingredients = cocktail.ingredients
                    }
                }
                emit(emit)
                return@flow
            }

            val response = cocktailApi.apiCocktailService.getCocktailsByType(type.toUrlArgument)
            if (!response.isSuccessful) {
                throw RepositoryError(
                    type = RepositoryErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw RepositoryError(
                type = RepositoryErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            val listCocktail: MutableList<Cocktail> = mutableListOf()
            dtoResponse.take(count).forEachIndexed { index, it ->

                val cocktail = it.toDomain()
                cocktailDao.insertCocktail(cocktail)
                ingredientDao.insertIngredients(cocktail.ingredients)
                listCocktail.add(cocktail)
            }

            emit(listCocktail)
        } catch (e: RepositoryError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override fun getCocktails(name: String): Flow<List<Cocktail>> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getCocktailsByName(name)

            if (!response.isSuccessful) {
                throw RepositoryError(
                    type = RepositoryErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw RepositoryError(
                type = RepositoryErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.map { it.toDomain() })
        } catch (e: RepositoryError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override fun getCocktails(category: Category): Flow<List<Cocktail>> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getCocktailsByCategory(category.name)

            if (!response.isSuccessful) {
                throw RepositoryError(
                    type = RepositoryErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw RepositoryError(
                type = RepositoryErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            val listCocktail: MutableList<Cocktail> = mutableListOf()
            dtoResponse.map { it ->

                val cocktail = it.toDomain()

                cocktailDao.insertCocktail(cocktail)
                ingredientDao.insertIngredients(cocktail.ingredients)

                listCocktail.add(cocktail)
            }
            emit(listCocktail)
        } catch (e: RepositoryError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override suspend fun updateCocktail(cocktail: Cocktail) {

        try {

            cocktailDao.updateCocktail(cocktail)
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage ?: "api_error_unknown"
            )
        }
    }

    override suspend fun deleteCocktail(cocktail: Cocktail) {

        try {

            cocktailDao.deleteCocktail(cocktail)
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage ?: "api_error_unknown"
            )
        }
    }

    override fun getFavorites(): Flow<List<Cocktail>> =
        cocktailDao.getFavorites()
            .map { cocktails ->
                if (cocktails.isEmpty()) {
                    throw RepositoryError(
                        type = RepositoryErrorType.PERSISTENCE_FAILED,
                        innerMessage = "api_error_response"
                    )
                }
                cocktails.map {
                    it.cocktail.apply { ingredients = it.ingredients }
                }
            }
            .catch { e ->
                RepositoryError(
                    type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                    innerMessage = e.localizedMessage ?: "api_error_unknown"
                )
            }

    override suspend fun insertCocktail(cocktail: Cocktail) {

        try {

            cocktailDao.insertCocktail(cocktail)
            ingredientDao.insertIngredients(cocktail.ingredients)
        } catch (e: Exception) {

            throw RepositoryError(
                type = RepositoryErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage ?: "api_error_unknown"
            )
        }
    }

    fun clearCache() {

        scope.launch {
            cocktailDao.truncateCocktail()
            ingredientDao.truncateIngredients()
            categoryDao.truncateCategory()

            buildCategoryCash()
        }
    }

    private suspend fun buildCategoryCash() {

        try {

            val categoryCount = categoryDao.getCategoryCount().first()
            if (categoryCount == 0) {

                Log.d("CocktailRepository::init", "Start categories caching")

                val response = cocktailApi.apiCocktailService.getCategories()
                if (!response.isSuccessful) {
                    throw RepositoryError(
                        type = RepositoryErrorType.RESPONSE_FAILED,
                        responseCode = response.code(),
                        innerMessage = "api_error_response"
                    )
                }

                val dtoResponse = response.body()?.category ?: throw RepositoryError(
                    type = RepositoryErrorType.PARSING_FAILED,
                    innerMessage = "api_error_parse"
                )

                categoryDao.insertCategories(dtoResponse.map { it.toDomain() })

                Log.d("CocktailRepository::int", "${dtoResponse.count()} Categories cached")
            } else {

                Log.d("CocktailRepository::init", "$categoryCount Categories already cached")
            }
        } catch (e: Exception) {

            Log.e("CocktailRepository", "Error during initialization", e)
        }
    }
}


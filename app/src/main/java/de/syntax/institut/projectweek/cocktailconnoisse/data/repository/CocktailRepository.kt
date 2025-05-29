package de.syntax.institut.projectweek.cocktailconnoisse.data.repository

import android.util.Log
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryOperationError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryOperationErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.toDomain
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CategoryDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import de.syntax.institut.projectweek.cocktailconnoisse.enum.CocktailType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CocktailRepository(

    private val cocktailApi: ApiCocktail,
    private val cocktailDao: CocktailDao,
    private val categoryDao: CategoryDao
) : CocktailRepositoryInterface {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {

        scope.launch {
            buildCategoryCash()
        }
    }


    fun isCacheEmpty(): Flow<Boolean> = combine(
        cocktailDao.getCocktailCount(),
        cocktailDao.getIngredientCount(),
        categoryDao.getCategoryCount()
    ) { cocktailCount, ingredientCount, categoryCount ->
        cocktailCount == 0 && ingredientCount == 0 && categoryCount == 0
    }.catch { e ->
        throw RepositoryOperationError(
            type = RepositoryOperationErrorType.PERSISTENCE_OPERATION_FAILED,
            innerMessage = e.localizedMessage ?: "api_error_unknown"
        )
    }

    override fun getCategories(): Flow<List<Category>> = categoryDao.getCategories()

    override suspend fun getCocktails(listIds: List<Long>): Flow<List<Cocktail>> = flow {

        // TODO sts 29.0525 - implement
        emit(emptyList<Cocktail>())
    }

    override suspend fun getCocktails(count: Long): Flow<List<Cocktail>> = flow {

        // TODO sts 29.0525 - implement
        emit(emptyList<Cocktail>())
    }

    override suspend fun getCocktails(type: CocktailType): Flow<List<Cocktail>> = flow {

        // TODO sts 29.0525 - implement
        emit(emptyList<Cocktail>())
    }

    override suspend fun getCocktails(name: String): Flow<List<Cocktail>> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getCocktailsByName(name)

            if (!response.isSuccessful) {
                throw RepositoryOperationError(
                    type = RepositoryOperationErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw RepositoryOperationError(
                type = RepositoryOperationErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.map { it.toDomain() })
        } catch (e: RepositoryOperationError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override suspend fun getCocktails(category: Category): Flow<List<Cocktail>> = flow {

        // TODO sts 29.0525 - implement
        emit(emptyList<Cocktail>())
    }

    override suspend fun getCocktail(id: Long): Flow<Cocktail?> = flow {

        val cocktailFlow = cocktailDao.getCocktailById(id.toString()).first()
        cocktailFlow?.let {
            val cocktail = it.cocktail.apply {
                ingredients = it.ingredients
            }
            emit(cocktail)
            return@flow
        }

        val response = cocktailApi.apiCocktailService.getCocktailById(id.toString())
        if (!response.isSuccessful) {
            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.RESPONSE_FAILED,
                responseCode = response.code(),
                innerMessage = "api_error_response"
            )
        }

        val cocktailDto =
            response.body()?.cocktails?.firstOrNull() ?: throw RepositoryOperationError(
                type = RepositoryOperationErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )
        val cocktail = cocktailDto.toDomain()

        if (cocktail.instructions != null && cocktail.modifiedAt != null) {
            cocktailDao.insertCachedCocktailWithIngredients(cocktail, cocktail.ingredients)
        }

        emit(cocktail)
    }

    override suspend fun updateCocktail(cocktail: Cocktail) {

        try {

            cocktailDao.updateCocktail(cocktail)
        } catch (e: Exception) {

            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage ?: "api_error_unknown"
            )
        }
    }

    override suspend fun deleteCocktail(cocktail: Cocktail) {

        try {

            cocktailDao.deleteCocktail(cocktail)
        } catch (e: Exception) {

            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.PERSISTENCE_OPERATION_FAILED,
                innerMessage = e.localizedMessage ?: "api_error_unknown"
            )
        }
    }

    override fun getFavorites(): Flow<List<Cocktail>> =
        cocktailDao.getFavorites()
            .map { cocktails ->
                if (cocktails.isEmpty()) {
                    throw RepositoryOperationError(
                        type = RepositoryOperationErrorType.PERSISTENCE_FAILED,
                        innerMessage = "api_error_response"
                    )
                }
                cocktails.map {
                    it.cocktail.apply { ingredients = it.ingredients }
                }
            }
            .catch { e ->
                when (e) {
                    is RepositoryOperationError -> {
                        RepositoryOperationError(
                            type = RepositoryOperationErrorType.PERSISTENCE_FAILED,
                            innerMessage = e.localizedMessage ?: "api_error_unknown"
                        )
                    }

                    else -> throw e
                }
            }


    override fun getRandomCocktail(): Flow<Cocktail?> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getRandomCocktail()

            if (!response.isSuccessful) {
                throw RepositoryOperationError(
                    type = RepositoryOperationErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse =
                response.body()?.cocktails?.firstOrNull() ?: throw RepositoryOperationError(
                    type = RepositoryOperationErrorType.PARSING_FAILED,
                    innerMessage = "api_error_parse"
                )

            emit(dtoResponse.toDomain())
        } catch (e: RepositoryOperationError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage
            )
        }
    }

    override fun getCocktailsByType(type: String): Flow<List<Cocktail>> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getCocktailsByType(type)

            if (!response.isSuccessful) {
                throw RepositoryOperationError(
                    type = RepositoryOperationErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw RepositoryOperationError(
                type = RepositoryOperationErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            emit(dtoResponse.map { it.toDomain() })
        } catch (e: RepositoryOperationError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }

    override fun getCocktailById(id: String): Flow<Cocktail?> = flow {
        try {

            val cached = cocktailDao.getCachedCocktailById(id).firstOrNull()

            if (cached != null) {
                val cocktail = cached.cocktail.apply {
                    ingredients = cached.ingredients
                }
                emit(cocktail)
            } else {

                val response = cocktailApi.apiCocktailService.getCocktailById(id)

                if (!response.isSuccessful) {
                    throw RepositoryOperationError(
                        type = RepositoryOperationErrorType.RESPONSE_FAILED,
                        responseCode = response.code(),
                        innerMessage = "api_error_response"
                    )
                }

                val dtoResponse =
                    response.body()?.cocktails?.firstOrNull() ?: throw RepositoryOperationError(
                        type = RepositoryOperationErrorType.PARSING_FAILED,
                        innerMessage = "api_error_parse"
                    )

                val cocktail = dtoResponse.toDomain()
//                cocktailDao.insertCachedCocktailWithIngredients(
//                    cocktail,
//                    cocktail.ingredients.map { it.copy(cocktailId = cocktail.id) }
//                )

                emit(cocktail)
            }
        } catch (e: RepositoryOperationError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage
            )
        }
    }

//
//    override fun getCocktailsByName(name: String): Flow<List<Cocktail>> = flow {
//
//        try {
//
//            val response = cocktailApi.apiCocktailService.getCocktailsByName(name)
//
//            if (!response.isSuccessful) {
//                throw RepositoryOperationError(
//                    type = RepositoryOperationErrorType.RESPONSE_FAILED,
//                    responseCode = response.code(),
//                    innerMessage = "api_error_response"
//                )
//            }
//
//            val dtoResponse = response.body()?.cocktails ?: throw RepositoryOperationError(
//                type = RepositoryOperationErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
//            )
//
//            emit(dtoResponse.map { it.toDomain() })
//        } catch (e: RepositoryOperationError) {
//
//            throw e
//        } catch (e: Exception) {
//
//            throw RepositoryOperationError(
//                type = RepositoryOperationErrorType.RESPONSE_FAILED,
//                innerMessage = e.localizedMessage,
//            )
//        }
//    }
//
//    override fun getAllCategories(): Flow<List<Category>> = flow {
//
//        try {
//
//            val response = cocktailApi.apiCocktailService.getAllCategories()
//
//            if (!response.isSuccessful) {
//                throw ApiError(
//                    type = ApiErrorType.RESPONSE_FAILED,
//                    responseCode = response.code(),
//                    innerMessage = "api_error_response"
//                )
//            }
//
//            val dtoResponse = response.body()?.category ?: throw ApiError(
//                type = ApiErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
//            )
//
//            emit(dtoResponse.map { it.toDomain() })
//        } catch (e: ApiError) {
//
//            throw e
//        } catch (e: Exception) {
//
//            throw ApiError(
//                type = ApiErrorType.RESPONSE_FAILED,
//                innerMessage = e.localizedMessage,
//            )
//        }
//    }

    override fun getCocktailsByCategory(category: String): Flow<List<Cocktail>> = flow {

        try {

            val response = cocktailApi.apiCocktailService.getCocktailsByCategory(category)

            if (!response.isSuccessful) {
                throw RepositoryOperationError(
                    type = RepositoryOperationErrorType.RESPONSE_FAILED,
                    responseCode = response.code(),
                    innerMessage = "api_error_response"
                )
            }

            val dtoResponse = response.body()?.cocktails ?: throw RepositoryOperationError(
                type = RepositoryOperationErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
            )

            val listCocktail: MutableList<Cocktail> = mutableListOf()
            dtoResponse.map { it ->

                val cocktail = it.toDomain()

//                cocktailDao.insertCachedCocktailWithIngredients(cocktail, cocktail.ingredients)

                listCocktail.add(cocktail)
            }
            emit(listCocktail)
        } catch (e: RepositoryOperationError) {

            throw e
        } catch (e: Exception) {

            throw RepositoryOperationError(
                type = RepositoryOperationErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage,
            )
        }
    }


    override suspend fun insertCachedCocktailWithIngredients(
        cocktail: Cocktail,
        ingredients: List<Ingredient>
    ) {

//        cocktailDao.insertCachedCocktailWithIngredients(cocktail, ingredients)
    }


    fun clearCache() {

        scope.launch {
            cocktailDao.clearCachedCocktails()
            cocktailDao.clearCachedIngredients()
            categoryDao.clearCache()

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
                    throw RepositoryOperationError(
                        type = RepositoryOperationErrorType.RESPONSE_FAILED,
                        responseCode = response.code(),
                        innerMessage = "api_error_response"
                    )
                }

                val dtoResponse = response.body()?.category ?: throw RepositoryOperationError(
                    type = RepositoryOperationErrorType.PARSING_FAILED,
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


//    override fun getCachedCocktails(): Flow<List<Cocktail>> = flow {
//
//        try {
//            cocktailDao.getCachedCocktails().collect { cocktailWithIngredients ->
//                if (cocktailWithIngredients.isEmpty()) {
//                    throw ApiError(
//                        type = ApiErrorType.PERSISTENCE_FAILED,
//                        innerMessage = "api_error_persistences"
//                    )
//                }
//
//                val cocktails = cocktailWithIngredients.map {
//                    it.cocktail.apply { ingredients = it.ingredients }
//                }
//
//                emit(cocktails)
//            }
//        } catch (e: Exception) {
//
//            if (e is ApiError) {
//                throw e
//            }
//            throw ApiError(
//                type = ApiErrorType.PERSISTENCE_FAILED,
//                innerMessage = e.localizedMessage
//            )
//        }
//    }

    //    override fun getCachedCocktailById(id: String): Flow<Cocktail> = flow {
//
//        try {
//
//            cocktailDao.getCachedCocktailById(id).collect { cocktailWithIngredient ->
//                val cocktailNotNull = cocktailWithIngredient ?: throw ApiError(
//                    type = ApiErrorType.PERSISTENCE_FAILED,
//                    innerMessage = "api_error_persistence"
//                )
//
//                val cocktail =
//                    cocktailNotNull.cocktail.apply { ingredients = cocktailNotNull.ingredients }
//
//                emit(cocktail)
//            }
//        } catch (e: Exception) {
//
//            if (e is ApiError) {
//                throw e
//            }
//            throw ApiError(
//                type = ApiErrorType.PERSISTENCE_FAILED,
//                innerMessage = e.localizedMessage
//            )
//        }
//    }
//
//    override fun getCocktailsByIngredient(ingredient: String): Flow<List<Cocktail>> = flow {
//        val response = cocktailApi.apiCocktailService.getCocktailsByIngredient(ingredient)
//
//        if (response.isSuccessful) {
//            val shortCocktails = response.body()?.cocktails ?: emptyList()
//            val detailedCocktails = mutableListOf<Cocktail>()
//
//            shortCocktails.forEach { shortDto ->
//                val detailResponse = cocktailApi.apiCocktailService.getCocktailById(shortDto.id)
//                if (detailResponse.isSuccessful) {
//                    val detailDto = detailResponse.body()?.cocktails?.firstOrNull()
//                    if (detailDto != null) {
//                        val cocktail = Cocktail(
//                            id = detailDto.id.toLongOrNull() ?: 0L,
//                            name = detailDto.name ?: "",
//                            category = detailDto.category ?: "",
//                            instructions = detailDto.instructions ?: "",
//                            imageUrl = detailDto.imageUrl ?: "",
//                            favorited = false,
//                            isAlcoholic = detailDto.alcoholic?.contains("Alcoholic") == true,
//                            modifiedAt = detailDto.modifiedAt,
//                            createdAt = "",
//                        ).apply {
//                            ingredients = buildIngredientsList(detailDto)
//                        }
//                        detailedCocktails.add(cocktail)
//                    }
//                }
//            }
//
//            emit(detailedCocktails)
//        } else {
//            emit(emptyList())
//        }
//    }
//
//    override fun getCategories(): Flow<List<Category>> = flow {
//
//        try {
//
//            val response = cocktailApi.apiCocktailService.getCategories()
//            if (!response.isSuccessful) {
//                throw RepositoryOperationError(
//                    type = RepositoryOperationErrorType.RESPONSE_FAILED,
//                    responseCode = response.code(),
//                    innerMessage = "api_error_response"
//                )
//            }
//
//            val dtoResponse = response.body()?.category ?: throw RepositoryOperationError(
//                type = RepositoryOperationErrorType.PARSING_FAILED, innerMessage = "api_error_parse"
//            )
//
//            emit(dtoResponse.map { it.toDomain() })
//        } catch (e: RepositoryOperationError) {
//
//            throw e
//        } catch (e: Exception) {
//
//            throw RepositoryOperationError(
//                type = RepositoryOperationErrorType.RESPONSE_FAILED,
//                innerMessage = e.localizedMessage,
//            )
//        }
//    }
//
//    fun buildIngredientsList(dto: CocktailDto): List<Ingredient> {
//        val ingredients = mutableListOf<Ingredient>()
//
//        val ingredientNames = listOf(
//            dto.ingredient1, dto.ingredient2, dto.ingredient3, dto.ingredient4, dto.ingredient5,
//            dto.ingredient6, dto.ingredient7, dto.ingredient8, dto.ingredient9, dto.ingredient10,
//            dto.ingredient11, dto.ingredient12, dto.ingredient13, dto.ingredient14, dto.ingredient15
//        )
//
//        val measures = listOf(
//            dto.measure1, dto.measure2, dto.measure3, dto.measure4, dto.measure5,
//            dto.measure6, dto.measure7, dto.measure8, dto.measure9
//        )
//
//        ingredientNames.forEachIndexed { index, name ->
//            if (!name.isNullOrEmpty()) {
//                ingredients.add(
//                    Ingredient(
//                        id = 0L,
//                        cocktailId = dto.id.toLongOrNull() ?: 0L,
//                        name = name,
//                        measure = measures.getOrNull(index)
//                    )
//                )
//            }
//        }
//
//        return ingredients
//    }
}


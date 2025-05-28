package de.syntax.institut.projectweek.cocktailconnoisse.data.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.CocktailDto
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.toDomain
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CocktailRepository(

    private val api: ApiCocktail,
    private val cocktailDao: CocktailDao
) : CocktailRepositoryInterface {

    override fun isCacheEmpty(): Flow<Boolean> = combine(
        cocktailDao.getCocktailCount(),
        cocktailDao.getIngredientCount()
    ) { cocktailCount, ingredientCount ->
        cocktailCount == 0 && ingredientCount == 0
    }

//    override fun getAllFavorites(): Flow<List<Cocktail>> = flow {
//        try {
//
//            cocktailDao.getAllFavorites().collect{ cocktails ->
//                if (cocktails.isEmpty()) {
//                    throw ApiError(
//                        type = ApiErrorType.PERSISTENCE_FAILED,
//                        innerMessage = "api_error_response"
//                    )
//                }
//                val favorites = cocktails.map {
//                    it.cocktail.apply { ingredients = it.ingredients }
//                }
//                emit(favorites)
//            }
//
//        } catch (e: ApiError) {
//
//            throw e
//        } catch (e: Exception) {
//
//            throw ApiError(
//                type = ApiErrorType.PERSISTENCE_FAILED,
//                innerMessage = e.localizedMessage,
//            )
//        }
//    }

    override fun getAllFavorites(): Flow<List<Cocktail>> =
        cocktailDao.getAllFavorites()
            .map { cocktails ->
                if (cocktails.isEmpty()) {
                    throw ApiError(
                        type = ApiErrorType.PERSISTENCE_FAILED,
                        innerMessage = "api_error_response"
                    )
                }
                cocktails.map {
                    it.cocktail.apply { ingredients = it.ingredients }
                }
            }
            .catch { e ->
                when (e) {
                    is ApiError -> {
                        ApiError(
                            type = ApiErrorType.PERSISTENCE_FAILED,
                            innerMessage = e.localizedMessage ?: "api_error_unknown"
                        )
                    }
                    else -> throw e
                }
            }

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

            val cached = cocktailDao.getCachedCocktailById(id).firstOrNull()

            if (cached != null) {
                val cocktail = cached.cocktail.apply {
                    ingredients = cached.ingredients
                }
                emit(cocktail)
            } else {

                val response = api.apiCocktailService.getCocktailById(id)

                if (!response.isSuccessful) {
                    throw ApiError(
                        type = ApiErrorType.RESPONSE_FAILED,
                        responseCode = response.code(),
                        innerMessage = "api_error_response"
                    )
                }

                val dtoResponse = response.body()?.cocktails?.firstOrNull() ?: throw ApiError(
                    type = ApiErrorType.PARSING_FAILED,
                    innerMessage = "api_error_parse"
                )

                val cocktail = dtoResponse.toDomain()
                cocktailDao.insertCachedCocktailWithIngredients(
                    cocktail,
                    cocktail.ingredients.map { it.copy(cocktailId = cocktail.id) }
                )

                emit(cocktail)
            }
        } catch (e: ApiError) {

            throw e
        } catch (e: Exception) {

            throw ApiError(
                type = ApiErrorType.RESPONSE_FAILED,
                innerMessage = e.localizedMessage
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

    override fun getCocktailsByCategory(category: String): Flow<List<Cocktail>> = flow {

        try {

            val response = api.apiCocktailService.getCocktailsByCategory(category)

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

            val listCocktail: MutableList<Cocktail> = mutableListOf()
            dtoResponse.map { it ->

                val cocktail = it.toDomain()

                cocktailDao.insertCachedCocktailWithIngredients(cocktail, cocktail.ingredients)

                listCocktail.add(cocktail)
            }
            emit(listCocktail)
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

        try
        {
            cocktailDao.getCachedCocktails().collect { cocktailWithIngredients ->
                if (cocktailWithIngredients.isEmpty()) {
                    throw ApiError(
                        type = ApiErrorType.PERSISTENCE_FAILED,
                        innerMessage = "api_error_persistences"
                    )
                }

                val cocktails = cocktailWithIngredients.map {
                    it.cocktail.apply { ingredients = it.ingredients }
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

            cocktailDao.getCachedCocktailById(id).collect { cocktailWithIngredient ->
                val cocktailNotNull = cocktailWithIngredient ?: throw ApiError(
                    type = ApiErrorType.PERSISTENCE_FAILED,
                    innerMessage = "api_error_persistence"
                )

                val cocktail =
                    cocktailNotNull.cocktail.apply { ingredients = cocktailNotNull.ingredients }

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

    override fun getCocktailsByIngredient(ingredient: String): Flow<List<Cocktail>> = flow {
        val response = api.apiCocktailService.getCocktailsByIngredient(ingredient)

        if (response.isSuccessful) {
            val shortCocktails = response.body()?.cocktails ?: emptyList()
            val detailedCocktails = mutableListOf<Cocktail>()

            shortCocktails.forEach { shortDto ->
                val detailResponse = api.apiCocktailService.getCocktailById(shortDto.id)
                if (detailResponse.isSuccessful) {
                    val detailDto = detailResponse.body()?.cocktails?.firstOrNull()
                    if (detailDto != null) {
                        val cocktail = Cocktail(
                            id = detailDto.id.toLongOrNull() ?: 0L,
                            name = detailDto.name ?: "",
                            category = detailDto.category ?: "",
                            instructions = detailDto.instructions ?: "",
                            imageUrl = detailDto.imageUrl ?: "",
                            favorited = false,
                            isAlcoholic = detailDto.alcoholic?.contains("Alcoholic") == true,
                            modifiedAt = detailDto.modifiedAt,
                            createdAt = "",
                        ).apply {
                            ingredients = buildIngredientsList(detailDto)
                        }
                        detailedCocktails.add(cocktail)
                    }
                }
            }

            emit(detailedCocktails)
        } else {
            emit(emptyList())
        }
    }

}

fun buildIngredientsList(dto: CocktailDto): List<Ingredient> {
    val ingredients = mutableListOf<Ingredient>()

    val ingredientNames = listOf(
        dto.ingredient1, dto.ingredient2, dto.ingredient3, dto.ingredient4, dto.ingredient5,
        dto.ingredient6, dto.ingredient7, dto.ingredient8, dto.ingredient9, dto.ingredient10,
        dto.ingredient11, dto.ingredient12, dto.ingredient13, dto.ingredient14, dto.ingredient15
    )

    val measures = listOf(
        dto.measure1, dto.measure2, dto.measure3, dto.measure4, dto.measure5,
        dto.measure6, dto.measure7, dto.measure8, dto.measure9
    )

    ingredientNames.forEachIndexed { index, name ->
        if (!name.isNullOrEmpty()) {
            ingredients.add(
                Ingredient(
                    id = 0L,
                    cocktailId = dto.id.toLongOrNull() ?: 0L,
                    name = name,
                    measure = measures.getOrNull(index)
                )
            )
        }
    }

    return ingredients
}
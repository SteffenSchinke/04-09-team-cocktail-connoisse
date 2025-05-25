package de.syntax.institut.projectweek.cocktailconnoisse.data.external

import de.schinke.steffen.base_classs.AppBaseClassApi
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.ResponseCocktailDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class ApiCocktail(

    baseUrl: String
) : AppBaseClassApi(baseUrl) {

    val apiCocktailService: ApiCocktailService = retrofit.create(ApiCocktailService::class.java)

    interface ApiCocktailService {

        @GET("random.php")
        suspend fun getRandomCocktail(): Response<ResponseCocktailDto>

        @GET("filter.php")
        suspend fun getCocktailsByType(@Query("a") type: String): Response<ResponseCocktailDto>

        @GET("lookup.php")
        suspend fun getCocktailById(@Query("i") id: String): Response<ResponseCocktailDto>

        @GET("search.php")
        suspend fun getCocktailsByName(@Query("s") name: String): Response<ResponseCocktailDto>
    }
}
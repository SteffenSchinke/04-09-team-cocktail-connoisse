package de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail

import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Cocktail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CocktailRepository(

    private val apiCocktail: ApiCocktail
): CocktailRepositoryInterface {

    override suspend fun getCocktails(): Flow<List<Cocktail>> {

        // TODO sts 23.05.25 - exception handling implement base of responce object retrofit2

        val response = apiCocktail.apiCocktailService.getCocktails()

        val cocktails = response.body()?.cocktails ?: emptyList()

        return flowOf(cocktails)
    }

}
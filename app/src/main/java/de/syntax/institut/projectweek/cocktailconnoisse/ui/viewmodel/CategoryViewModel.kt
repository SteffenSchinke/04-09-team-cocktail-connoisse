package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail.CocktailRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite.FavoriteCocktailRepository

class CategoryViewModel(

    private val cocktailRepository: CocktailRepository,
    private val favoriteCocktailRepository: FavoriteCocktailRepository,
    application: Application
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    // TODO sts 23.05.25 - implement viewmodel with filtering

}
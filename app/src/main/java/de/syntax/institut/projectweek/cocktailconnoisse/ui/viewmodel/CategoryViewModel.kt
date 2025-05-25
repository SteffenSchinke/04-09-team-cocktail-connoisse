package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.repository.CocktailApiRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.repository.CocktailDBRepositoryInterface

class CategoryViewModel(

    application: Application,
    private val cocktailApi: CocktailApiRepositoryInterface,
    private val cocktailDb: CocktailDBRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    // TODO sts 23.05.25 - implement viewmodel with filtering

}
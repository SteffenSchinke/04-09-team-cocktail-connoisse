package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface

class CocktailsViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {


}
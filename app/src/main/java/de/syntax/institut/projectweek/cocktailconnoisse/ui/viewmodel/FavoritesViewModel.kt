package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoritesViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _withAlcoholic = MutableStateFlow(true)
    val withAlcoholic: StateFlow<Boolean> = _withAlcoholic

    // TODO sts 23.05.25 - implement viewmodel database per repository


    fun setCocktailType(newValue: Boolean) {
        _withAlcoholic.value = newValue
    }
}
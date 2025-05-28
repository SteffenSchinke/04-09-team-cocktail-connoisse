package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails


    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    fun loadFavorites(): StateFlow<List<Cocktail>> {
/*
        if (state.value != ViewModelState.READY) return

        viewModelScope.launch {

            setState { ViewModelState.WORKING }
            try {

               cocktailRepo.getAllFavorites().collect { cocktails ->
                    _cocktails.value = cocktails
                }

                setState { ViewModelState.READY }

            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }

 */
        return cocktailRepo.getAllFavorites().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
        )
    }
    fun resetApiError() { _apiError.value = null }
}
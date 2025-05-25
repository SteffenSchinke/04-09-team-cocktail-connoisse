package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.repository.CocktailApiRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.repository.CocktailDBRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(

    application: Application,
    private val cocktailApi: CocktailApiRepositoryInterface,
    private val cocktailDb: CocktailDBRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    private val _isFavorited = MutableStateFlow(false)
    val isFavorited: StateFlow<Boolean> = _isFavorited

    fun loadCocktailById(id: String) {

        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        viewModelScope.launch {

            try {
                cocktailApi.getCocktailById(id).collect { cocktail ->
                    _cocktail.value = cocktail
                }

                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun updateIsFavorited() {

        _isFavorited.value = !_isFavorited.value

        _cocktail.value?.let {
            viewModelScope.launch {
                if (_isFavorited.value) {
                    cocktailDb.insertFavoritedCocktail(it)
                } else {
                    cocktailDb.deleteFavoritedCocktail(it)
                }
            }
        }
    }


    fun resetApiError() { _apiError.value = null }
}
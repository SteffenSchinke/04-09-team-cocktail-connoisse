package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(

    application: Application,
    cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    val cocktails = cocktailRepo.getAllFavorites()
        .onStart { ViewModelState.WORKING }
        .catch { e ->
            if( e is ApiError) {
                _apiError.value = e
            } else {
                _apiError.value = ApiError(
                    type = ApiErrorType.PERSISTENCE_FAILED,
                    innerMessage = e.localizedMessage ?: "api_error_unknown"
                )
            }
            setState { ViewModelState.ERROR }
        }
        .onEach { setState { ViewModelState.READY } }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList()
        )

    fun resetApiError() { _apiError.value = null }
}
package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    init {

        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            cocktailRepo.getAllFavorites()
                .onStart {
                    setState { ViewModelState.WORKING }
                }
                .catch { e ->
                    _apiError.value = if (e is ApiError) {
                        e
                    } else {
                        ApiError(
                            type = ApiErrorType.PERSISTENCE_FAILED,
                            innerMessage = e.localizedMessage ?: "api_error_unknown"
                        )
                    }
                    setState { ViewModelState.ERROR }
                    emit(emptyList()) // Damit collect() nicht abstürzt
                }
                .collect { result ->
                    _cocktails.value = result
                    setState { ViewModelState.READY }
                }
        }
    }

//    fun loadFavorites(): StateFlow<List<Cocktail>> {
//
//        val cocktails = cocktailRepo.getAllFavorites()
//            .onStart { ViewModelState.WORKING }
//            .catch { e ->
//                if (e is ApiError) {
//                    _apiError.value = e
//                } else {
//                    _apiError.value = ApiError(
//                        type = ApiErrorType.PERSISTENCE_FAILED,
//                        innerMessage = e.localizedMessage ?: "api_error_unknown"
//                    )
//                }
//                setState { ViewModelState.ERROR }
//            }
//            .onEach { setState { ViewModelState.READY } }
//            .stateIn(
//                viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList()
//            )
//
//        return cocktails
//    }

    fun resetApiError() {
        _apiError.value = null
    }

    fun updateIsFavorited(cocktail: Cocktail) {

        viewModelScope.launch {

            val newCocktail = cocktail.copy(favorited = !cocktail.favorited)
            cocktailRepo.updateCachedCocktail(newCocktail)

            cocktailRepo.getAllFavorites()
                .collect {
                    _cocktails.value = it
                    setState { ViewModelState.READY }
                }
        }
    }
}
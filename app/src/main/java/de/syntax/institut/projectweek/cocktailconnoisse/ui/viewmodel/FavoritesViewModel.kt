package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FavoritesViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _repoError = MutableStateFlow<RepositoryError?>(null)
    val repoError: StateFlow<RepositoryError?> = _repoError

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    init {

        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            cocktailRepo.getFavorites()
                .onStart {
                    setState { ViewModelState.WORKING }
                }
                .catch { e ->
                    _repoError.value = e as? RepositoryError
                        ?: RepositoryError(
                            type = RepositoryErrorType.PERSISTENCE_FAILED,
                            innerMessage = e.localizedMessage ?: "api_error_unknown"
                        )
                    setState { ViewModelState.ERROR }
                    emit(emptyList())
                }
                .collect { result ->
                    _cocktails.value = result
                    setState { ViewModelState.READY }
                }
        }
    }

    fun resetApiError() {
        _repoError.value = null
    }
}
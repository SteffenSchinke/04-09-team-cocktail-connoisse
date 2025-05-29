package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryOperationError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryOperationErrorType
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

    private val _repositoryOperationError = MutableStateFlow<RepositoryOperationError?>(null)
    val repositoryOperationError: StateFlow<RepositoryOperationError?> = _repositoryOperationError

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
                    _repositoryOperationError.value = e as? RepositoryOperationError
                        ?: RepositoryOperationError(
                            type = RepositoryOperationErrorType.PERSISTENCE_FAILED,
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
        _repositoryOperationError.value = null
    }
}
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

    private val _repoError = MutableStateFlow<RepositoryError?>(null)
    val repoError: StateFlow<RepositoryError?> = _repoError

    val cocktails = cocktailRepo.getFavorites()
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
        .onEach {
            setState { ViewModelState.READY }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun updateFavoriteState(cocktail: Cocktail) {

        viewModelScope.launch {

            val newCocktail = cocktail.copy(favorited = !cocktail.favorited)
            cocktailRepo.upsertCocktail(newCocktail)
        }
    }

    fun resetApiError() {

        _repoError.value = null
        setState { ViewModelState.READY }
    }
}
package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryOperationError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _repositoryOperationError = MutableStateFlow<RepositoryOperationError?>(null)
    val repositoryOperationError: StateFlow<RepositoryOperationError?> = _repositoryOperationError

    fun loadCocktailById(id: String) {

        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        viewModelScope.launch {

            try {
                cocktailRepo.getCocktail(id.toLong()).collect { cocktail ->
                    _cocktail.value = cocktail
                }

                setState { ViewModelState.READY }
            } catch (e: RepositoryOperationError) {

                _repositoryOperationError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun resetApiError() { _repositoryOperationError.value = null }
}
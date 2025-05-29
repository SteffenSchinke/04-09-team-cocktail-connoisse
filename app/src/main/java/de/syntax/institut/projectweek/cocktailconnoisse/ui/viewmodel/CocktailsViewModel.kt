package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CocktailsViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _repoError = MutableStateFlow<RepositoryError?>(null)
    val repoError: StateFlow<RepositoryError?> = _repoError

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    fun setCocktailIds(stringIds: String) {

        if (state.value != ViewModelState.READY || stringIds.isEmpty()) return

        setState { ViewModelState.WORKING }

        val listId = stringIds.split(",")
        viewModelScope.launch {

            try {

                val result: MutableList<Cocktail> = mutableListOf()
                listId.forEach { id ->
                    cocktailRepo.getCocktail(id.toLong()).collect { cocktail ->
                        cocktail?.let {
                            result.add(it)
                        }
                    }
                }

                _cocktails.value = result
                setState { ViewModelState.READY }
            } catch (e: RepositoryError) {

                _repoError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun resetApiError() { _repoError.value = null }
}
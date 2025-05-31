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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class DetailsViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    private val _repoError = MutableStateFlow<RepositoryError?>(null)
    val repoError: StateFlow<RepositoryError?> = _repoError

    fun loadCocktailById(id: String) {

        if (state.value != ViewModelState.READY) return

        viewModelScope.launch {

            setState { ViewModelState.WORKING }

            try {

                _cocktail.value= cocktailRepo.getCocktail(id.toLong()).first()

                _cocktail.value?.let { cocktail ->
                    val ingredient = cocktail.ingredients.firstOrNull()

                    ingredient?.let { ingredient ->
                        val result = cocktailRepo.getCocktails(ingredient, 5).first()
                        _cocktails.value = result.take(5)
                    }
                }

                setState { ViewModelState.READY }
            } catch (_: CancellationException) {
                // flow cancelled bei first() || firstOrNull()
            } catch (e: RepositoryError) {

                _repoError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun resetApiError() {
        _repoError.value = null
        setState { ViewModelState.READY }
    }
}
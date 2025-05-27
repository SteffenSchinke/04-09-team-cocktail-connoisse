package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CocktailsViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

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
                    cocktailRepo.getCocktailById(id).collect { cocktail ->
                        cocktail?.let {
                            result.add(it)

                            // todo repo
                            // TODO sts 26.05.25 - cache handling per time line ?!?!?
                            launch(Dispatchers.IO) {
                                cocktailRepo.insertCachedCocktailWithIngredients(it, it.ingredients)
                            }
                        }
                    }
                }

                _cocktails.value = result
                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun resetApiError() { _apiError.value = null }
}
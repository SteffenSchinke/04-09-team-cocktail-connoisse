package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.enum.CocktailType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private var _searchJob: Job? = null
    private val _repoError = MutableStateFlow<RepositoryError?>(null)
    val repoError: StateFlow<RepositoryError?> = _repoError

    private val _cocktailType = MutableStateFlow(CocktailType.ALCOHOLIC)
    val cocktailType: StateFlow<CocktailType> = _cocktailType

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    private val _searchedCocktails = mutableStateOf<List<Cocktail>>(emptyList())
    val searchedCocktails: MutableState<List<Cocktail>> = _searchedCocktails

    val withAlcoholic: StateFlow<Boolean> = cocktailType
        .map { it == CocktailType.ALCOHOLIC }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _cocktailType.value == CocktailType.ALCOHOLIC
        )

    var isSearching by mutableStateOf(false)
    var searchText by mutableStateOf("")

    fun loadCocktails() {

        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        viewModelScope.launch {

            try {

                cocktailRepo.getCocktail().collect { cocktail ->
                    _cocktail.value = cocktail
                }

                cocktailRepo.getCocktails(
                    count = 30,
                    type =
                        if (withAlcoholic.value) CocktailType.ALCOHOLIC
                        else CocktailType.NON_ALCOHOLIC).collect { cocktails ->
                    _cocktails.value = cocktails
                }

                setState { ViewModelState.READY }
            } catch (e: RepositoryError) {

                _repoError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun setCocktailType(newValue: Boolean) {

        if (newValue) _cocktailType.value = CocktailType.ALCOHOLIC
        else _cocktailType.value = CocktailType.NON_ALCOHOLIC

        loadCocktails()
    }

    fun resetApiError() { _repoError.value = null }

    fun searchCocktailsByName(name: String) {
        _searchJob?.cancel()
        _searchJob = viewModelScope.launch {
            cocktailRepo.getCocktails(name)
                .catch { e ->
                    _searchedCocktails.value = emptyList()
                }
                .collect { cocktails ->
                    val filtered = cocktails.filter { cocktail ->
                        if (withAlcoholic.value) cocktail.isAlcoholic
                        else !cocktail.isAlcoholic
                    }
                    _searchedCocktails.value = filtered
                }
        }
    }
}
package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.repository.CocktailApiRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.repository.CocktailDBRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CategoryViewModel(

    application: Application,
    private val cocktailApi: CocktailApiRepositoryInterface,
    private val cocktailDb: CocktailDBRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    // TODO sts 23.05.25 - implement viewmodel with filtering

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    init {

        loadCategories()
    }

    private fun loadCategories() {

        setState { ViewModelState.WORKING }

        try {

        _categories.value = cocktailApi.getAllCategories()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                emptyList()
            ).value

            setState { ViewModelState.READY }
        } catch (e: ApiError) {

            _apiError.value = e
            setState { ViewModelState.ERROR }
        }
    }
}
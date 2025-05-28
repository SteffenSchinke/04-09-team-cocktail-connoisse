package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class CategoriesViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    private val _cocktailsByCategory = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktailsByCategory: StateFlow<List<Cocktail>> = _cocktailsByCategory

    private val _hasNavigated = MutableStateFlow(false)
    val hasNavigated: StateFlow<Boolean> = _hasNavigated

    private val _navigatedCategory = MutableStateFlow("")
    val navigatedCategory: StateFlow<String> = _navigatedCategory

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    init {

        loadCategories()
    }

    private fun loadCategories() {

        setState { ViewModelState.WORKING }

        viewModelScope.launch {
            try {

                cocktailRepo.getAllCategories().collect { categories ->
                    _categories.value = categories
                }

                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun loadCocktailsFromCategory(category: Category) {

        setState { ViewModelState.WORKING }

        viewModelScope.launch {
            try {

                val urlArgument = category.toUrlArgument()
                cocktailRepo.getCocktailsByCategory(urlArgument).collect { cocktails ->
                    _cocktailsByCategory.value = cocktails
                    _navigatedCategory.value = category.name
                }

                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun resetApiError() { _apiError.value = null }

    fun setNavigationFlag() { _hasNavigated.value = true }

    fun resetNavigationFlag() {
        _hasNavigated.value = false
        _cocktailsByCategory.value = emptyList()
        _navigatedCategory.value = ""
    }


    fun loadCocktailsByIngredient(ingredient: String) {
        viewModelScope.launch {
            cocktailRepo.getCocktailsByIngredient(ingredient)
                .catch { e -> e.printStackTrace() }
                .collect { cocktails ->
                    _cocktails.value = cocktails
                }
        }
    }
}

package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryOperationError
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


class CategoriesViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {


    private val _error = MutableStateFlow<RepositoryOperationError?>(null)
    val error: StateFlow<RepositoryOperationError?> = _error

    private val _listForNavigationIds = MutableStateFlow<String?>(null)
    val listForNavigationIds: StateFlow<String?> = _listForNavigationIds

    private val _clickedCategory = MutableStateFlow<Category?>(null)
    val clickedCategory: StateFlow<Category?> = _clickedCategory

    val categories: StateFlow<List<Category>> =
        cocktailRepo.getCategories()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                emptyList()
            )

//    private val _cocktailsByCategory = MutableStateFlow<List<Cocktail>>(emptyList())
//    val cocktailsByCategory: StateFlow<List<Cocktail>> = _cocktailsByCategory
//
//    private val _hasNavigated = MutableStateFlow(false)
//    val hasNavigated: StateFlow<Boolean> = _hasNavigated
//
//    private val _navigatedCategory = MutableStateFlow("")
//    val navigatedCategory: StateFlow<String> = _navigatedCategory

//    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
//    val cocktails: StateFlow<List<Cocktail>> = _cocktails


    fun onCategoryClick(category: Category) {

        _clickedCategory.value = category

        Log.d("CategoriesViewModel", "navigateToCocktails: $category")
    }

    fun resetApiError() { _error.value = null }


//    init {
//
//        loadCategories()
//    }
//
//    private fun loadCategories() {
//
//        setState { ViewModelState.WORKING }
//
//        viewModelScope.launch {
//            try {
//
//                cocktailRepo.getCategories().collect { categories ->
//                    _categories.value = categories
//                }
//
//                setState { ViewModelState.READY }
//            } catch (e: RepositoryOperationError) {
//
//                _error.value = e
//                setState { ViewModelState.ERROR }
//            }
//        }
//    }
//
//    fun loadCocktailsFromCategory(category: Category) {
//
//        setState { ViewModelState.WORKING }
//
//        viewModelScope.launch {
//            try {
//
//                val urlArgument = category.toUrlArgument()
//                cocktailRepo.getCocktailsByCategory(urlArgument).collect { cocktails ->
//                    _cocktailsByCategory.value = cocktails
//                    _navigatedCategory.value = category.name
//                }
//
//                setState { ViewModelState.READY }
//            } catch (e: RepositoryOperationError) {
//
//                _error.value = e
//                setState { ViewModelState.ERROR }
//            }
//        }
//    }
//
//    fun setNavigationFlag() { _hasNavigated.value = true }
//
//    fun resetNavigationFlag() {
//        _hasNavigated.value = false
//        _cocktailsByCategory.value = emptyList()
//        _navigatedCategory.value = ""
//    }
//
//    fun loadCocktailsByIngredient(ingredient: String) {
//        viewModelScope.launch {
//            cocktailRepo.getCocktailsByIngredient(ingredient)
//                .catch { e -> e.printStackTrace() }
//                .collect { cocktails ->
//                    _cocktails.value = cocktails
//                }
//        }
//    }
}

package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class CategoriesViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {


    private val _repoError = MutableStateFlow<RepositoryError?>(null)
    val repoError: StateFlow<RepositoryError?> = _repoError

    private val _listForNavigationIds = MutableStateFlow<String?>(null)
    val listForNavigationIds: StateFlow<String?> = _listForNavigationIds

    private var _clickedCategoryName: String? = null
    val clickedCategoryName = _clickedCategoryName

    val categories: StateFlow<List<Category>> =
        cocktailRepo.getCategories()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                emptyList()
            )

    fun onCategoryClick(category: Category) {

        if (state.value != ViewModelState.READY) return

        viewModelScope.launch {
            setState { ViewModelState.WORKING }

            try {
                cocktailRepo.getCocktails(category).collect { cocktails ->

                    val listIds = cocktails.take(30).map { it.id.toString() }

                    _clickedCategoryName = category.name
                    _listForNavigationIds.value = listIds.joinToString(",")

                    cancel()
                }
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

    fun resetListForNavigationIds() {
        _listForNavigationIds.value = null
        setState { ViewModelState.READY }
    }
}

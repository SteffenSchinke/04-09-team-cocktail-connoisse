package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch

class FavoriteSwitchViewModel (

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface,
    private val cocktailId: Long
) : AndroidViewModel(application) {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    init {
        loadCocktail()
    }

    private fun loadCocktail() {

        viewModelScope.launch {
            try {

                _cocktail.value = cocktailRepo.getCocktail(cocktailId).singleOrNull()
            } catch (e: Exception) {

                Log.e("FavoriteSwitchViewModel", "loadCocktail($cocktailId), VM Instance: ${this.hashCode()}", e)
            }
        }
    }

    fun updateFavoriteState() {

        viewModelScope.launch {

            val current = _cocktail.value ?: return@launch

            val updated = current.copy(favorited = !current.favorited)
            try {

                cocktailRepo.updateCocktail(updated)
                _cocktail.value = updated
            } catch (e: Exception) {

                Log.e("FavoriteSwitchVM", "updateFavoriteState($cocktailId), VM Instance: ${this.hashCode()}", e)
            }
        }
    }
}
package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val DATASTORE_IS_DARK_MODE = booleanPreferencesKey("isDarkMode")

class SettingsViewModel(

    application: Application
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    // TODO sts 23.05.25 - implement viewmodel preference for user setting local notifications

    private val _dataStore = application.dataStore
    private val _scope = viewModelScope

    val isDarkMode: StateFlow<Boolean> = _dataStore.data
        .map { prefs -> prefs[DATASTORE_IS_DARK_MODE] == true }
        .stateIn(
            scope = _scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun toggleIsDarkMode(newValue: Boolean) {

        viewModelScope.launch {
            _dataStore.edit { prefs ->
                prefs[DATASTORE_IS_DARK_MODE] = newValue
            }
        }
    }
}
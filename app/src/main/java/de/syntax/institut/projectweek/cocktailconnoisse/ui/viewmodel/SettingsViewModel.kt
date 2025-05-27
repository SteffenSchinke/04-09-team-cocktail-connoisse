package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.SnackbarDisplayTime
import de.schinke.steffen.enums.SnackbarMode
import de.schinke.steffen.enums.ViewModelState
import de.schinke.steffen.extensions.sendMessageOnSnackbar
import de.schinke.steffen.services.AppSnackbar
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val DATASTORE_IS_DARK_MODE = booleanPreferencesKey("isDarkMode")
private val DATASTORE_IS_NOTIFICATION_INFO = booleanPreferencesKey("isNotificationInfo")
private val DATASTORE_IS_NOTIFICATION_TIP = booleanPreferencesKey("isNotificationTip")
private val DATASTORE_IS_NOTIFICATION_ERROR = booleanPreferencesKey("isNotificationError")

class SettingsViewModel(

    application: Application,
    private val cocktailDao: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _dataStore = application.dataStore
    private val _scope = viewModelScope

    init {

        viewModelScope.launch {

            // first app start set default values
            val prefs = _dataStore.data.first()

            if (DATASTORE_IS_NOTIFICATION_INFO !in prefs) {
                _dataStore.edit { it[DATASTORE_IS_NOTIFICATION_INFO] = true }
            }

            if (DATASTORE_IS_NOTIFICATION_ERROR !in prefs) {
                _dataStore.edit { it[DATASTORE_IS_NOTIFICATION_ERROR] = true }
            }
        }
    }

    val isCacheEmpty: StateFlow<Boolean> = cocktailDao.isCacheEmpty()
        .stateIn( _scope, SharingStarted.WhileSubscribed(), true)

    val isDarkTheme: StateFlow<Boolean> = _dataStore.data
        .map { prefs -> prefs[DATASTORE_IS_DARK_MODE] == true }
        .stateIn(
            scope = _scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val isNotificationInfo: StateFlow<Boolean> = _dataStore.data
        .map { prefs -> prefs[DATASTORE_IS_NOTIFICATION_INFO] == true }
        .stateIn(
            scope = _scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = true
        )

    val isNotificationTip: StateFlow<Boolean> = _dataStore.data
        .map { prefs -> prefs[DATASTORE_IS_NOTIFICATION_TIP] == true }
        .stateIn(
            scope = _scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val isNotificationError: StateFlow<Boolean> = _dataStore.data
        .map { prefs -> prefs[DATASTORE_IS_NOTIFICATION_ERROR] == true }
        .stateIn(
            scope = _scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = true
        )

    fun toggleIsDarkMode(newValue: Boolean) {

        viewModelScope.launch {
            _dataStore.edit { prefs ->
                prefs[DATASTORE_IS_DARK_MODE] = newValue
            }

            sendMessageOnSnackbar(
                if (newValue) "Dark Mode wurde aktiviert"
                else "Light Mode wurde aktiviert"
            )
        }
    }

    fun toggleIsNotificationInfo(newValue: Boolean) {

        viewModelScope.launch {
            _dataStore.edit { prefs ->
                prefs[DATASTORE_IS_NOTIFICATION_INFO] = newValue
            }
            configSnackbarMode(newValue, SnackbarMode.INFO)

            sendMessageOnSnackbar(
                if (newValue) "Benachrichtigungen bei Info's wurde aktiviert"
                else "Benachrichtigungen bei Info's wurde deaktiviert"
            )
        }
    }

    fun toggleIsNotificationTip(newValue: Boolean) {

        // TODO sts 25.05.25 - implement useful hints for app

        viewModelScope.launch {
            _dataStore.edit { prefs ->
                prefs[DATASTORE_IS_NOTIFICATION_TIP] = newValue
            }

            configSnackbarMode(newValue, SnackbarMode.TIP)

            sendMessageOnSnackbar(
                message = "Leider sind in der Cocktail Anwendung noch keine Hinweise implementiert.\nSchaue später noch einmal vorbei!",
                mode = SnackbarMode.TIP,
                actionOnNewLine = true,
                actionLabel = "Verstanden",
                actionAction = {
                    viewModelScope.launch {
                        _dataStore.edit { prefs ->
                            prefs[DATASTORE_IS_NOTIFICATION_TIP] = false
                        }
                        configSnackbarMode(false, SnackbarMode.TIP)
                    }
                },
                withDismissAction = false,
                duration = SnackbarDisplayTime.INDEFINITE
            )
        }

//
//        viewModelScope.launch {
//            _dataStore.edit { prefs ->
//                prefs[DATASTORE_IS_NOTIFICATION_TIP] = newValue
//            }
//            configSnackbarMode(newValue, SnackbarMode.TIP)
//
//            sendMessageOnSnackbar(
//                if (newValue) "Benachrichtigungen für Hinweise wurde aktiviert"
//                else "Benachrichtigungen für Hinweise wurde deaktiviert"
//            )
//        }
    }

    fun toggleIsNotificationError(newValue: Boolean) {

        viewModelScope.launch {
            _dataStore.edit { prefs ->
                prefs[DATASTORE_IS_NOTIFICATION_ERROR] = newValue
            }
            configSnackbarMode(newValue, SnackbarMode.ERROR)
        }

        sendMessageOnSnackbar(
            if (newValue) "Benachrichtigungen bei Error wurde aktiviert"
            else "Benachrichtigungen bei Error wurde deaktiviert"
        )
    }

    fun deleteCache() {

        viewModelScope.launch {

            cocktailDao.truncateCache()

            sendMessageOnSnackbar(
                message = "Cache wurde gelöscht",
                mode = SnackbarMode.INFO
            )
        }

        // TODO sts 27.05.25 - handling ?!?!

//        sendMessageOnSnackbar(
//            message = "Achtung, du bist dabei die ganzen Cache zu löschen!",
//            mode = SnackbarMode.TIP,
//            actionOnNewLine = true,
//            actionLabel = "Löschen",
//            duration = SnackbarDisplayTime.INDEFINITE,
//            actionAction = {
//                viewModelScope.launch {
//                    cocktailDao.truncateCache()
//                }
//            }
//        )
    }

    private fun configSnackbarMode(newValue: Boolean, mode: SnackbarMode) {

        if (newValue) {
            AppSnackbar.addNotificationMode(mode)
        } else {
            AppSnackbar.removeNotificationMode(mode)
        }
    }
}
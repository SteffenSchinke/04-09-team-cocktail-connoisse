package de.syntax.institut.projectweek.cocktailconnoisse.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import de.schinke.steffen.interfaces.AppRouteContent

@Composable
fun AppRouteContent.getStringResourceByName(

    key: String
): String {

    // TODO sts 24.05.25 - find another way to get string from resource

    val context = LocalContext.current
    val resId = context.resources.getIdentifier(key, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else "[$key]"
}
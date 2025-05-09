package com.example.soundbeat_test.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.soundbeat_test.R

/**
 * Representa un elemento de navegación dentro de la interfaz de usuario.
 *
 * Esta clase se utiliza típicamente para construir menús de navegación (como una BottomNavigationBar),
 * donde cada ítem tiene un texto descriptivo y un icono visual.
 *
 * @property label Texto que se mostrará como etiqueta del ítem de navegación.
 * @property icon Ícono representado mediante un [ImageVector], usado para mostrar el gráfico del ítem.
 */
data class NavItem(
    val label: String,
    val icon: ImageVector
)

/**
 * Composable que devuelve una lista de elementos de navegación [NavItem].
 *
 * Esta función genera una lista estática de ítems que representan secciones principales
 * de la aplicación, como la pantalla de inicio, listas guardadas, búsqueda y perfil.
 * Cada ítem incluye una etiqueta y un icono correspondiente cargado desde recursos vectoriales.
 *
 * @return Lista de [NavItem] que puede utilizarse, por ejemplo, en una barra de navegación inferior.
 */
@Composable
fun GetNavItemList(): List<NavItem> {
    val navItemList: List<NavItem> = listOf(
        NavItem("HOME", ImageVector.vectorResource(R.drawable.home_24dp)),
        NavItem("PLAYLISTS", ImageVector.vectorResource(R.drawable.bookmark_24dp_filled)),
        NavItem("SEARCH", ImageVector.vectorResource(R.drawable.search)),
        NavItem("PROFILE", ImageVector.vectorResource(R.drawable.account_circle_24dp))
    )
    return navItemList // Devuelvo la lista con los elementos de la barra inferior de navegación.
}
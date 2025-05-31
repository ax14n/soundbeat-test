package com.example.soundbeat_test.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.local.listLocalAlbums
import com.example.soundbeat_test.network.getServerSongs
import com.example.soundbeat_test.ui.screens.search.SearchMode.LOCAL
import com.example.soundbeat_test.ui.screens.search.SearchMode.REMOTE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Enumera los modos de funcionamiento del ViewModel. Dependiendo del modo elegido el ViewModel
 * buscará canciones en el servidor remoto o en la carpeta local del usuario.
 * @property REMOTE: Indica que debe buscar en el servidor remoto.
 * @property LOCAL: Indica que debe buscar en el almacenamiento local.
 */
enum class SearchMode {
    REMOTE, LOCAL
}

/**
 * Clase que se encarga de la lógica de la pantalla de búsqueda de canciones.
 */
class SearchScreenViewModel() : ViewModel() {

    /**
     * Propiedad que almacena el modo en el que se encuentra el ViewModel.
     */
    private val _searchMode = MutableStateFlow<SearchMode>(LOCAL)

    /**
     * Propiedad de solo lectura que sirve al proposito de ver el modo en el que se encuentra
     * el ViewModel.
     */
    val searchMode: StateFlow<SearchMode> = _searchMode

    /**
     * Propiedad que almacena la lista de canciones obtenidas del servidor.
     */
    private var _albumList = MutableStateFlow<List<Album>>(emptyList<Album>())

    /**
     * Propiedad de solo lectura que sirve al proposito de mostrar la lista de canciones.
     */
    public val albumList = _albumList

    /**
     * Propiedad que, en caso de fallar la petición, almacena el mensaje de error.
     */
    private var _errorMessage: String = ""

    /**
     * Propiedad de solo lectura que sirve al proposito de mostrar el mensaje de error en caso de
     * fallo.
     */
    public val errorMessage = _errorMessage

    /**
     * Propiedad que almacena el estado del texto del TextField ubicado en pantalla.
     */
    private val _textFieldText = MutableStateFlow<String>("")

    /**
     * Propiedad de solo lectura que apunta al texto del TextField para consultar su valor actual.
     */
    public val textFieldText = _textFieldText

    init {
        fillSongsList()
    }

    /**
     * Carga canciones locales o remotas dependiendo del modo asignado en que actua el ViewModel.
     * @see SearchMode
     */
    fun fillSongsList(query: String = "") {

        _searchMode.value?.let { mode ->
            when (mode) {
                REMOTE -> {
                    viewModelScope.launch {

                        val result = getServerSongs()

                        if (result.isSuccess) {
                            val fullList = result.getOrNull()
                            _albumList.value = (if (query.isBlank()) {
                                fullList
                            } else {
                                fullList?.filter { it.name.startsWith(query, ignoreCase = true) }
                            })!!
                        } else {
                            _errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                        }
                    }
                }

                LOCAL -> {
                    _albumList.value = listLocalAlbums()
                }
            }

        }
    }

    /**
     * Funcón hecha para actualizar el valor de texto del TextField de forma interna en ViewModel.
     */
    fun onSearchQueryChange(query: String) {
        _textFieldText.value = query
    }

    /**
     * Asigna un modo de búsqueda al ViewModel.
     */
    fun setSearchMode(searchMode: SearchMode) {
        _searchMode.value = searchMode
    }
}

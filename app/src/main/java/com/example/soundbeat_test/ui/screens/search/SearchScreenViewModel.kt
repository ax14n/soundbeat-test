package com.example.soundbeat_test.ui.screens.search

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.local.listLocalAlbums
import com.example.soundbeat_test.network.getServerSongs
import com.example.soundbeat_test.ui.screens.search.SearchMode.LOCAL
import com.example.soundbeat_test.ui.screens.search.SearchMode.REMOTE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Lista de géneros disponibles para el filtrado.
 */
enum class Genres(val displayName: String) {
    ROCK("Rock"), POP("Pop"), JAZZ("Jazz"), REGGAE("Reggae"), METAL("Metal"), CLASSIC("Classic"), HIP_HOP(
        "Hip Hop"
    ),
    ELECTRONICA("Electrónica"), BLUES("Blues"), COUNTRY("Country"), FOLK("Folk"), LATINO("Latino"), RAP(
        "Rap"
    ),
    TRAP("Trap"), RNB("R&B"), PUNK("Punk"), SOUL("Soul"), DANCE("Dance"), HOUSE("House"), TECHNO("Techno"), AMBIENT(
        "Ambient"
    ),
    CINEMATIC("Cinematic"), INSTRUMENTAL("Instrumental"), ACOUSTIC("Acoustic"), INDIE("Indie"), EXPERIMENTAL(
        "Experimental"
    ),
    CHILL("Chill"), LOFI("Lo-Fi"), OPERA("Opera"), SOUNDTRACK("Soundtrack"), TRAILER("Trailer"), EPIC(
        "Epic"
    ),
    DARK("Dark"), PIANO("Piano"), SAD("Sad"), ALTERNATIVE("Alternative"), CLASSICAL("Classical"), INSPIRATIONAL(
        "Inspirational"
    ),
    ORCHESTRAL("Orchestral"), HAPPY("Happy"), TEEN("Teen"), CORPORATE("Corporate"), INSPIRATION("Inspiration"), OTHER(
        "Other"
    )
}


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
     * Propuedad que almacena los géneros seleccionados en el filtro de búsqueda.
     */
    private val _selectedGenres = MutableStateFlow<Set<Genres>>(setOf())

    /**
     * Propiedad de solo lectura que sirve al propósito de mostrar los géneros filtrados
     * por el usuario.
     */
    val selectedGenres: StateFlow<Set<Genres>> = _selectedGenres

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
     * Propiedad que almacena la lista de canciones remotas/locales a mostrar.
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

    /**
     * Propiedad que almacena si el desplegable de filtros se encuentra visible o no.
     */
    private val _isFilterVisible = MutableStateFlow(false)

    /**
     * Propiedad de solo lectura que consulta si el desplegable de filtros se encuentra visible o no.
     */
    val isFilterVisible: StateFlow<Boolean> = _isFilterVisible

    /**
     * Esta propiedad determina si el interruptor de alternancia debe estar oculto.
     * Tiene sentido ocultarlo dependiendo de desde qué pantalla se accede al buscador,
     * por ejemplo al añadir canciones a una playlist.
     */
    private val _alternationSwitchIsHidden = MutableStateFlow<Boolean>(false)

    /**
     * Propiedad de solo lectura que consulta si el Switch de búsqueda en remoto o local se
     * encuentra visible o no.
     */
    val alternationSwitchIsHidden: StateFlow<Boolean> = _alternationSwitchIsHidden

    init {
//        fillSongsList()
    }

    /**
     * Muestra o esconde el desplegable de filtros.
     */
    fun toggleFilterVisibility() {
        _isFilterVisible.value = !_isFilterVisible.value
    }

    /**
     * Carga canciones locales o remotas dependiendo del modo asignado en que actua el ViewModel.
     * @see SearchMode
     */
    @OptIn(UnstableApi::class)
    fun fillSongsList(query: String = "") {
        val mode = _searchMode.value
        Log.d("SearchScreenViewModel", "searching songs using $mode mode.")
        val shouldFilterByGenre = _selectedGenres.value.isNotEmpty()
        when (mode) {

            REMOTE -> {
                viewModelScope.launch {
                    Log.d(
                        "SearchScreenViewModel", "genre filtering enabled: $shouldFilterByGenre"
                    )
                    val result = getServerSongs()
                    result.getOrNull()?.let { remoteAlbums ->
                        val filteredRemoteAlbums =
                            if (shouldFilterByGenre) filterAlbumsByGenre(remoteAlbums) else remoteAlbums
                        _albumList.value = filterAlbumsByQuery(filteredRemoteAlbums, query)
                    } ?: run {
                        _errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    }
                }
            }

            LOCAL -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val localAlbums = listLocalAlbums()

                    val filteredLocalAlbums =
                        if (shouldFilterByGenre) filterAlbumsByGenre(localAlbums) else localAlbums

                    val finalAlbums = filterAlbumsByQuery(filteredLocalAlbums, query)

                    withContext(Dispatchers.Main) {
                        _albumList.value = finalAlbums
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun filterAlbumsByGenre(list: List<Album>): List<Album> = list.filter { album ->

        val selectedGenreNames = _selectedGenres.value.map { it.displayName }.toSet()

        val hasOccurrences = selectedGenreNames.all { it in album.genre }

        Log.d("SearchScreenViewModel", "album stored genres: ${album.genre}")
        Log.d("SearchScreenViewModel", "selected genres: $selectedGenreNames")
        Log.d("SearchScreenViewModel", "has common genres? ${if (hasOccurrences) "YES" else "NO"}")

        hasOccurrences
    }

    private fun filterAlbumsByQuery(albums: List<Album>, query: String): List<Album> {
        return if (query.isBlank()) albums
        else albums.filter { it.name.startsWith(query, ignoreCase = true) }
    }

    /**
     * Funcón hecha para actualizar el valor de texto del TextField de forma interna en ViewModel.
     */
    fun onSearchQueryChange(query: String) {
        _textFieldText.value = query
    }

    /**
     * Establece el modo de búsqueda.
     */
    fun setSearchMode(mode: SearchMode) {
        _searchMode.value = mode
        fillSongsList()
    }


    /**
     * Alterna la presencia de un género en el filtro:
     * si ya está seleccionado, lo elimina; si no, lo agrega.
     */
    @OptIn(UnstableApi::class)
    fun toggleGenreInSongsFilter(genre: Genres) {
        _selectedGenres.value = if (genre in _selectedGenres.value) {
            _selectedGenres.value - genre
        } else {
            _selectedGenres.value + genre
        }
        Log.d("SearchScreenViewModel", "the filter has: ${_selectedGenres.value}")
    }

    fun switchHidden() {
        _alternationSwitchIsHidden.value = !_alternationSwitchIsHidden.value
    }

}

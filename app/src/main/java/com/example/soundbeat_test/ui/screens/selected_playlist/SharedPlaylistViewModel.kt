package com.example.soundbeat_test.ui.screens.selected_playlist

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.data.Playlist
import com.example.soundbeat_test.data.Playlist.Companion.toEntity
import com.example.soundbeat_test.local.room.DatabaseProvider
import com.example.soundbeat_test.local.room.repositories.PlaylistRepository
import com.example.soundbeat_test.network.deletePlaylist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Representa los modos posibles de operación para selección o reproducción.
 *
 * Este enum se utiliza para distinguir si el usuario está interactuando
 * con una única canción o con una playlist completa.
 */
enum class SelectionMode {
    SONG, PLAYLIST
}

/**
 * Indica la procedencia de las canciones.
 */
enum class SongSource {
    LOCALS, REMOTES, FAVORITES
}

/**
 * ViewModel compartido para almacenar temporalmente la playlist seleccionada.
 *
 * Esta clase se utiliza como puente de datos entre pantallas: una pantalla selecciona
 * una playlist y la almacena aquí, y la siguiente pantalla puede recuperarla sin
 * necesidad de pasarla explícitamente por los argumentos de navegación.
 *
 * Es útil cuando se navega entre composables y se quiere evitar la serialización
 * de objetos complejos como `Playlist`.
 */
class SharedPlaylistViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Flujo interno que contiene la playlist actualmente seleccionada.
     *
     * Este valor puede ser observado desde otras pantallas para acceder a la playlist
     * elegida por el usuario sin necesidad de pasarla como argumento.
     * Se expone públicamente a través de [selectedPlaylist].
     */
    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)

    /**
     * Flujo público de solo lectura que representa la playlist seleccionada.
     *
     * Las pantallas o componentes pueden observar este flujo para reaccionar
     * ante cambios en la selección de playlist.
     */
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist

    /**
     * Almacena la procedencia de las canciones.
     */
    private val _songsSource = MutableStateFlow<SongSource>(SongSource.REMOTES)

    /**
     * Informa si la Playlist que contiene en su interior esta hecha de canciones locales o remotas.
     */
    val songsSource: StateFlow<SongSource> = _songsSource

    /**
     * Indica si el elemento actualmente seleccionado es una playlist.
     *
     * Se utiliza como flag de control para distinguir entre distintos tipos de selección
     * cuando hay elementos que pueden ser playlists u otras entidades.
     */
    private val _isPlaylist = MutableStateFlow<SelectionMode>(SelectionMode.SONG)

    /**
     * Flujo público de solo lectura que expone si el elemento seleccionado es una playlist.
     *
     * Este valor puede usarse para condicionar comportamiento o interfaz según el tipo de selección.
     */
    val mode: StateFlow<SelectionMode> = _isPlaylist

    /**
     * Actualiza la playlist seleccionada para compartirla entre pantallas.
     *
     * Este método se utiliza cuando el usuario selecciona una playlist que se quiere
     * mantener accesible temporalmente desde otros componentes o pantallas de la aplicación.
     *
     * @param playlist La playlist que se desea guardar como seleccionada.
     */
    fun updatePlaylist(playlist: Playlist) {
        _selectedPlaylist.value = playlist
    }

    /**
     * Limpia la playlist seleccionada.
     *
     * Este método elimina la playlist actualmente almacenada para evitar
     * mantener datos obsoletos o innecesarios en memoria.
     * Generalmente se llama después de navegar o finalizar una operación relacionada.
     */
    fun clearPlaylist() {
        _selectedPlaylist.value = null
    }

    /**
     * Establece el modo de selección actual.
     *
     * Esta función actualiza el valor de [_isPlaylist] indicando si el modo actual
     * corresponde a la selección de una canción individual o de una playlist completa.
     * Es útil para adaptar el comportamiento de la interfaz según el tipo de contenido seleccionado.
     *
     * @param selectionMode El modo de selección deseado, ya sea [SelectionMode.SONG] o [SelectionMode.PLAYLIST].
     */
    fun setMode(selectionMode: SelectionMode) {
        _isPlaylist.value = selectionMode
    }

    /**
     * Almacena y configura el ViewModel para destingir la procedencia de las canciones
     * almacenadas en su interior.
     */
    fun setSongsSource(songsSource: SongSource) {
        _songsSource.value = songsSource
    }

    /**
     * Elimina la playlist remota pasada como argumento a la función.
     * @param playlist Referencia de la playlist a eliminar.
     */
    @OptIn(UnstableApi::class)
    fun deleteRemotePlaylist(playlist: Playlist) {
        Log.d("SharedPlaylistViewModel", "trying to erase remote playlist with id: ${playlist.id}")
        CoroutineScope(Dispatchers.IO).launch {
            deletePlaylist(playlist?.id ?: -1)
        }
    }

    /**
     * Elimina la playlist local pasada como argumento a la función.
     * @param context Contexto
     * @param playlist Referencia de la playlist a eliminar.
     */
    @OptIn(UnstableApi::class)
    fun deleteLocalPlaylist(playlist: Playlist) {
        Log.d("SharedPlaylistViewModel", "trying to erase local playlist with id: ${playlist.id}")
        val context = getApplication<Application>().applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.getPlaylistDao(context)
            val repository = PlaylistRepository(dao)
            repository.delete(playlist.toEntity())
        }
    }

}

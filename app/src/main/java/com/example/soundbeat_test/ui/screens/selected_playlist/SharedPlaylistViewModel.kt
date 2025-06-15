package com.example.soundbeat_test.ui.screens.selected_playlist

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Album.Companion.toSong
import com.example.soundbeat_test.data.Playlist
import com.example.soundbeat_test.data.Playlist.Companion.toEntity
import com.example.soundbeat_test.local.room.DatabaseProvider
import com.example.soundbeat_test.local.room.DatabaseProvider.getPlaylistSongDao
import com.example.soundbeat_test.local.room.DatabaseProvider.getSongDao
import com.example.soundbeat_test.local.room.entities.PlaylistSong
import com.example.soundbeat_test.local.room.repositories.PlaylistRepository
import com.example.soundbeat_test.network.addSongsToRemotePlaylist
import com.example.soundbeat_test.network.deletePlaylist
import com.example.soundbeat_test.network.deleteSongsFromPlaylist
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
    LOCALS, REMOTES, REMOTES_FAVORITES
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

    private val _isEditionMode = MutableStateFlow<Boolean>(false)
    val isEditionMode: StateFlow<Boolean> = _isEditionMode

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
    val isPlaylist: StateFlow<SelectionMode> = _isPlaylist

    /**
     * Flujo público de solo lectura que expone si el elemento seleccionado es una playlist.
     *
     * Este valor puede usarse para condicionar comportamiento o interfaz según el tipo de selección.
     */
    val mode: StateFlow<SelectionMode> = _isPlaylist

    /**
     * Las canciones agregadas para actualizar una playlist serán almacenadas aquí. Al confirmar
     * los cambios se usará para agregarlas a la respectiva playlist.
     */
    private val _insertStagedSongs = MutableStateFlow<Set<Album?>>(emptySet())
    val insertStagedSongs: StateFlow<Set<Album?>> = _insertStagedSongs

    private val _removeStagedSongs = MutableStateFlow<Set<Album?>>(emptySet())
    val removeStagedSongs: StateFlow<Set<Album?>> = _removeStagedSongs

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

    fun addSongToSharedSongs(album: Album) {
        android.util.Log.d("SharedPlaylistViewModel", "adding to the temporal list: $album")
        val currentPlaylist = _selectedPlaylist.value
        if (currentPlaylist != null) {
            val updatedSongs = currentPlaylist.songs + album
            _selectedPlaylist.value = currentPlaylist.copy(songs = updatedSongs)
            _insertStagedSongs.value = updatedSongs
            android.util.Log.d(
                "SharedPlaylistViewModel", "final songs list: ${_selectedPlaylist.value}"
            )
        }
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
    fun setSelectionMode(selectionMode: SelectionMode) {
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

    /**
     * Agrega una o varias canciones a una playlist remota.
     * @param playlistId: Identificador de la playlist.
     * @param songIds: Colección que contiene los identificadores de las canciones a agregar.
     */
    fun addSongsToExistentRemotePlaylist() {
        val playlistId = _selectedPlaylist.value?.id
        val stagedSongs = _insertStagedSongs.value
        viewModelScope.launch {
            addSongsToRemotePlaylist(
                playlistId = playlistId!!, albums = stagedSongs.toList()
            )
        }
        // Agregadas las canciones a la playlist, vacio la zona de stage.
        _insertStagedSongs.value = emptySet<Album>()
    }

    private val localPlaylistSongDb = getPlaylistSongDao(application.applicationContext)
    private val localSongDb = getSongDao(application.applicationContext)

    /**
     * Agrega una o varias canciones a una playlist.
     * @param playlistId: Identificador de la playlist.
     * @param songIds: Colección que contiene los identificadores de las canciones a agregar.
     */
    @OptIn(UnstableApi::class)
    fun addSongsToExistentLocalPlaylist() {
        viewModelScope.launch {
            val playlistId = _selectedPlaylist.value?.id
            val stagedSongs = _insertStagedSongs.value
            for (album in stagedSongs) {
                val existence = localSongDb.getSongByTitleAndArtist(album!!.title, album.author)

                val songId = existence?.songId ?: localSongDb.insert(album.toSong()).toInt()

                Log.d("SharedPlaylistViewModel", "inserted song with id: $songId")

                val playlistSong = album?.let {
                    PlaylistSong(
                        playlist_id = playlistId!!, song_id = songId
                    )
                }
                localPlaylistSongDb.insert(playlistSong!!)
            }
        }
        // Agregadas las canciones a la playlist, vacio la zona de stage.
        _insertStagedSongs.value = emptySet<Album>()
    }

    /**
     * Elimina una o varias canciones pertenecientes a una playlist remota.
     */
    fun deleteSongsFromExistentRemotePlaylist() {
        val playlistId = _selectedPlaylist.value?.id
        val removeStagedSongs = _removeStagedSongs.value

        viewModelScope.launch {
            deleteSongsFromPlaylist(
                playlistId = playlistId!!, albums = removeStagedSongs.toList()
            )
        }
        // Removidas las canciones de la playlist, vacio la zona de stage.
        _removeStagedSongs.value = emptySet<Album>()
    }

    /**
     * Elimina una o varias canciones pertenecientes a una playlist local.
     */
    @OptIn(UnstableApi::class)
    fun deleteSongsFromExistentLocalePlaylist() {
        val playlistId = _selectedPlaylist.value?.id
        val stagedSongs = _removeStagedSongs.value
        viewModelScope.launch {
            for (album in stagedSongs) {
                val existence = localSongDb.getSongByTitleAndArtist(album!!.title, album.author)

                val songId = existence?.songId ?: -1
                Log.d("SharedPlaylistViewModel", "trying to delete song with id: $songId")

                val playlistSong = album?.let {
                    PlaylistSong(
                        playlist_id = playlistId!!, song_id = songId
                    )
                }
                localPlaylistSongDb.delete(playlistSong!!)
            }
        }
        // Removidas las canciones de la playlist, vacio la zona de stage.
        _removeStagedSongs.value = emptySet<Album>()
    }

    fun addToRemoveStagedSong(album: Album) {
        _removeStagedSongs.value += album
    }

    fun addToInsertStagedSong(album: Album) {
        _insertStagedSongs.value += album
    }

    fun removeFromInsertStagedSong(album: Album) {
        _insertStagedSongs.value -= album
    }

    fun removeFromRemoveStagedSong(album: Album) {
        _removeStagedSongs.value -= album
    }

    /**
     * Si se interactua con el switch se cambiara al modo edición o se desactivará.
     */
    fun onSwitchToggle() {
        _isEditionMode.value = !_isEditionMode.value
    }

    fun setEditableMode(status: Boolean) {
        _isEditionMode.value = status
    }

}

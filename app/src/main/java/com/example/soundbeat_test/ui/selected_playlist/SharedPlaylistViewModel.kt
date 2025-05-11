package com.example.soundbeat_test.ui.selected_playlist

import androidx.lifecycle.ViewModel
import com.example.soundbeat_test.data.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
class SharedPlaylistViewModel : ViewModel() {
    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist

    fun updatePlaylist(playlist: Playlist) {
        _selectedPlaylist.value = playlist
    }
}

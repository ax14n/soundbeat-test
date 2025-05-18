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
}

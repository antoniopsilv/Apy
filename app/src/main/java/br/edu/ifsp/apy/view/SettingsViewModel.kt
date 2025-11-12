package br.edu.ifsp.apy.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.apy.model.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    // Lê o valor salvo no DataStore
    val useUCrop = repository.useUCrop
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Atualiza e salva no DataStore
    fun toggleUCrop(enabled: Boolean) {
        viewModelScope.launch {
            repository.setUseUCrop(enabled)
        }
    }
}

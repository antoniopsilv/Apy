package br.edu.ifsp.apy.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifsp.apy.model.entity.History
import br.edu.ifsp.apy.model.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val historyRepository: HistoryRepository = HistoryRepository(application)

    fun insertHistory(history: History) = viewModelScope.launch(Dispatchers.IO){
        historyRepository.insert(history)
    }

    fun deleteHistory()  = viewModelScope.launch(Dispatchers.IO) {
        historyRepository.delete()
    }

    fun getHistory(): LiveData<List<History>> = historyRepository.getHistory()

}

class HistoryViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
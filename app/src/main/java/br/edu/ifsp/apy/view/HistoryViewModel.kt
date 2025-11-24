package br.edu.ifsp.apy.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
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


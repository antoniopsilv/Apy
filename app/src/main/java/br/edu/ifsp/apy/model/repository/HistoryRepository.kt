package br.edu.ifsp.apy.model.repository

import br.edu.ifsp.apy.model.dao.HistoryDAO
import br.edu.ifsp.apy.model.database.HistoryDatabase
import br.edu.ifsp.apy.model.entity.History
import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {
    private val historyDao: HistoryDAO
    //private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDatabase.getDatabase(application)
        historyDao = db.historyDao()
    }

    suspend fun insert(history: History) {
        historyDao.insert(history)
    }

    suspend fun delete() {
        historyDao.delete()
    }

    fun getHistory(): LiveData<List<History>> = historyDao.getHistory()
}

package br.edu.ifsp.apy.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.edu.ifsp.apy.model.entity.History

@Dao
interface HistoryDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: History)

    @Query("DELETE FROM history")
    suspend fun delete()

    @Query("SELECT * FROM history ORDER BY id ASC")
    fun getHistory(): LiveData<List<History>>

}

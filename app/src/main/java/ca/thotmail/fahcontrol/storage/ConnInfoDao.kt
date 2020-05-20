package ca.thotmail.fahcontrol.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConnInfoDao {
    @Query("SELECT * FROM ConnectionInfo")
    suspend fun getAll():List<ConnectionInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg connections: ConnectionInfo)
}
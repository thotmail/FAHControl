package ca.thotmail.fahcontrol.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConnInfoDao {
    @Query("SELECT * FROM ConnectionInfo")
    fun getAll():List<ConnectionInfo>
    //suspend for coroutine
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertAll(vararg connections: ConnectionInfo)
}
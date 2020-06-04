package ca.thotmail.fahcontrol.storage

import androidx.room.*

@Dao
interface ConnInfoDao {
    @Query("SELECT * FROM ConnectionInfo")
    suspend fun getAll():List<ConnectionInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg connections: ConnectionInfo)

    @Delete
    suspend fun delete(info: ConnectionInfo)

    @Query("UPDATE ConnectionInfo SET ipAddr = :newip, port = :newport, nickname = :newnick, paswd = :newpas WHERE ipAddr = :oldip AND port = :oldport")
    suspend fun update(oldip:String, oldport:Int, newip:String, newport:Int, newnick:String, newpas:String)

    suspend fun update(old:ConnectionInfo, replace:ConnectionInfo){
        update(old.ipAddr, old.port, replace.ipAddr, replace.port, replace.nickname, replace.paswd)
    }
}
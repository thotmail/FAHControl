package ca.thotmail.fahcontrol.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(primaryKeys = ["ipAddr", "port"])
data class ConnectionInfo  (
    var ipAddr: String,
    val port: Int,
    @ColumnInfo val nickname: String,
    @ColumnInfo val paswd: String

): Serializable
package com.demo.currencyconversion.data.dbtable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "CurrencyRateDomain" ,
    indices = [
        Index(
            value = ["symbol"], unique = true
        )
    ]
)
data class CurrencyRateDomain (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long=0L,
    @ColumnInfo(name = "base")
    val base: String="",
    @ColumnInfo(name = "symbol")
    val symbol: String="",
    @ColumnInfo(name = "name")
    val rate: Float? = null)
package com.demo.currencyconversion.data.dbtable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Currency" ,
    indices = [
        Index(
            value = ["symbol"], unique = true
        )
    ]
)
data class Currency(
    @PrimaryKey(autoGenerate = true)
    var id:Long= 0L,
    @ColumnInfo(name = "symbol")
    val symbol: String="",
    @ColumnInfo(name = "name")
    val name: String? = null
)
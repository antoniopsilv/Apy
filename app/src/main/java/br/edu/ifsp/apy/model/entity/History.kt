package br.edu.ifsp.apy.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "result")
    var result: String,

    @ColumnInfo(name = "imageUri")
    var imageUri: String
)
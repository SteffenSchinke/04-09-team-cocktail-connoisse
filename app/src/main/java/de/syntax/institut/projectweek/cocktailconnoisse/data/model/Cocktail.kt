package de.syntax.institut.projectweek.cocktailconnoisse.data.model


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "cocktail")
data class Cocktail(

    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val category: String,
    val instructions: String?,
    val imageUrl: String,
    var favorited: Boolean = false,
    val isAlcoholic: Boolean,
    val modifiedAt: String?,
    @ColumnInfo(name = "createdAt", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String
) {

    @Ignore
    lateinit var ingredients: List<Ingredient>
}



package de.softdeveloper.shoppinglistroom.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingMemo(
    var quantity:Int,
    var product: String,
    @ColumnInfo(name =  "selected") var isSelected: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0


    override fun toString(): String {
        return "$quantity x $product"
    }
}
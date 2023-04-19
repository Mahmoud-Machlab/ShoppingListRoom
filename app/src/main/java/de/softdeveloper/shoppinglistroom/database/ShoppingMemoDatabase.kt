package de.softdeveloper.shoppinglistroom.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class ShoppingMemoDatabase: RoomDatabase() {

    abstract fun shoppingMemoDao(): ShoppingMemoDao

    object Facrory{
        private var instance: ShoppingMemoDatabase? = null

        fun getInstance(context:Context): ShoppingMemoDatabase{
            if(instance == null){
                synchronized(ShoppingMemoDatabase::class){
                    if(instance == null){
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            ShoppingMemoDatabase::class.java,
                            "shopping_memos"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return instance!!
        }
    }

}
package database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [FoodEntity::class , OrderEntity::class], version = 2)

 abstract class Fooddatabase : RoomDatabase(){
    abstract fun foodDao():FoodDao

    abstract fun orderDao(): OrderDao





}

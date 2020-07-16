package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface FoodDao {

    @Insert
    fun insertFood(foodEntity: FoodEntity)

    @Delete
    fun deleteFood(foodEntity: FoodEntity)

    @Query("SELECT * from foods")
    fun getAllFoods(): List<FoodEntity>

     @Query("SELECT * FROM foods WHERE id = :foodId")
     fun getFoodById(foodId:String):FoodEntity
}

package adaptor

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.arpit.foodproject.R
import com.squareup.picasso.Picasso
import database.FoodEntity
import database.Fooddatabase
import fragment.FragmentMenu

import model.Foods


class HomeRecyclerAdaptor(private var foods: ArrayList<Foods>, val context: Context) :
        RecyclerView.Adapter<HomeRecyclerAdaptor.HomeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_home_single_home, parent, false)
        return HomeViewHolder(View)
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food = foods[position]
        holder.foodName.text = food.name
        holder.foodrating.text = food.rating
        holder.foodcost.text = food.costForTwo.toString()
        Picasso.get().load(food.image_url).error(R.drawable.res_image).into(holder.foodimage)


        val listOfFavourites = GetAllFav(context).execute().get()
        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(food.id.toString())) {
            holder.favImage.setImageResource(R.drawable.ic_action_favchecked)
        } else {
            holder.favImage.setImageResource(R.drawable.ic_action_checked)
        }

          holder.favImage.setOnClickListener {
            val foodentity = FoodEntity(
                    food.id,
                    food.name,
                    food.rating,
                    food.costForTwo.toString(),
                    food.image_url

            )

              if (!DBAsyncTask(context, foodentity, 1).execute().get()) {
                  val async = DBAsyncTask(context, foodentity, 2).execute()
                    val result = async.get()
                    if (result){
                        Toast.makeText(context , "Added to favourite" ,Toast.LENGTH_SHORT).show()
                        holder.favImage.setImageResource(R.drawable.ic_action_favchecked)
                         }
                    } else {
                  val async = DBAsyncTask(context, foodentity, 3).execute()
                  val result = async.get()
                  if (result) {
                      Toast.makeText(context, "Remove from favourite", Toast.LENGTH_SHORT).show()
                      holder.favImage.setImageResource(R.drawable.ic_action_checked)
                  }
              }

        }


                    holder.llcontent.setOnClickListener {

                        val fragment = FragmentMenu()
                        val args = Bundle()
                        args.putInt("id", food.id )
                        args.putString("name", food.name)
                        fragment.arguments = args
                        val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.framelayout, fragment)
                        transaction.commit()
                        (context as AppCompatActivity).supportActionBar?.title = holder.foodName.text.toString()
                    }
                    }



    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val foodimage: ImageView = view.findViewById(R.id.imgfoodimage)
        val llcontent: LinearLayout = view.findViewById(R.id.llcontent)
        val favImage: ImageView = view.findViewById(R.id.imgIsFav)
        val foodName: TextView = view.findViewById(R.id.txtFoodName)
        val foodrating: TextView = view.findViewById(R.id.txtfoodRating)
        val foodcost: TextView = view.findViewById(R.id.txtCostForTwo)

    }


    class DBAsyncTask(context: Context, val foodEntity: FoodEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, Fooddatabase::class.java, "res-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            /*
                        Mode 1 -> Check DB if the food is favourite or not
                        Mode 2 -> Save the food into DB as favourite
                        Mode 3 -> Remove the favourite food
                        */


            when (mode) {

                1 -> {
                    val food: FoodEntity? = db.foodDao().getFoodById(foodEntity.id.toString())
                    db.close()
                    return food != null

                }

                2 -> {

                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true

                }

                3 -> {

                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true
                }

            }

            return false
        }


    }

    /*Since the outcome of the above background method is always a boolean, we cannot use the above here.
    * We require the list of favourite restaurants here and hence the outcome would be list.
    * For simplicity we obtain the list of restaurants and then extract their ids which is then compared to the ids
    * inside the list sent to the adapter */
    class GetAllFav(context: Context) : AsyncTask<Void, Void, List<String>>() {
        val db = Room.databaseBuilder(context, Fooddatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {
            val list = db.foodDao().getAllFoods()
            val lisOfIds = arrayListOf<String>()
            for (i in list) {
                lisOfIds.add(i.id.toString())
            }
            return lisOfIds

        }


    }
}






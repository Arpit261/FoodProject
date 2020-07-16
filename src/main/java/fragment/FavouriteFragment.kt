package fragment

import adaptor.FavRecyclerAdaptor
import adaptor.HomeRecyclerAdaptor
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.arpit.foodproject.R
import database.FoodEntity
import database.Fooddatabase
import model.Foods


class FavouriteFragment:Fragment(){

    private lateinit var recyclerFood: RecyclerView
    private lateinit var favRecyclerAdaptor: FavRecyclerAdaptor
       var Favlist = listOf<FoodEntity>()
    private lateinit var progressLoading: RelativeLayout
    lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var relativeFavourites: RelativeLayout
    private lateinit var relativeNoFavourites: RelativeLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        relativeFavourites = view.findViewById(R.id.relativeFavorites)
        relativeNoFavourites = view.findViewById(R.id.relativeNoFavorites)
        progressLoading = view.findViewById(R.id.progressLoading)
        progressLoading.visibility = View.VISIBLE
        setUpRecycler(view)
        return view
    }

    private fun setUpRecycler(view: View) {
        recyclerFood = view.findViewById(R.id.recyclerFoods)
         layoutManager =LinearLayoutManager(activity)
          Favlist = RetrieveFavouritesAsync(activity as Context).execute().get()
           if (activity!=null){
               progressLoading.visibility=View.GONE

               favRecyclerAdaptor=FavRecyclerAdaptor(activity as Context ,Favlist)
               recyclerFood.adapter =favRecyclerAdaptor
               recyclerFood.layoutManager=layoutManager
           }


    }

class RetrieveFavouritesAsync(context: Context) : AsyncTask<Void, Void, List<FoodEntity>>() {

    val db = Room.databaseBuilder(context, Fooddatabase::class.java, "res-db").build()

    override fun doInBackground(vararg params: Void?): List<FoodEntity> {

        return db.foodDao().getAllFoods()
    }

}
}
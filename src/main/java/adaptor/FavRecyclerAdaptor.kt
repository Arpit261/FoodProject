package adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arpit.foodproject.R
import com.squareup.picasso.Picasso
import database.FoodEntity

class FavRecyclerAdaptor(val context: Context, private val foodList: List<FoodEntity>) :RecyclerView.Adapter<FavRecyclerAdaptor.FavouriteViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_favourite,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
      return foodList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
   val list=foodList[position]
        holder.foodName.text=list.name
        holder.foodrating.text = list.rating
        holder.foodcost.text = list.costForTwo

        Picasso.get().load(list.imageUrl).error(R.drawable.res_image).into(holder.foodimage)
    }




    class FavouriteViewHolder(view: View):RecyclerView.ViewHolder(view){
      val foodimage = view.findViewById(R.id.imgfoodimage) as ImageView
      val foodName = view.findViewById(R.id.txtFoodName) as TextView
      val foodrating = view.findViewById(R.id.txtfoodRating) as TextView
      val foodcost = view.findViewById(R.id.txtCostForTwo) as TextView
  }



}

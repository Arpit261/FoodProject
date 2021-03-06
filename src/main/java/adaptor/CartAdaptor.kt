package adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arpit.foodproject.R
import model.FoodItem

class CartAdaptor(private val cartList: ArrayList<FoodItem>, val context: Context) :
        RecyclerView.Adapter<CartAdaptor.CartViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row, parent, false)
        return CartViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return cartList.size
            }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObject = cartList[position]
        holder.itemName.text = cartObject.name
        val cost = "Rs. ${cartObject.cost?.toString()}"
        holder.itemCost.text = cost
    }


    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.txtCartItemName)
        val itemCost: TextView = view.findViewById(R.id.txtCartPrice)
    }




}
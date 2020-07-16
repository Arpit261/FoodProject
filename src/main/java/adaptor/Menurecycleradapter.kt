package adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arpit.foodproject.R
import model.FoodItem

class Menurecycleradapter (val context: Context, private val orders:ArrayList<FoodItem>, private val listener:OnItemClickListener
):RecyclerView.Adapter<Menurecycleradapter.MenuviewHolder> () {

    companion object {
        var isCartEmpty = true
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_single_row, parent, false)
        return MenuviewHolder(view)

    }

    override fun getItemCount(): Int {
        return orders.size

    }
    interface OnItemClickListener {
        fun onAddItemClick(foodItem: FoodItem)
        fun onRemoveItemClick(foodItem: FoodItem)

    }

    override fun onBindViewHolder(holder: MenuviewHolder, position: Int) {
        val menu = orders[position]
        holder.foodItemName.text = menu.name
        holder.foodItemCost.text = menu.cost.toString()
        holder.sno.text = (position + 1).toString()

        holder.addToCart.setOnClickListener {
            holder.addToCart.visibility = View.GONE
            holder.removeFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(menu)
        }
        holder.removeFromCart.setOnClickListener {
            holder.removeFromCart.visibility = View.GONE
            holder.addToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(menu)
        }
    }


    class MenuviewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val foodItemName: TextView = view.findViewById(R.id.txtItemName)
        val foodItemCost: TextView = view.findViewById(R.id.txtItemCost)
        val sno: TextView = view.findViewById(R.id.txtSNo)
        val addToCart: Button = view.findViewById(R.id.btnAddToCart)
        val removeFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }



}
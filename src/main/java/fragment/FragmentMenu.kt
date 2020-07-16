package fragment



import activity.CartActivity
import adaptor.Menurecycleradapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arpit.foodproject.R
import com.google.gson.Gson

import database.Fooddatabase
import database.OrderEntity
import model.FoodItem
import util.ConnectionManager
import util.FETCH_RESTAURANTS



class FragmentMenu : Fragment() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var menurecycleradaptor: Menurecycleradapter
    private lateinit var recyclerMenuItems: RecyclerView
    private lateinit var btnGotocart: Button
    private lateinit var progresslayout: RelativeLayout

    lateinit var sharedPreferences: SharedPreferences

    var menuItemList = arrayListOf<FoodItem>()
    var orderList = arrayListOf<FoodItem>()

    @SuppressLint("StaticFieldLeak")
    companion object {
        var resId: Int? = 0
        var resName: String? = ""
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        sharedPreferences = activity?.getSharedPreferences("FoodApp", Context.MODE_PRIVATE) as SharedPreferences

        progresslayout = view.findViewById(R.id.progressLayout)

        progresslayout.visibility = View.VISIBLE

        resId = arguments?.getInt("id", 0)
        resName = arguments?.getString("name", "")



        btnGotocart = view.findViewById(R.id.btnGoToCart)
        btnGotocart.visibility = View.GONE
        btnGotocart.setOnClickListener {
          proceedToCart()
        }

        setUpMenu(view)
        return view
    }

    private fun setUpMenu(view: View) {

        layoutManager = LinearLayoutManager(activity)
        recyclerMenuItems = view.findViewById(R.id.recyclerMenuItems)

        val queue = Volley.newRequestQueue(activity as Context)
        if (ConnectionManager().isNetworkAvailable(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, FETCH_RESTAURANTS + resId, null,
                    Response.Listener {

                progresslayout.visibility = View.GONE

                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {

                        val resArray = data.getJSONArray("data")
                        for (i in 0 until resArray.length()) {
                            val resObject = resArray.getJSONObject(i)

                            val restaurant = FoodItem(
                                    resObject.getString("id"),
                                    resObject.getString("name"),
                                    resObject.getString("cost_for_one").toInt()
                            )

                            menuItemList.add(restaurant)

                            menurecycleradaptor = Menurecycleradapter(activity as Context, menuItemList,
                                    object : Menurecycleradapter.OnItemClickListener {
                                        override fun onAddItemClick(foodItem: FoodItem) {
                                            orderList.add(foodItem)
                                            if (orderList.size > 0) {

                                                btnGotocart.visibility = View.VISIBLE
                                                Menurecycleradapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(foodItem: FoodItem) {
                                            orderList.remove(foodItem)
                                            if (orderList.isEmpty()) {
                                                btnGotocart.visibility = View.GONE
                                                Menurecycleradapter.isCartEmpty = true
                                            }
                                        }

                                    }

                            )

                            recyclerMenuItems.adapter = menurecycleradaptor
                            recyclerMenuItems.layoutManager = layoutManager

                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                }


            },
                    Response.ErrorListener {
                        if (activity != null) {
                        Toast.makeText(
                                activity as Context,
                                "Volley error occur",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                    }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "d18f4285586317"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        } else {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not found")
            dialog.setPositiveButton("Open Setting")
            { text, Listner ->

                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit the app") { text, Listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


    }

    private fun proceedToCart() {

        /*Here we see the implementation of Gson.
        * Whenever we want to convert the custom data types into simple data types
        * which can be transferred across for utility purposes, we will use Gson*/
        val gson = Gson()

        /*With the below code, we convert the list of order items into simple string which can be easily stored in DB*/
        val foodItems = gson.toJson(orderList)

        val async = ItemsOfCart(activity as Context, resId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putInt("resId", resId as Int)
            data.putString("resName", resName)
            val intent = Intent(activity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                    .show()
        }

    }


        class ItemsOfCart(
                context: Context,
                val restaurantId: String,
                val foodItems: String,
                val mode: Int
        ) : AsyncTask<Void, Void, Boolean>() {
            val db = Room.databaseBuilder(context, Fooddatabase::class.java, "res-db").build()


            override fun doInBackground(vararg params: Void?): Boolean {
                when (mode) {
                    1 -> {
                        db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                        db.close()
                        return true
                    }

                    2 -> {
                        db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                        db.close()
                        return true
                    }
                }

                return false
            }

        }

    }






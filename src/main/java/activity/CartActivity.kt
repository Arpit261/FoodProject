package activity


import adaptor.CartAdaptor
import adaptor.Menurecycleradapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
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
import fragment.FragmentMenu
import model.FoodItem
import org.json.JSONArray
import org.json.JSONObject
import util.PLACE_ORDER

class CartActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerCart: RecyclerView
    private lateinit var cartItemAdapter: CartAdaptor
    private var orderList = ArrayList<FoodItem>()
    private lateinit var txtResName: TextView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar:ProgressBar
    private lateinit var btnPlaceOrder: Button

    private var resId: Int = 0
    private var resName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        
        progressLayout= findViewById(R.id.progresslayout)
        progressLayout.visibility=View.VISIBLE
        progressBar = findViewById(R.id.progressBar)

        progressBar.visibility = View.GONE


        init()
        setupToolbar()
        setUpCartList()
        setUpOrder()
    }


    private fun init() {


        txtResName = findViewById(R.id.txtCartResName)
        txtResName.text = FragmentMenu.resName

        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getInt("resId", 0) as Int
        resName = bundle.getString("resName", " ") as String
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    private fun setUpCartList() {
        recyclerCart = findViewById(R.id.recyclerCartItems)
        val dbList = GetItemsFromDBAsync(applicationContext).execute().get()

        /*Extracting the data saved in database and then using Gson to convert the String of food items into a list
        * of food items*/
        for (element in dbList) {
            orderList.addAll(
                    Gson().fromJson(element.foodItems, Array<FoodItem>::class.java).asList()
            )
        }

        /*If the order list extracted from DB is empty we do not display the cart*/
        if (orderList.isEmpty()) {
            progressBar.visibility = View.GONE
            progressLayout.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.VISIBLE
            progressLayout.visibility = View.GONE
        }

        /*Else we display the cart using the cart item adapter*/
        cartItemAdapter = CartAdaptor(orderList, this@CartActivity)
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCart.layoutManager = mLayoutManager
        recyclerCart.adapter = cartItemAdapter
    }

    private fun setUpOrder() {
        btnPlaceOrder = findViewById(R.id.btnplaceorder)

        /*Before placing the order, the user is displayed the price or the items on the button for placing the orders*/
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            sendServerRequest()
        }
    }

    private fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)

        /*Creating the json object required for placing the order*/
        val jsonParams = JSONObject()
        jsonParams.put(
                "user_id",
                this@CartActivity.getSharedPreferences("EatFood", Context.MODE_PRIVATE).getString("user_id", null) as String)

        jsonParams.put("restaurant_id", FragmentMenu.resId?.toString() as String)

        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        jsonParams.put("total_cost", sum.toString())

        val foodArray = JSONArray()

        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].id)
            foodArray.put(i, foodId)
        }
        jsonParams.put("food", foodArray)

        val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, PLACE_ORDER, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        /*If order is placed, clear the DB for the recently added items
                        * Once the DB is cleared, notify the user that the order has been placed*/
                        if (success) {

                        ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            Menurecycleradapter.isCartEmpty = true

                            val dialog= Dialog(this , android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                            dialog.setContentView(R.layout.order_placed)
                            dialog.show()
                            dialog.setCancelable(false)

                            val button = dialog.findViewById<Button>(R.id.btnok)
                            button.setOnClickListener{
                                dialog.dismiss()
                               val intent = Intent(this , LoginActivity::class.java)
                                startActivity(intent)
                                ActivityCompat.finishAffinity(this@CartActivity)
                            }


                        
                        } else {
                            progressBar.visibility = View.VISIBLE
                            Toast.makeText(this@CartActivity, "Some Error occurred", Toast.LENGTH_SHORT)
                                    .show()
                        }

                    } catch (e: Exception) {
                        progressBar.visibility = View.VISIBLE
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    progressBar.visibility = View.VISIBLE
                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "d18f4285586317"
                        return headers

                    }
                }

        queue.add(jsonObjectRequest)

    }


    /*Asynctask class for extracting the items from the database*/
    class GetItemsFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, Fooddatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }

    }

    /*Asynctask class for clearing the recently added items from the database*/
    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, Fooddatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

    /*When the user presses back, we clear the cart so that when the returns to the cart, there is no
    * redundancy in the entries*/
    override fun onSupportNavigateUp(): Boolean {
        ClearDBAsync(applicationContext, resId.toString()).execute().get()
        Menurecycleradapter.isCartEmpty = true
        onBackPressed()
        return true
    }


}

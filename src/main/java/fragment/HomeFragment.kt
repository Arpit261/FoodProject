package fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import adaptor.HomeRecyclerAdaptor
import androidx.recyclerview.widget.DefaultItemAnimator
import com.android.volley.Request
import com.android.volley.VolleyError

import com.arpit.foodproject.R
import model.Foods
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager
import util.FETCH_RESTAURANTS
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {
    private lateinit var recyclerRestaurants : RecyclerView
    private lateinit var layoutManager :RecyclerView.LayoutManager
    lateinit var homeRecyclerAdaptor: HomeRecyclerAdaptor
    private lateinit var progressbar: ProgressBar
    private lateinit var progressLayout: RelativeLayout

    val foodInfoList = arrayListOf<Foods>()
   private val ratingcomparator = Comparator<Foods>{food1 ,food2 ->
       if (food1.rating.compareTo(food2.rating , true)==0){
           food1.name.compareTo(food2.name,true)
       }else{
           food1.rating.compareTo(food2.rating , true)
       }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)


            progressLayout = view.findViewById(R.id.progressLayout)
            progressbar = view.findViewById(R.id.progressBar)
            progressLayout.visibility = View.VISIBLE

            setUpRecycler(view)
            setHasOptionsMenu(true)
            return view
    }

        private fun setUpRecycler(view: View) {

        recyclerRestaurants = view.findViewById(R.id.recyclerRestaurants)
        layoutManager = LinearLayoutManager(activity)

         val queue = Volley.newRequestQueue(activity as Context)

         if (ConnectionManager().isNetworkAvailable(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, FETCH_RESTAURANTS, null,
                Response.Listener {

                    progressLayout.visibility = View.GONE

                    /*Once response is obtained, parse the JSON accordingly*/
                         try {
                            val data = it.getJSONObject("data")
                             val success = data.getBoolean("success")
                                if (success) {

                                  val resArray = data.getJSONArray("data")
                                  for (i in 0 until resArray.length()) {
                                      val resObject = resArray.getJSONObject(i)


                                      val restaurant = Foods(
                                     resObject.getString("id").toInt(),
                                     resObject.getString("name"),
                                     resObject.getString("rating"),
                                     resObject.getString("cost_for_one").toInt(),
                                     resObject.getString("image_url")
                                        )
                                      if (activity !=null) {
                                          foodInfoList.add(restaurant)
                                          homeRecyclerAdaptor = HomeRecyclerAdaptor(foodInfoList, activity as Context)
                                          recyclerRestaurants.adapter = homeRecyclerAdaptor
                                          recyclerRestaurants.layoutManager = layoutManager
                                      }
                            }
                        }


                    } catch (e: JSONException) {
                             e.printStackTrace()
                        Toast.makeText(
                            activity as Context,
                            "Some unaccepted error occur",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                },
                Response.ErrorListener {
                        Toast.makeText(
                                activity as Context,
                                "Volley error occur",
                                Toast.LENGTH_SHORT
                        ).show()


                })



            {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menuhome , menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if (id==R.id.actionsort){
            Collections.sort(foodInfoList , ratingcomparator)
            foodInfoList.reverse()
        }
        homeRecyclerAdaptor.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }



}

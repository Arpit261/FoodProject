package activity



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.arpit.foodproject.R
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager
import util.LOGIN
import util.Validation
import java.util.HashMap


class LoginActivity : AppCompatActivity() {

    /*Declaring all the views present in the activity_login.xml file*/
     lateinit var etmobilenumberlogin: EditText
     lateinit var etpasswordlogin: EditText
     lateinit var btnlogin: Button
     lateinit var txtforgetpasswordlogin: TextView
    lateinit var txtregisteraccountlogin: TextView


    lateinit var sharedPreferences: SharedPreferences


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            sharedPreferences=getSharedPreferences(getString(R.string.pref_name) , Context.MODE_PRIVATE)
            /*   Initialising User login value true */

            val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn" ,false)
            if (isLoggedIn){
                val intent =  Intent(this,DashBoardActivity::class.java)
                startActivity(intent)
            }else {

                setContentView(R.layout.login_activity)
            }

            /*Initialising the views*/
            etmobilenumberlogin = findViewById(R.id.etmobilenumberlogin)
            etpasswordlogin = findViewById(R.id.etpasswordlogin)
            btnlogin = findViewById(R.id.btnlogin)
            txtforgetpasswordlogin = findViewById(R.id.txtforgetpasswordlogin)
            txtregisteraccountlogin = findViewById(R.id.txtregisteraccountlogin)



                /*Start the login process when the user clicks on the login button*/
                btnlogin.setOnClickListener {

                  /*Hide the login button when the process is going on*/
                    btnlogin.visibility = View.INVISIBLE


                    /*First validate the mobile number and password length*/
                    if(Validation.validatemobilelength(etmobilenumberlogin.text.toString())&&Validation.validatepasswordlength(etpasswordlogin.text.toString())) {

                        if (ConnectionManager().isNetworkAvailable(this@LoginActivity)) {

                            /*Create the queue for the request*/
                            val queue = Volley.newRequestQueue(this@LoginActivity)

                            /*Create the JSON parameters to be sent during the login process*/
                            val jsonParams = JSONObject()
                            jsonParams.put("mobile_number", etmobilenumberlogin.text.toString())
                            jsonParams.put("password", etpasswordlogin.text.toString())

                            /*Finally send the json object request*/
                            val jsonObjectRequest =
                            object : JsonObjectRequest(Method.POST, LOGIN, jsonParams, Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        val response = data.getJSONObject("data")

                                        sharedPreferences.edit().putString("user_id", response.getString("user_id")).apply()
                                        sharedPreferences.edit().putString("user_name", response.getString("name")).apply()
                                        sharedPreferences.edit().putString("user_email", response.getString("email")).apply()
                                        sharedPreferences.edit().putString("user_mobile", response.getString("mobile_number")).apply()
                                        sharedPreferences.edit().putString("user_address", response.getString("address")).apply()

                                        savePreferenece()
                                        val intent = Intent(
                                                this@LoginActivity,
                                                DashBoardActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()


                                    } else {
                                        btnlogin.visibility = View.VISIBLE
                                        txtforgetpasswordlogin.visibility = View.VISIBLE
                                        btnlogin.visibility = View.VISIBLE
                                        Toast.makeText(this@LoginActivity, "Some unexpected error occur", Toast.LENGTH_SHORT).show()
                                    }

                                } catch (e: JSONException) {
                                    btnlogin.visibility = View.VISIBLE
                                    txtforgetpasswordlogin.visibility = View.VISIBLE
                                            txtregisteraccountlogin.visibility = View.VISIBLE

                                    Toast.makeText(this@LoginActivity, "Volley Error occured", Toast.LENGTH_SHORT).show()
                                }


                            }, Response.ErrorListener {
                                btnlogin.visibility = View.VISIBLE
                                txtforgetpasswordlogin.visibility = View.VISIBLE
                                txtregisteraccountlogin.visibility = View.VISIBLE
                                Log.e("Error::::", "/post request fail! Error: ${it.message}")
                                Toast.makeText(this@LoginActivity, "Some error occured", Toast.LENGTH_SHORT)
                                        .show()

                            }) {
                                override fun getHeaders(): MutableMap<String, String> {

                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "d18f4285586317"
                                    return headers
                                }

                                }
                                queue.add(jsonObjectRequest)



                            } else {
                            btnlogin.visibility = View.VISIBLE
                            txtforgetpasswordlogin.visibility = View.VISIBLE
                            txtregisteraccountlogin.visibility = View.VISIBLE
                            Toast.makeText(this@LoginActivity, "No internet Connection", Toast.LENGTH_SHORT)
                                    .show()
                        }

                        }
                        else{
                        btnlogin.visibility = View.VISIBLE
                        txtforgetpasswordlogin.visibility = View.VISIBLE
                        txtregisteraccountlogin.visibility = View.VISIBLE

                    }

             }




        /*Clicking on this text takes you to the register activity*/
        txtregisteraccountlogin.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )

        }
        /*Clicking on this text takes you to the forgot password activity*/
        txtforgetpasswordlogin.setOnClickListener {
            startActivity  ( Intent(this@LoginActivity ,
                ForgetActivity::class.java ))
        }


    }


    fun  savePreferenece(){
        sharedPreferences.edit().putBoolean("isLoggedIn" ,true).apply()
     }



}


package activity

/* The registration activity is responsible for registering the users to the app
* This will send the fields to server and the user will get registered if all the fields were correct.
* The user receives response in the form of JSON
* If the login is true, the user is navigated to the dashboard else appropriate error message is displayed*/




import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arpit.foodproject.R
import org.json.JSONException

import org.json.JSONObject
import util.ConnectionManager
import util.REGISTER
import util.Validation
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var etnameregister: EditText
    private lateinit var etemailregister: EditText
    private lateinit var etmobilenumberregister: EditText
    private lateinit var etdeliveryregister: EditText
    private lateinit var etpasswordregister: EditText
    private lateinit var etconfirmregister: EditText
    private lateinit var btnregister: Button
    lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences=getSharedPreferences(getString(R.string.pref_name) ,Context.MODE_PRIVATE)
        setContentView(R.layout.registration)

        etnameregister = findViewById(R.id.etnameregister)
        etemailregister = findViewById(R.id.etemailregister)
        etmobilenumberregister = findViewById(R.id.etmobilenumberregister)
        etdeliveryregister = findViewById(R.id.deliveryregister)
        etpasswordregister = findViewById(R.id.etpasswordregister)
        etconfirmregister = findViewById(R.id.etconfirmregister)
        btnregister = findViewById(R.id.btnregister)






        btnregister.setOnClickListener {

            if (Validation.validatenamelength(etnameregister.text.toString())) {
                etnameregister.error = null
                if (Validation.validateemail(etemailregister.text.toString())) {
                    etemailregister.error = null
                    if (Validation.validatemobilelength(etmobilenumberregister.text.toString())) {
                        etmobilenumberregister.error = null
                        if (Validation.validatepasswordlength(etpasswordregister.text.toString())) {
                            etpasswordregister.error = null
                            if (Validation.matchpassword(
                                    etpasswordregister.text.toString(),
                                    etconfirmregister.text.toString()
                                )
                            ) {
                                etpasswordregister.error = null
                                etconfirmregister.error = null


                                if (ConnectionManager().isNetworkAvailable(this@RegisterActivity)) {
                                    sendRegisterRequest(
                                        etnameregister.text.toString(),
                                        etmobilenumberregister.text.toString(),
                                        etdeliveryregister.text.toString(),
                                        etemailregister.text.toString(),
                                        etpasswordregister.text.toString()

                                    )
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "No Internet Connection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {
                                etpasswordregister.error = "Incorrect Password"
                                etconfirmregister.error = "Incorrect Password"
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Password doesn't match",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            etpasswordregister.error =
                                "Password should be more than or equal to 4 digits"
                            Toast.makeText(
                                this@RegisterActivity,
                                "Password should be more than or equal to 4 digits",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } else {
                        etmobilenumberregister.error = "Invalid mobile number"
                        Toast.makeText(
                            this@RegisterActivity,
                            "Invalid mobile number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    etemailregister.error = "Invalid email"
                    Toast.makeText(this@RegisterActivity, "Invalid email", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                etnameregister.error = "Name should be greater than or equal to three"
                Toast.makeText(
                    this@RegisterActivity,
                    "Name should be greater than or equal to three",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }

    private fun sendRegisterRequest(
        name: String,
        mobile: String,
        address: String,
        email: String,
        password: String
    ) {


        val queue = Volley.newRequestQueue(this@RegisterActivity)

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)


        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, REGISTER, jsonParams, Response.Listener {

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



                        val intent = Intent(
                            this@RegisterActivity,
                            DashBoardActivity::class.java
                        )
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Some unexpected error occur",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                } catch (e: JSONException) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Volley Error occured",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }


            }, Response.ErrorListener {
                Toast.makeText(this@RegisterActivity, "Some error occured", Toast.LENGTH_SHORT)
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
    }

    override fun onSupportNavigateUp(): Boolean {
        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
        onBackPressed()
        return true
    }


}

package activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.arpit.foodproject.R
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager
import util.FORGET
import util.Validation
import java.util.HashMap
/*In this activity, we will send the email and the mobile number to the server.
 Kindly use the registered email and mobile number only, otherwise the OTP won't be sent to your email id.
 The received OTP will be valid for next 24 hours and you will not receive more than one email per day*/

/*Once the OTP is shared to you on your registered email id, you can then send it along with the mobile number\
* and the new password. The mobile number can be stored in an intent and sent to the next activity from here itself*/

class ForgetActivity : AppCompatActivity() {


    private lateinit var etemailforget: EditText
    private lateinit var etmobilenumberforget: EditText
    private lateinit var btnnextforget: Button
    private lateinit var r1progresslayout:RelativeLayout
    private lateinit var progressbar:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgetpassword)

        etemailforget = findViewById(R.id.etemailforget)
        etmobilenumberforget = findViewById(R.id.etmobilenumberforget)
        btnnextforget = findViewById(R.id.btnnextforget)

        r1progresslayout = findViewById(R.id.progressLayout)
        progressbar = findViewById(R.id.progressBar)
        r1progresslayout.visibility = View.VISIBLE
        progressbar.visibility = View.GONE




        btnnextforget.setOnClickListener {

            if (Validation.validateemail(etemailforget.text.toString())) {
                etemailforget.error = null

                if (Validation.validatemobilelength(etmobilenumberforget.text.toString())) {
                    etmobilenumberforget.error = null


                    if (ConnectionManager().isNetworkAvailable(this@ForgetActivity)) {
                        r1progresslayout.visibility = View.GONE
                        progressbar.visibility = View.VISIBLE

                        sendForgetRequest(
                                etemailforget.text.toString(),
                                etmobilenumberforget.text.toString()
                        )

                    } else {
                        r1progresslayout.visibility = View.GONE
                        progressbar.visibility = View.VISIBLE

                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()

                    }
                } else {
                    r1progresslayout.visibility = View.GONE
                    progressbar.visibility = View.VISIBLE
                    etmobilenumberforget.error = "Invalid No"
                    Toast.makeText(this, "Invalid No", Toast.LENGTH_SHORT).show()
                }

            } else {
                r1progresslayout.visibility = View.GONE
                progressbar.visibility = View.VISIBLE
                etemailforget.error = "Invalid Email"
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }


        }
    }


    private fun sendForgetRequest(email: String, mobile: String) {

        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("email", email)

        val jsonObject = object : JsonObjectRequest(Method.POST, FORGET, jsonParams, Response.Listener {

            try {
                val data = it.getJSONObject("data")

                val success = data.getBoolean("success")

                if (success) {
                    val firstTry = data.getBoolean("first_try")

                    if (firstTry) {
                        Toast.makeText(this, "Please check you registered email for OTP", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ResetActivity::class.java)
                        intent.putExtra("user_mobile", mobile)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, "Please check your Previous registered email", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ResetActivity::class.java)
                        intent.putExtra("user_mobile", mobile)
                        startActivity(intent)

                    }

                } else {
                    r1progresslayout.visibility = View.GONE
                    progressbar.visibility = View.VISIBLE
                    Toast.makeText(this@ForgetActivity, "Mobile no. not registered ", Toast.LENGTH_SHORT).show()
                }

            } catch (e: JSONException) {
                e.printStackTrace()
                r1progresslayout.visibility = View.GONE
                progressbar.visibility = View.VISIBLE
                Toast.makeText(this@ForgetActivity, "Some volley exception here!!", Toast.LENGTH_SHORT).show()
            }

        },
                Response.ErrorListener {
                    r1progresslayout.visibility = View.GONE
                    progressbar.visibility = View.VISIBLE
                    Toast.makeText(
                            this@ForgetActivity,
                            "Some  error occured",
                            Toast.LENGTH_SHORT
                    ).show()

                }) {
            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "d18f4285586317"
                return headers
            }

        }

        queue.add(jsonObject)
    }
}

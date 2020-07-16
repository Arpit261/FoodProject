package activity



import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation
import android.view.View
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arpit.foodproject.R
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager
import util.RESET
import util.Validation
import java.util.HashMap
/*The reset password activity resets the password, by sending the OTP along with the mobile number and the new password*/

      class ResetActivity : AppCompatActivity() {

        private lateinit var etotp: EditText
        private lateinit var etnewpasswordreset: EditText
        private lateinit var etconfirmpasswordreset: EditText
        private lateinit var buttonsumbitreset: Button
        private lateinit var mobileNumber :String

        private lateinit var r1progresslayout:RelativeLayout
        private lateinit var progressbar:ProgressBar

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

           etotp = findViewById(R.id.etotp)
          etnewpasswordreset = findViewById(R.id.etnewpasswordreset)
          etconfirmpasswordreset = findViewById(R.id.etconfirmpasswordreset)
          buttonsumbitreset = findViewById(R.id.btnsumbitreset)

           r1progresslayout = findViewById(R.id.progressLayout)
            progressbar =findViewById(R.id.progressBar)

            r1progresslayout.visibility = View.VISIBLE
            progressbar.visibility = View.GONE



          if (intent != null){
            mobileNumber = intent.getStringExtra("user_mobile")as String
          }

                 buttonsumbitreset.setOnClickListener {
                    r1progresslayout.visibility = View.GONE
                    progressbar.visibility = View.VISIBLE

                    if (ConnectionManager().isNetworkAvailable(this@ResetActivity)) {
                    if (etotp.text.length==4) {
//                        etotp.error = null
                     if (Validation.validatepasswordlength(etnewpasswordreset.text.toString())) {
//                     etnewpasswordreset.error = null

                       if (Validation.matchpassword(etnewpasswordreset.text.toString(), etconfirmpasswordreset.text.toString())) {
//                         etnewpasswordreset.error = null
//                         etconfirmpasswordreset.error = null

                             sendRequestReset(
                                     mobileNumber,
                                     etotp.text.toString(),
                                     etnewpasswordreset.text.toString()

                             )
                            }
                            else {
                              r1progresslayout.visibility = View.VISIBLE
                              progressbar.visibility = View.GONE
                             Toast.makeText(this, "Password does't match", Toast.LENGTH_SHORT).show()

                          }
                          }
                     else {
                           r1progresslayout.visibility = View.VISIBLE
                           progressbar.visibility = View.GONE
//                         etnewpasswordreset.error = "Password does't match"
//                         etconfirmpasswordreset.error = "Password does't match"
                         Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                         }

                     } else {
                         r1progresslayout.visibility = View.VISIBLE
                         progressbar.visibility = View.GONE
//                     etotp.error = "OTP should be greater than 4"
                     Toast.makeText(this, "OTP should be greater than 4", Toast.LENGTH_SHORT).show()
                     }
                    }  else
                    {
                        r1progresslayout.visibility = View.VISIBLE
                        progressbar.visibility = View.GONE
                    Toast.makeText(this , "No internet connection" , Toast.LENGTH_SHORT).show()
                    }

                }
            }


             private fun sendRequestReset(mobile: String, password: String, otp: String) {

             val queue = Volley.newRequestQueue(this)
             val jsonParams = JSONObject()
             jsonParams.put("mobile_number", mobile)
             jsonParams.put("password", password)
             jsonParams.put("otp", otp)

             val jsonObjectRequest = object : JsonObjectRequest(Method.POST, RESET, jsonParams, Response.Listener {

                try {
                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")

                    if (success) {
                        progressbar.visibility = View.INVISIBLE

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    Toast.makeText(this, "Password Successfully Changed", Toast.LENGTH_SHORT).show()


                           }
                    else {
                        r1progresslayout.visibility = View.VISIBLE
                        progressbar.visibility = View.GONE
                          Toast.makeText(
                            this,
                            "Some error occured",
                            Toast.LENGTH_SHORT
                        ).show()
                        }

                    } catch (e: JSONException) {
                    e.printStackTrace()
                    r1progresslayout.visibility = View.VISIBLE
                    progressbar.visibility = View.GONE
                    Toast.makeText(
                        this, "Some volley error occured", Toast.LENGTH_SHORT).show()
            }

            }, Response.ErrorListener {
                 r1progresslayout.visibility = View.VISIBLE
                 progressbar.visibility = View.GONE
                 Toast.makeText(
                    this,
                    "some unaccecpted occured",
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
    }

}

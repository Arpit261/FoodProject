package activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.arpit.foodproject.R


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Handler().postDelayed({
            val startAct = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(startAct)
        }, 1000)
        setContentView(R.layout.splash_activity)


    }
    /*Lifecycle method. Here the finish() ensures that the activity does not open again when the user presses back button*/
    override fun onPause() {
        super.onPause()
        finish()
    }
}


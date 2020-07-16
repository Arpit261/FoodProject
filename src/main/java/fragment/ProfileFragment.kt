package fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

import com.arpit.foodproject.R
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    lateinit var txtusername: TextView
    lateinit var txtphone:TextView
    lateinit var txtemail:TextView
    lateinit var txtaddress:TextView

    lateinit var sharedperference:SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view  = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedperference = (activity as FragmentActivity).getSharedPreferences("FoodApp", Context.MODE_PRIVATE)


            txtusername=view.findViewById(R.id.txtUserName)
             txtphone=view.findViewById(R.id.txtPhone)
            txtaddress=view.findViewById(R.id.txtAddress)
            txtemail=view.findViewById(R.id.txtEmail)


        txtUserName.text = sharedperference.getString("user_name", null)
        val phoneText = "+91-${sharedperference.getString("user_mobile", null)}"
        txtPhone.text = phoneText
        txtEmail.text = sharedperference.getString("user_email", null)
        val address = sharedperference.getString("user_address", null)
        txtAddress.text = address


        return view

    }

}

package fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

import com.arpit.foodproject.R


class ProfileFragment : Fragment() {

    lateinit var txtusername: TextView
    lateinit var txtphone:TextView
    lateinit var txtemail:TextView
    lateinit var txtaddress:TextView
    lateinit var imgUser:ImageView
    lateinit var sharedperference:SharedPreferences




    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


       sharedperference = (activity as FragmentActivity).getSharedPreferences(getString(R.string.pref_name) , Context.MODE_PRIVATE)


            txtusername=view.findViewById(R.id.txtUserName)
            txtphone=view.findViewById(R.id.txtPhone)
             txtemail=view.findViewById(R.id.txtEmail)
            txtaddress=view.findViewById(R.id.txtAddress)
          imgUser = view.findViewById(R.id.imgUserImageprofile)

                txtusername.text = " ${sharedperference.getString("user_name" ," ")}"
                txtphone.text= " ${sharedperference.getString("user_mobile" , " ")}"
              txtemail.text=  "  ${sharedperference.getString("user_email" ," ")}"
             txtaddress.text= "  ${sharedperference.getString("user_address" , " ")}"
        return view

    }

    }


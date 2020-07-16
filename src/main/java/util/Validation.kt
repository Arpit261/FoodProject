package util

import android.util.Patterns

/*Another utility class similar to Constants which is used to just peform some constant functions
* Here we can create different functions for performing different validations used in our app*/

object Validation {

    fun validatenamelength(name:String):Boolean{
      return name.length >=3
    }
  fun validatemobilelength(mobile:String):Boolean{
      return  mobile.length ==10
  }
    fun validatepasswordlength(password:String):Boolean{
        return  password.length >=4
    }
   fun matchpassword(pass:String ,confirmPass:String):Boolean{
       return pass==confirmPass
   }
   fun  validateemail(email:String):Boolean{
       return (! email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
   }








}
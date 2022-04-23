package com.yarmouk.bainah.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yarmouk.bainah.R
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterFragment : Fragment(R.layout.register_fragment) {

    //initialize the variables that will be used in this fragment
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Define auth variable with firebase auth object
        //Firebase.auth will give us instance of our firebase authentication database
        //By using this we can do the following
        //Login existing users, SingUp new users, Logout users (All authentication actions)
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Handle click on login button in the design
        btnGoToLogin.setOnClickListener {
            //Take the use back to login screen
            findNavController().popBackStack()
        }

        //Handle click on register button in the design
        btnRegister.setOnClickListener {
            //Get email and password and userName that user entered in email editText and password editText and userName editText
            val email = etEmail.text.toString()
            val password  =etPassword.text.toString()
            val userName = etUserName.text.toString()

            //Check if the email is valid
            //Email in valid when it's contains @ and .com or any domain
            //Patterns.EMAIL_ADDRESS do that for us
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                showErrorSnackBar("Please enter valid email")
            }
            //Check if the password is valid
            //password in valid when it's length more than 6
            else if(password.length < 6){
                showErrorSnackBar("Please enter valid password")
            }
            //Check if the userName is valid
            //userName in valid when it's length more than 4
            else if(userName.length < 4){
                showErrorSnackBar("Please enter valid name")
            }
            //Here the user entered valid email, password and userName
            else{
               //Call signUpNewUser function and pass to it email and password
               signUpNewUser(email,password)
            }

        }

    }

    //This function will take two parameters email and password
    //Then it will create new user account using passed email
    private fun signUpNewUser(email:String,password:String){
        //First we show loading indicator to notify user that we are creating his account
        showLoading()

        //Then we use createUserWithEmailAndPassword function from firebase authentication
        //This function will create new user account with passed email and store it to firebase authentication database
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                //Here creating account is done successfully and we can navigate user to home_fragment
                //Also we hide loading indicator
                hideLoading()
                if(task.isSuccessful){
                    updateUi(auth.currentUser)
                }
            }
            .addOnFailureListener { exception ->
                //Here creating account fail and we show the user error messages
                //Also hide loading indicator
                hideLoading()
                if(exception.message?.contains("email address")!!){
                    showErrorSnackBar("This email is already registered")
                }
                else{
                    showErrorSnackBar("Unknown_error")
                }

            }
    }

    //Navigate user from registration_fragment to home_fragment
    private fun updateUi(currentUser: FirebaseUser?){
        if(currentUser != null){
            findNavController().navigate(R.id.action_registerFragment_to_fragmentHome)
        }
    }

    private fun showLoading() {
        allViews.visibility = View.GONE
        progressBar2.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        allViews.visibility = View.VISIBLE
        progressBar2.visibility = View.GONE
    }

    private fun showErrorSnackBar(message:String){
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
            .show()
    }

}
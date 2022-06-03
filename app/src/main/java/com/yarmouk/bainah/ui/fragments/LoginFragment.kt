package com.yarmouk.bainah.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yarmouk.bainah.R
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment:Fragment(R.layout.login_fragment) {

    //initialize the variables that will be used in this fragment
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Handle on back press when the user click back in this page close the app
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        //Define auth variable with firebase auth object
        //Firebase.auth will give us instance of our firebase authentication database
        //By using this we can do the following
        //Login existing users, SingUp new users, Logout users (All authentication actions)
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Handle the click on registration button in the design
        btnGoToRegister.setOnClickListener {
            //Navigate to registration fragment
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())

        }

        //Handle the click on login button in the design
        btnLogin.setOnClickListener {
            //Get email and password that user entered in email editText and password editText
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            //Check if the email is valid
            //Email in valid when it's contains @ and .com or any domain
            //Patterns.EMAIL_ADDRESS do that for us
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Snackbar.make(requireView(),"Please enter valid email", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.red))
                    .show()
            }
            //In this block user entered valid email now we have to log him
            else{
                //First thing we show loading indicator to notify user that we are logging him
                showLoading()

                //Here we user signInWithEmailAndPassword from firebase
                //Takes email and password and login user
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        //Here user entered correct email and password so we will log him
                        //And hide loading indicator
                        hideLoading()
                        if (task.isSuccessful) {
                            //this function will navigate user from login_fragment to home_fragment
                            updateUi(auth.currentUser)
                        }
                    }.addOnFailureListener {
                        //Here user entered wrong email or password
                        //We show him error message based on the error
                        //Also hide loading indicator
                        hideLoading()
                        when {
                            it.message?.contains("password")!! -> {
                                showErrorSnackBar("Error: Wrong password")
                            }
                            it.message?.contains("record")!! -> {
                                showErrorSnackBar("Error: No registered account with this email")
                            }
                            else -> {
                                showErrorSnackBar("Error: Unknown error")
                            }
                        }
                    }
            }

        }
    }

    private fun updateUi(currentUser: FirebaseUser?){
        if(currentUser != null) {
            if(rbPolice.isChecked){
                findNavController().navigate(R.id.action_loginFragment_to_policeDashboardFragment)
            }
            else{
                findNavController().navigate(R.id.action_loginFragment_to_fragmentHome)
            }

        }
        else{
            showErrorSnackBar("Please login or sign up")
        }
    }

    private fun showLoading(){
        allViews.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        allViews.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showErrorSnackBar(message: String){
        Snackbar.make(requireView(),message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.red))
            .show()
    }

}
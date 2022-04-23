package com.yarmouk.bainah.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yarmouk.bainah.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.change_language_dialog.view.*
import kotlinx.android.synthetic.main.settings_fragment.*

@AndroidEntryPoint
class SettingsFragment:Fragment(R.layout.settings_fragment) {

    //initialize the variables that will be used in this fragment
    private lateinit var auth:FirebaseAuth

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

        //Handle click on changeLanguage button
        changeLanguage.setOnClickListener {
            showChangeLanguageDialog()
        }

        //Handle click on aboutThisApp button
        aboutThisApp.setOnClickListener {
            showAboutDialog()
        }

        //Handle click on logout button
        logout.setOnClickListener {
            showLogoutDialog()
        }

    }

    private fun showLogoutDialog(){
        //Create dialog with title (Logout) and message (Are Are you sure you want to log out?)
        //And two buttons yes (Will logout user), No(Will dismiss the dialog)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                //User click yes
                //Here we call signOut() function from firebase authentication which will log out the user
                //Then we navigate to login_fragment
                //finally dismiss the dialog
                auth.signOut()
                updateUi(auth.currentUser)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                //user click no just dismiss the dialog
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("InflateParams")
    private fun showChangeLanguageDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).create()
        val layout = layoutInflater.inflate(R.layout.change_language_dialog, null)
        layout.languageButtons.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.arabicLanguage -> {
                    changeLanguage("ar")
                }
                R.id.englishLanguage -> {
                    changeLanguage("en")
                }
            }
        }
        layout.tvCancelLanguageDialog.setOnClickListener { dialog.dismiss() }
        dialog.setView(layout)
        dialog.show()

    }

    private fun showAboutDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).create()
        dialog.setTitle(getString(R.string.about_this_app))
        val message = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."
        dialog.setMessage(message)
        dialog.show()
    }

    private fun changeLanguage(lang: String) {
        val editor = activity?.getSharedPreferences("language", Context.MODE_PRIVATE)?.edit()
        editor?.putString("lang", lang)
        editor?.apply()
        activity?.finish()
        startActivity(activity?.intent)
        activity?.overridePendingTransition(0, 0)
    }

    private fun updateUi(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }
    }

}
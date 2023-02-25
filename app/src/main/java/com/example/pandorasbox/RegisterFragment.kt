package com.example.pandorasbox

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.alpha
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterFragment : Fragment() {

    // Variables
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var cnfPassword: EditText

    // Firebase
    private lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_register, container, false)

        // Getting input from user
        username = view.findViewById(R.id.reg_username)
        password = view.findViewById(R.id.reg_password)
        cnfPassword = view.findViewById(R.id.reg_cnf_password)

        // Initialise firebase - add internet perm in manifest file
        fAuth = Firebase.auth

        // Clicking 'register' on login page navigates to register page
        view.findViewById<Button>(R.id.btn_login_reg).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            // Where it goes to - LoginFragment()
            navRegister.navigateFrag(LoginFragment(), false)
        }

        // Clicking 'register' on register page submits
        view.findViewById<Button>(R.id.btn_register_reg).setOnClickListener {
            // Call validate method - can be any name
            validateEmptyForm()
        }
        return view
    }

    private fun firebaseSignUp() {
        // Disabling Register button so user can only click once - code not here
        fAuth.createUserWithEmailAndPassword(
            username.text.toString(),
            // Check if successful
            password.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Use interface to call method and pass Home fragment
                var navHome = activity as FragmentNavigation
                navHome.navigateFrag(HomeFragment(), true)

            } else {
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateEmptyForm() {

        // Warning icon displays when field empty
        val icon = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_warning
        )

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        when {
            // If username field is empty, throw error
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter Username", icon)
            }
            // If password field is empty, throw error
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please Enter Password", icon)
            }
            // If confirm password field is empty, throw error
            TextUtils.isEmpty(cnfPassword.text.toString().trim()) -> {
                cnfPassword.setError("Please Enter Password Again", icon)
            }

            // Checking if values entered are correct
            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfPassword.text.toString().isNotEmpty() -> {

                // Check if format is email
                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    // Happy path
                    // Check password length
                    if (password.text.toString().length >= 5) {
                        // Happy path
                        // Check confirm password field == password field
                        if (password.text.toString() == cnfPassword.text.toString()) {

                            // Register user to Firebase
                            firebaseSignUp()
                            //Toast.makeText(context, "Register Succesful", Toast.LENGTH_SHORT).show()
                        } else {
                            cnfPassword.setError("Password Doesn't Match", icon)
                        }

                    } else {
                        password.setError("Please Enter At Least 5 Characters", icon)
                    }
                } else {
                    username.setError("Please Enter Valid Email", icon)
                }
            }
        }
    }
}
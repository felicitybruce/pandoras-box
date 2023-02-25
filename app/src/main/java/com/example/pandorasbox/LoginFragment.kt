package com.example.pandorasbox

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.pandorasbox.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var username: EditText
    private lateinit var password: EditText

    // Declare firebase auth
    private lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)

        username = view.findViewById(R.id.log_username)
        password = view.findViewById(R.id.log_password)
        fAuth = Firebase.auth

        // When you click 'Register' btn, it calls method
        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            validateForm()
        }

        return view
    }

    private fun firebaseSignIn() {

        fAuth.signInWithEmailAndPassword(username.text.toString(),
            // Check if successful
            password.text.toString()
        ).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                // Happy path
                // Move to home screen
                var navHome = activity as FragmentNavigation
                navHome.navigateFrag(HomeFragment(), true)
            } else {
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm() {

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

            // Checking if values entered are correct
            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() -> {

                // Check if format is email
                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    // Happy path

                    firebaseSignIn()

                    //Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                } else {
                    username.setError("Please Enter Valid Email", icon)
                }
            }
        }
    }

}
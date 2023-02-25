package com.example.pandorasbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), FragmentNavigation {

//    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //  Initialise
//        fAuth = Firebase.auth

        // Check if user exists
//        val currentUser = fAuth.currentUser
//        if (currentUser != null) {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.container, HomeFragment()).addToBackStack(null)
//                .commit()
//
//            // Sad path
//        } else {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.container, LoginFragment())
//                .commit()
//        }


        // Linking LoginFragment to MainActivity so it opens on Loginfragment
        supportFragmentManager.beginTransaction()
            .add(R.id.container, LoginFragment())
            .commit()  // maybe comment out
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            // Will replace from LoginFragment to any fragment which is passed into this method
            // So make it generic 'fragment'
            .beginTransaction()
            .replace(R.id.container, fragment)

        if (addToStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}
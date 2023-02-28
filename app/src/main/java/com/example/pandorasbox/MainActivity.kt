package com.example.pandorasbox

import android.app.DownloadManager.Request
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class MainActivity : AppCompatActivity(), FragmentNavigation {

    // Full URL to retrieve post
    private var url = ""

    // Next page token to get next post
    private var nextToken = ""

    private lateinit var postArrayList: ArrayList<ModelPost>
    private lateinit var adapterPost: AdapterPost

    private lateinit var progressDialog: ProgressDialog

    private val TAG = "MAIN_TAG"
    var moreBtn: ImageButton = findViewById(R.id.moreBtn)
    val postsRv: RecyclerView = findViewById(R.id.postsRv)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")

        // Initialise and clear list before add data to it
        postArrayList = ArrayList()
        postArrayList.clear()

        loadPosts()

        // Handle click + load more posts
        moreBtn.setOnClickListener {
            loadPosts()
        }


        // Linking LoginFragment to MainActivity so it opens on Loginfragment
        supportFragmentManager.beginTransaction()
            .add(R.id.container, LoginFragment())
            .commit()  // maybe comment out
    }

    private fun loadPosts() {
        progressDialog.show()
        url = when (nextToken) {
            "" -> {
                Log.d(TAG, "loadPosts: NextPageToken is empty, more posts")
                ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts?maxResults=${Constants.MAX_RESULTS}&key=${Constants.API_KEY}")
            }
            "end" -> {
                Log.d(TAG, "loadPosts: Next page token is end, no more posts i.e. loaded all posts")
                Toast.makeText(this, "No more posts...", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                return

            }
            else -> {
                Log.d(TAG, "loadPosts: NextPage Token: $nextToken")
                ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts?maxResults=${Constants.MAX_RESULTS}&pageToken=$nextToken&key=${Constants.API_KEY}")

            }
        }
        Log.d(TAG, "loadPosts: URL: $url")


        // Request data, method = GET
//        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
//
//
//        }, {error ->})

        val stringRequest = StringRequest(
            com.android.volley.Request.Method.GET, url, { response ->
                // Handle response
                // Get response here so dismiss dialog first
                progressDialog.dismiss()
                Log.d(TAG, "loadPosts: $response")
                // In tryCatch as JSON data in resp param/variable so could cause error whilst formatting
                try {
                    // Have response ad JSON Obj
                    val jsonObject = JSONObject(response)
                    try {
                        nextToken = jsonObject.getString("nextPageToken")
                        Log.d(TAG, "loadPosts: NextPagetoken: $nextToken")
                    }
                    catch (e:Exception) {
                        Toast.makeText(this, "Reached end of page...", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "loadPosts: Reached end of page...")
                        nextToken = "end"
                    }
                    // grab json array data from json obj
                    val jsonArray = jsonObject.getJSONArray("items")

                    // Loop getting ddata until got all
                    for (i in 0 until jsonArray.length()){
                        try {
                            val jsonObject01 = jsonArray.getJSONObject(i)
                            val id = jsonObject01.getString("id")
                            val title = jsonObject01.getString("title")
                            val content = jsonObject01.getString("content")
                            val published = jsonObject01.getString("published")
                            val updated = jsonObject01.getString("updated")
                            val url = jsonObject01.getString("url")
                            val selfLink = jsonObject01.getString("selfLink")
                            val authorName = jsonObject01.getJSONObject("author").getString("displayName")
                            //val image = jsonObject01.getJSONObject("author").getString("image")

                            // Set data
                            val modelPost = ModelPost(
                                "$authorName",
                                "$content",
                                "$id",
                                "$published",
                                "$selfLink",
                                "$title",
                                "$updated",
                                "$url"
                            )
                            // Add data to list
                            postArrayList.add(modelPost)
                        }
                        catch (e:Exception){
                            Log.d(TAG, "loadPosts: 1 ${e.message}")
                            Toast.makeText(this, "{ e.message }", Toast.LENGTH_SHORT).show()



                        }
                    }
                    // Set up adapter
                    adapterPost = AdapterPost(this@MainActivity, postArrayList)
                    // Set adapter to recyclerview
                    postsRv.adapter = adapterPost
                    progressDialog.dismiss()
                } catch (e:Exception) {
                    Log.d(TAG, "loadPosts: 2 ${e.message}")
                    Toast.makeText(this, "{ e.message }", Toast.LENGTH_SHORT).show()

                }
            }, { error ->
                // Handle error
                Log.d(TAG, "loadPosts: $error.message")
                Toast.makeText(this, "{ e.message }", Toast.LENGTH_SHORT).show()

            })
        // Put req in queue
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
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
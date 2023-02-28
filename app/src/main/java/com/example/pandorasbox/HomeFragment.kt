package com.example.pandorasbox

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import com.example.pandorasbox.AdapterPost


class HomeFragment : Fragment() {
    // Full URL to retrieve post
    private var url = ""

    // Next page token to get next post
    private var nextToken = ""

    private lateinit var postArrayList: ArrayList<ModelPost>

    // Initialise and clear list before add data to it

    init {
        postArrayList = ArrayList<ModelPost>()
        postArrayList.clear()
    }

    private lateinit var adapterPost: AdapterPost

    private lateinit var progressDialog: ProgressDialog

    private val TAG = "MAIN_TAG"
    lateinit var loadMoreBtn: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)
        val postsRv = view.findViewById<RecyclerView>(R.id.postsRv)
        loadMoreBtn = view.findViewById(R.id.loadMoreBtn)

        postsRv.layoutManager = LinearLayoutManager(requireContext())

        // Setup progress dialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading blogs, please wait...")

        // Set up adapter
        adapterPost = AdapterPost(requireActivity(), postArrayList)
        // Set adapter to recyclerview
        postsRv.adapter = adapterPost
        progressDialog.dismiss()


        // CLicking on log out btn calls sign out method
        view.findViewById<Button>(R.id.btn_log_out).setOnClickListener {
            Firebase.auth.signOut()
            var navLogin = activity as FragmentNavigation
            navLogin.navigateFrag(LoginFragment(), false)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        // Initialise and clear list before add data to it
//
//        postArrayList = ArrayList()
//        postArrayList.clear()///////


        loadPosts()

        loadMoreBtn.setOnClickListener {
            loadPosts()
        }
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
                Toast.makeText(requireContext(), "No more posts...", Toast.LENGTH_SHORT).show()
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
        val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->
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
                    } catch (e: Exception) {
                        Toast.makeText(activity, "Reached end of page...", Toast.LENGTH_SHORT)
                            .show()
                        Log.d(TAG, "loadPosts: Reached end of page...")
                        nextToken = "end"
                    }
                    // grab json array data from json obj
                    val jsonArray = jsonObject.getJSONArray("items")

                    // Loop getting ddata until got all
                    for (i in 0 until jsonArray.length()) {
                        try {
                            val jsonObject01 = jsonArray.getJSONObject(i)
                            val id = jsonObject01.getString("id")
                            val title = jsonObject01.getString("title")
                            val content = jsonObject01.getString("content")
                            val published = jsonObject01.getString("published")
                            val updated = jsonObject01.getString("updated")
                            val url = jsonObject01.getString("url")
                            val selfLink = jsonObject01.getString("selfLink")
                            val authorName =
                                jsonObject01.getJSONObject("author").getString("displayName")
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
                        } catch (e: Exception) {
                            Log.d(TAG, "loadPosts: 1 ${e.message}")
                            Toast.makeText(activity, "{ e.message }", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "loadPosts: 2 ${e.message}")
                    Toast.makeText(activity, "{ e.message }", Toast.LENGTH_SHORT).show()

                }
            }, { error ->
                // Handle error
                Log.d(TAG, "loadPosts: ${error.message}")
                Toast.makeText(activity, "{ e.message }", Toast.LENGTH_SHORT).show()
            })

        // Put req in queue
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)

    }

}


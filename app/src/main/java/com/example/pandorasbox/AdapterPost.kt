package com.example.pandorasbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.text.SimpleDateFormat

class AdapterPost (
    // Context stores info on how the itemAdapter resolves string/image resources
    private val context: Context,
    private val postArrayList: ArrayList<ModelPost>
) : RecyclerView.Adapter<AdapterPost.HolderPost>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPost {
        // Inflate the layout of row_post.xml
        val view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false)
        return HolderPost(view)
    }

    override fun getItemCount(): Int {
        //Return size of stuff
        return postArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPost, position: Int) {
        // Get/send/format data and handle clicks
        // Grab data at certain pos of list
        val model = postArrayList[position]

        // Get data
        val authorName = model.authorName
        // HTML -> simple text using jsoup
        val content = model.content
        val id = model.id
        // The date published -> format
        val published = model.published
        val selfLink = model.selfLink
        // Date post is edited
        val title = model.updated
        val url = model.url

        val document = Jsoup.parse(content)
        try {
            // Grab image, allow for no/multiple images, try to get first
            val elements = document.select("img")
            val image = elements[0].attr("src")
            // Set img w Picasso lib
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv)
        }
        catch (e:Exception) {
            // Failure, i.e. no image to grab -> so place default image
            holder.imageIv.setImageResource(R.drawable.ic_image_black)

        }

        // Format date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val dateFormat2 = SimpleDateFormat("dd-MM-yyy'K'HH:mm:a") //02:07
        var formattedDate =""
        try {
            val date = dateFormat.parse(published)
            formattedDate = dateFormat2.format(date)

        } catch (e:Exception) {
            // Fall back to secondary ugly date from API JSON resp
            formattedDate = published
            e.printStackTrace()
        }
        holder.titleTv.text = title
        holder.descriptionTv.text = document.text()
        holder.publishInfoTv.text = "By $authorName $formattedDate"



    }


    // View holder class to hold init UI views of row_posts.xml
    inner class HolderPost(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Init UI views
        var moreBtn: ImageButton = itemView.findViewById(R.id.moreBtn)
        var titleTv: TextView = itemView.findViewById(R.id.titleTv)
        var publishInfoTv: TextView = itemView.findViewById(R.id.publishInfoTv)
        var imageIv: ImageView = itemView.findViewById(R.id.imageIv)
        var descriptionTv: TextView = itemView.findViewById(R.id.descriptionTv)


    }
}



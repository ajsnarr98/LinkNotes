package com.github.ajsnarr98.linknotes.notes

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.Entry
import com.github.ajsnarr98.linknotes.data.EntryType
import com.github.ajsnarr98.linknotes.databinding.ItemEditnoteImagesEntryBinding

/**
 * An entry for images
 *
 * @property isEditable - true if this view supports adding images
 */
class ImageEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val isEditable: Boolean = true,
    var addImageListener: ((entry: Entry, imageUrl: String) -> Unit)? = null,
): ConstraintLayout(context, attrs, defStyleAttr), EntryView {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    private val recyclerAdapter: ImageEntryAdapter = ImageEntryAdapter()
    private lateinit var entry: Entry
    private val binding = ItemEditnoteImagesEntryBinding.inflate(
        LayoutInflater.from(context),
        this,
        true,
    ).apply {
        // set up recyclerview
        images.apply {
            layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            adapter = recyclerAdapter
        }
        addImage.setOnClickListener {
            // TODO - get new image
            val onSuccess = { imageUrl: String ->
                recyclerAdapter.addImage(imageUrl)
                addImageListener?.invoke(entry, imageUrl)
            }
            val testImages = listOf<String>(
                "https://homepages.cae.wisc.edu/~ece533/images/cat.png",
                "https://homepages.cae.wisc.edu/~ece533/images/sails.png",
                "https://homepages.cae.wisc.edu/~ece533/images/airplane.png",
            )
            onSuccess(testImages.random())
        }

        if (isEditable) {
            deleteButton.visibility = View.VISIBLE
            addImage.visibility = View.VISIBLE
            entryTypeEditable.visibility = View.VISIBLE
            entryTypeTitle.visibility = View.VISIBLE
            entryTypeStatic.visibility = View.GONE
        } else {
            deleteButton.visibility = View.GONE
            addImage.visibility = View.GONE
            entryTypeEditable.visibility = View.GONE
            entryTypeTitle.visibility = View.GONE
            entryTypeStatic.visibility = View.VISIBLE
        }
    }

    /**
     * Set the images entry for this view to display.
     */
    override fun bind(entry: Entry) {
        if (entry.type !is EntryType.IMAGES) throw IllegalStateException("Entry should be an image entry")
        this.entry = entry
        this.recyclerAdapter.setImages(entry.content.images.toList())
    }

    /**
     * Takes in a list of image urls.
     */
    private class ImageEntryAdapter(
        private var images: List<String> = emptyList()
    ) : RecyclerView.Adapter<ImageEntryAdapter.ViewHolder>() {

        /** Set the urls of the images to be shown. */
        fun setImages(newImages: List<String>) {
            this.images = newImages
            // TODO - use diff util
            notifyDataSetChanged()
        }

        /** Add a new image url. */
        fun addImage(newImage: String) {
            setImages(
                ArrayList<String>(images.size).apply {
                    addAll(images)
                    add(newImage)
                }
            )
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                requireNotNull(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_default_image, parent, false) as? ImageView,
                    { "Could not cast default image view to ImageView" }
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(images[position])
        }

        override fun getItemCount(): Int = images.size

        class ViewHolder(private val view: ImageView) : RecyclerView.ViewHolder(view) {
            fun bind(imageUrl: String) {
                Glide.with(view)
                    .load(imageUrl)
                    .fitCenter()
                    .placeholder(R.drawable.default_profile)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
            }
        }
    }
}

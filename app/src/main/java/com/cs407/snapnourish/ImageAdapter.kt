import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cs407.snapnourish.R

class ImageAdapter(private val imageUrls: List<String>,
    private val onImageClick: (String) -> Unit
): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.image_loading)
            .error(R.drawable.image_error)
            .into(holder.imageView)  // Load the image into the ImageView

        holder.itemView.setOnClickListener {
            onImageClick(imageUrl)
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}

package dev.ayer.dmusic.presenter.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import dev.ayer.dmusic.R

class BannerRecyclerViewAdapter(
    private val adUnit: String
): RecyclerView.Adapter<BannerRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val templateView: TemplateView = view.findViewById(R.id.banner)
        val placeholder: View = view.findViewById(R.id.banner_placeholder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.banner_adapter_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adLoader: AdLoader = AdLoader.Builder(holder.itemView.context, adUnit)
            .forNativeAd { nativeAd ->
                val styles = NativeTemplateStyle.Builder().build()
                val template = holder.templateView
                template.setStyles(styles)
                template.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    val template = holder.templateView
                    val placeholder = holder.placeholder
                    template.visibility = View.VISIBLE
                    placeholder.visibility = View.GONE
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun getItemCount(): Int {
        return 1
    }
}

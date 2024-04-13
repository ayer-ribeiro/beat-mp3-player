package dev.ayer.dmusic.presenter.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.ayer.dmusic.R
import dev.ayer.dmusic.entity.Theme

class ColorSettingsAdapter : ListAdapter<Theme, ColorSettingsAdapter.ViewHolder>(ThemeDiffUtilsCallback()) {

    fun interface OnClickListener {
        fun onClick(song: Theme)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val themeNameTextView: TextView = view.findViewById(R.id.theme_name)
        val themeColorImageView: ImageView = view.findViewById(R.id.theme_color_Image_view)
    }

    var onClickListener: OnClickListener? = null
    var selectedTheme: Theme? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.settings_color_adapter_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = currentList[position]

        if (position == 0) {
            val margin = holder.itemView.context.resources.getDimensionPixelOffset(R.dimen.default_half_margin)
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = margin
        } else {
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 0
        }

        if (position == currentList.size - 1) {
            val margin = holder.itemView.context.resources.getDimensionPixelOffset(R.dimen.mini_player_recycler_view_end_space)
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = margin
        } else {
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 0
        }

        if (theme == selectedTheme) {
            holder.themeColorImageView.setImageResource(R.drawable.outline_check_circle_24)
        } else {
            holder.themeColorImageView.setImageResource(R.drawable.outline_circle_24)
        }

        @StringRes
        val themeNameStringRes: Int = when(theme) {
            Theme.GREEN -> R.string.settings_color_green_theme_name
            Theme.BLUE -> R.string.settings_color_blue_theme_name
            Theme.ORANGE -> R.string.settings_color_orange_theme_name
            Theme.PINK -> R.string.settings_color_pink_theme_name
            Theme.PURPLE -> R.string.settings_color_purple_theme_name
        }

        @IdRes
        val themeColorResId: Int = when(theme) {
            Theme.GREEN -> R.color.app_green_secondary
            Theme.BLUE -> R.color.app_blue_secondary
            Theme.ORANGE -> R.color.app_orange_secondary
            Theme.PINK -> R.color.app_pink_secondary
            Theme.PURPLE -> R.color.app_purple_secondary
        }

        holder.themeNameTextView.text = holder.itemView.context.getString(themeNameStringRes)
        holder.themeColorImageView.setBackgroundResource(themeColorResId)
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(theme)
        }
    }
}

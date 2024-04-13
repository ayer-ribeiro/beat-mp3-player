package dev.ayer.dmusic.presenter.customviews

import android.content.ContentUris
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.net.Uri
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ProgressBar
import android.widget.Space
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import dev.ayer.dmusic.R

class MiniPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    fun interface OnPlayPauseButtonClicked {
        fun onClicked()
    }

    fun interface OnForwardButtonClicked {
        fun onClicked()
    }

    var onPlayPauseButtonClicked: OnPlayPauseButtonClicked? = null
    var onForwardButtonClicked: OnForwardButtonClicked? = null

    @ColorInt
    var accentColor: Int? = null
        set(value) {
            field = value
            updateButtonsColors()
        }

    var maxSeekBar: Int
        set(value) {
            progressBar.max = value
        }
        get() {
            return progressBar.max
        }

    var progress: Int
        set(value) {
            progressBar.progress = value
        }
        get() {
            return progressBar.progress
        }

    private val progressBar: ProgressBar
    private val playPauseImageView: AppCompatImageView
    private val forwardImageView: AppCompatImageView
    private val albumThumbImageView: AppCompatImageView
    private val songNameTextView: TextView
    private val artistNameTextView: TextView
    private val spaceCenterView: Space

    init {
        inflate(context, R.layout.custom_view_mini_player, this)

        progressBar = findViewById(R.id.progress_bar)
        playPauseImageView = findViewById(R.id.play_pause_image_buttom)
        forwardImageView = findViewById(R.id.forward_image_buttom)
        spaceCenterView = findViewById(R.id.space_center)
        songNameTextView = findViewById(R.id.song_name)
        artistNameTextView = findViewById(R.id.artist_name)
        albumThumbImageView = findViewById(R.id.album_thumb)

        playPauseImageView.setOnClickListener { onPlayPauseButtonClicked?.onClicked() }
        forwardImageView.setOnClickListener { onForwardButtonClicked?.onClicked() }

        val styledAttributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MiniPlayerView,
            0,
            0
        )

        initializeAttributes(styledAttributes)
        updateButtonsColors()
        updateSeekBarProgressColor()
    }

    private fun initializeAttributes(styledAttributes: TypedArray) {
        try {
            if (styledAttributes.hasValue(R.styleable.MiniPlayerView_accentColor)) {
                accentColor = styledAttributes.getColor(R.styleable.MiniPlayerView_accentColor, -1)
            }
        } finally {
            styledAttributes.recycle()
        }
    }

    private fun updateSeekBarProgressColor() {
        val progressColor = accentColor ?: getThemeColor(com.google.android.material.R.attr.colorSecondary)
        progressBar.progressTintList = generateColorStateList(progressColor)
    }

    private fun updateButtonsColors() {
        val color = getButtonColorOrDefault()

        initializeImageButtonColors(playPauseImageView, color)
        initializeImageButtonColors(forwardImageView, color)
    }

    private fun getButtonColorOrDefault(): Int {
        return accentColor ?: getThemeColor(com.google.android.material.R.attr.colorControlNormal)
    }

    private fun getThemeColor(attr: Int): Int {
        val typedValue = TypedValue()
        val typedArray: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    private fun initializeImageButtonColors(image: AppCompatImageView, color: Int) {
        image.imageTintList = generateColorStateList(color)
    }

    fun setPlayButton() {
        playPauseImageView.setImageResource(R.drawable.outline_play_arrow_24)
    }

    fun setPauseButton() {
        playPauseImageView.setImageResource(R.drawable.outline_pause_24)
    }

    fun setSongName(songName: String) {
        songNameTextView.text = songName
    }

    fun setSongArtistName(artistName: String) {
        artistNameTextView.text = artistName
    }

    fun setAlbumThumbImageUri(albumAndroidId: Long?) {
        Glide.with(context)
            .load(getAlbumThumbUri(albumAndroidId))
            .into(albumThumbImageView)
    }

    private fun getAlbumThumbUri(albumAndroidId: Long?): Uri? {
        if (albumAndroidId == null) {
            return null
        }

        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, albumAndroidId)
    }

    private fun generateColorStateList(
        color: Int,
    ): ColorStateList {
        return generateColorStateList(
            enabledColor = color,
            disabledColor = color,
            checkedColor = color,
            uncheckedColor = color,
            activeColor = color,
            inactiveColor = color,
            pressedColor = color,
            focusedColor = color,
            selectedColor = color,
        )
    }

    private fun generateColorStateList(
        enabledColor: Int,
        disabledColor: Int,
        checkedColor: Int,
        uncheckedColor: Int,
        activeColor: Int,
        inactiveColor: Int,
        pressedColor: Int,
        focusedColor: Int,
        selectedColor: Int,
    ): ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_active),
            intArrayOf(-android.R.attr.state_active),
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_selected)
        )
        val colors = intArrayOf(
            enabledColor,
            disabledColor,
            checkedColor,
            uncheckedColor,
            activeColor,
            inactiveColor,
            pressedColor,
            focusedColor,
            selectedColor
        )
        return ColorStateList(states, colors)
    }
}

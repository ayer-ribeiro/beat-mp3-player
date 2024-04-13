package dev.ayer.dmusic.presenter.customviews

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.SeekBar
import android.widget.Space
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import dev.ayer.dmusic.R

class BigMiniPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    fun interface OnShuffleButtonClicked {
        fun onClicked()
    }

    fun interface OnPreviousButtonClicked {
        fun onClicked()
    }

    fun interface OnPlayPauseButtonClicked {
        fun onClicked()
    }

    fun interface OnForwardButtonClicked {
        fun onClicked()
    }

    fun interface OnRepeatButtonClicked {
        fun onClicked()
    }

    var onShuffleButtonClicked: OnShuffleButtonClicked? = null
    var onPreviousButtonClicked: OnPreviousButtonClicked? = null
    var onPlayPauseButtonClicked: OnPlayPauseButtonClicked? = null
    var onForwardButtonClicked: OnForwardButtonClicked? = null
    var onRepeatButtonClicked: OnRepeatButtonClicked? = null

    @ColorInt var buttonColor: Int? = null
        set(value) {
            field = value
            updateButtonsColors()
        }

    @ColorInt var activatedButtonColor: Int? = null
        set(value) {
            field = value
            updateButtonsColors()
        }

    @ColorInt var progressColor: Int? = null
        set(value) {
            field = value
            updateSeekBarProgressColor()
        }

    @ColorInt var thumbColor: Int? = null
        set(value) {
            field = value
            updateSeekBarThumbColor()
        }

    var spaceCenter: Int? = null
        set(value) {
            field = value
            updateSpaceCenterView()
        }

    var maxSeekBar: Int
        set(value) {
            seekBar.max = value
        }
        get() {
            return seekBar.max
        }

    var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
        set(value) {
            field = value
            seekBar.setOnSeekBarChangeListener(value)
        }

    var progress: Int
        set(value) {
            seekBar.progress = value
        }
        get() {
            return seekBar.progress
        }

    private val seekBar: SeekBar
    private val shuffleImageView: AppCompatImageView
    private val previousImageView: AppCompatImageView
    private val playPauseImageView: AppCompatImageView
    private val forwardImageView: AppCompatImageView
    private val repeatImageView: AppCompatImageView
    private val spaceCenterView: Space

    init {
        inflate(context, R.layout.custom_view_big_mini_player, this)

        seekBar = findViewById(R.id.seekbar)
        shuffleImageView = findViewById(R.id.shuffle_image_buttom)
        previousImageView = findViewById(R.id.previous_image_buttom)
        playPauseImageView = findViewById(R.id.play_pause_image_buttom)
        forwardImageView = findViewById(R.id.forward_image_buttom)
        repeatImageView = findViewById(R.id.repeat_image_button)
        spaceCenterView = findViewById(R.id.space_center)

        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
        shuffleImageView.setOnClickListener { onShuffleButtonClicked?.onClicked() }
        previousImageView.setOnClickListener { onPreviousButtonClicked?.onClicked() }
        playPauseImageView.setOnClickListener { onPlayPauseButtonClicked?.onClicked() }
        forwardImageView.setOnClickListener { onForwardButtonClicked?.onClicked() }
        repeatImageView.setOnClickListener { onRepeatButtonClicked?.onClicked() }

        val styledAttributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BigMiniPlayerView,
            0,
            0
        )

        initializeAttributes(styledAttributes)
        updateButtonsColors()
        updateSeekBarThumbColor()
        updateSeekBarProgressColor()
        updateSpaceCenterView()
    }

    private fun initializeAttributes(styledAttributes: TypedArray) {
        try {
            if (styledAttributes.hasValue(R.styleable.BigMiniPlayerView_buttonColor)) {
                buttonColor = styledAttributes.getColor(R.styleable.BigMiniPlayerView_buttonColor, -1)
            }

            if (styledAttributes.hasValue(R.styleable.BigMiniPlayerView_progressColor)) {
                progressColor = styledAttributes.getColor(R.styleable.BigMiniPlayerView_progressColor, -1)
            }

            if (styledAttributes.hasValue(R.styleable.BigMiniPlayerView_thumbColor)) {
                thumbColor = styledAttributes.getColor(R.styleable.BigMiniPlayerView_thumbColor, -1)
            }

            if (styledAttributes.hasValue(R.styleable.BigMiniPlayerView_activatedButtonColor)) {
                activatedButtonColor = styledAttributes.getColor(R.styleable.BigMiniPlayerView_activatedButtonColor, -1)
            }

            if (styledAttributes.hasValue(R.styleable.BigMiniPlayerView_spaceCenter)) {
                spaceCenter = styledAttributes.getDimensionPixelOffset(R.styleable.BigMiniPlayerView_spaceCenter, 0)
            }

        } finally {
            styledAttributes.recycle()
        }
    }

    private fun updateSeekBarThumbColor() {
        val thumbColor = thumbColor ?: getThemeColor(com.google.android.material.R.attr.colorSecondary)
        seekBar.thumbTintList = generateColorStateList(thumbColor)
    }

    private fun updateSeekBarProgressColor() {
        val progressColor = progressColor ?: getThemeColor(com.google.android.material.R.attr.colorSecondary)
        seekBar.progressTintList = generateColorStateList(progressColor)
    }

    private fun updateSpaceCenterView() {
        spaceCenter?.let { spaceCenter ->
            spaceCenterView.layoutParams.height = spaceCenter
        }
    }

    private fun updateButtonsColors() {
        val defaultColor = getButtonColorOrDefault()
        val activatedColor = getActivatedButtonColorOrDefault()

        initializeImageButtonColors(shuffleImageView, defaultColor, activatedColor)
        initializeImageButtonColors(previousImageView, defaultColor, activatedColor)
        initializeImageButtonColors(playPauseImageView, defaultColor, activatedColor)
        initializeImageButtonColors(forwardImageView, defaultColor, activatedColor)
        initializeImageButtonColors(repeatImageView, defaultColor, activatedColor)
    }

    private fun getActivatedButtonColorOrDefault(): Int {
        return activatedButtonColor ?: getThemeColor(com.google.android.material.R.attr.colorControlActivated)
    }

    private fun getButtonColorOrDefault(): Int {
        return buttonColor ?: getThemeColor(androidx.appcompat.R.attr.colorControlNormal)
    }

    private fun getThemeColor(attr: Int): Int {
        val typedValue = TypedValue()
        val typedArray: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    private fun initializeImageButtonColors(
        image: AppCompatImageView,
        defaultColor: Int,
        activatedColor: Int,
    ) {
        image.imageTintList = generateColorStateList(
            enabledColor = defaultColor,
            disabledColor = defaultColor,
            checkedColor = activatedColor,
            uncheckedColor = defaultColor,
            activeColor = activatedColor,
            inactiveColor = defaultColor,
            pressedColor = defaultColor,
            focusedColor = defaultColor,
            selectedColor = activatedColor
        )
    }

    fun setPlayButton() {
        playPauseImageView.setImageResource(R.drawable.outline_play_arrow_24)
    }

    fun setPauseButton() {
        playPauseImageView.setImageResource(R.drawable.outline_pause_24)
    }

    fun setShuffleActiveButton() {
        shuffleImageView.isActivated = true
        shuffleImageView.imageTintList = generateColorStateList(getActivatedButtonColorOrDefault())
    }

    fun setShuffleInactiveButton() {
        shuffleImageView.isActivated = false
        shuffleImageView.imageTintList = generateColorStateList(getButtonColorOrDefault())
    }

    fun setRepeatOneButton() {
        repeatImageView.isActivated = true
        repeatImageView.imageTintList = generateColorStateList(getActivatedButtonColorOrDefault())
        repeatImageView.setImageResource(R.drawable.outline_repeat_one_24)
    }

    fun setRepeatAllButton() {
        repeatImageView.isActivated = true
        repeatImageView.imageTintList = generateColorStateList(getActivatedButtonColorOrDefault())
        repeatImageView.setImageResource(R.drawable.outline_repeat_24)
    }

    fun setRepeatNoneButton() {
        repeatImageView.isActivated = false
        repeatImageView.imageTintList = generateColorStateList(getButtonColorOrDefault())
        repeatImageView.setImageResource(R.drawable.outline_repeat_24)
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

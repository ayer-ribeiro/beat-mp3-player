package dev.ayer.dmusic.presenter.theme

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.ayer.dmusic.R
import dev.ayer.dmusic.entity.Theme

abstract class CustomThemeBaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CustomThemeManager.listeners.addListener(lifecycle) {
            recreate()
        }
    }

    override fun getTheme(): Resources.Theme {
        val superTheme =  super.getTheme()

        when(CustomThemeManager.getCurrentTheme()) {
            Theme.GREEN -> superTheme.applyStyle(R.style.Theme_PlayerCustomizável, true)
            Theme.BLUE -> superTheme.applyStyle(R.style.Theme_PlayerCustomizável_Blue, true)
            Theme.ORANGE -> superTheme.applyStyle(R.style.Theme_PlayerCustomizável_Orange, true)
            Theme.PINK -> superTheme.applyStyle(R.style.Theme_PlayerCustomizável_Pink, true)
            Theme.PURPLE -> superTheme.applyStyle(R.style.Theme_PlayerCustomizável_Purple, true)
        }

        return superTheme
    }
}

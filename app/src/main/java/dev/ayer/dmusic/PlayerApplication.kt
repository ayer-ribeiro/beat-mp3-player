package dev.ayer.dmusic

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dev.ayer.audioplayer.transport.service.PlayerApp
import dev.ayer.dmusic.presenter.theme.CustomThemeManager

class PlayerApplication: Application(), PlayerApp {
    override fun onCreate() {
        super.onCreate()
        CustomThemeManager.init(this)
        MobileAds.initialize(this)
    }

    override fun getNotificationIconDrawableId(): Int {
        return R.drawable.ic_launcher_foreground
    }
}

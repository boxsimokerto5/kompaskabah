package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSourceBannerLayout

/**
 * Composable that displays an ironSource LevelPlay Banner ad (with Meta Audience Network mediation).
 * Height standard banner is 50.dp.
 */
@Composable
fun IronSourceBannerAd(
    modifier: Modifier = Modifier,
    placementName: String = IronSourceAdManager.BANNER_UNIT_ID,
    bannerSize: ISBannerSize = ISBannerSize.BANNER
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    var isLoaded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var bannerLayout by remember { mutableStateOf<IronSourceBannerLayout?>(null) }

    DisposableEffect(activity, placementName) {
        if (activity != null) {
            val banner = IronSourceAdManager.createAndLoadBanner(
                activity = activity,
                placementName = placementName,
                bannerSize = bannerSize,
                onLoaded = {
                    isLoaded = true
                    errorMessage = null
                },
                onFailed = { err ->
                    isLoaded = false
                    errorMessage = err
                }
            )
            bannerLayout = banner
        }

        onDispose {
            bannerLayout?.let {
                IronSourceAdManager.destroyBanner(it)
            }
            bannerLayout = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        if (bannerLayout != null) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                factory = { ctx ->
                    val frameLayout = FrameLayout(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                    bannerLayout?.let { bl ->
                        if (bl.parent != null) {
                            (bl.parent as? ViewGroup)?.removeView(bl)
                        }
                        frameLayout.addView(bl)
                    }
                    frameLayout
                },
                update = { container ->
                    bannerLayout?.let { bl ->
                        if (bl.parent != container) {
                            (bl.parent as? ViewGroup)?.removeView(bl)
                            container.removeAllViews()
                            container.addView(bl)
                        }
                    }
                }
            )
        }

        // Placeholder or status while loading or waiting
        AnimatedVisibility(
            visible = !isLoaded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = if (errorMessage != null) "Iklan Sedang Dimuat..." else "Memuat Iklan...",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

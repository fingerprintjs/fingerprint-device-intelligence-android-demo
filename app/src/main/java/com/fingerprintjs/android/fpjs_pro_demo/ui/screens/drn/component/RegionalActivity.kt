package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn.component

import androidx.compose.runtime.Composable
import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.Drn
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.view.flag.Flag
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.view.flag.FlagSpriteManager

@Composable
fun RegionalActivity(
    flagSpriteManager: FlagSpriteManager,
    ra: Drn.RegionalActivity
) {
    ra.countries.forEach { country ->
        Flag(spriteManager = flagSpriteManager, countryCode = country.code)
    }
}

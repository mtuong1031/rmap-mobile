package com.rmap.mobile.features.roadmap.presentation.components.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
internal fun formattedString(
    @StringRes resId: Int,
    args: List<String>
): String {
    return when (args.size) {
        0 -> stringResource(resId)
        1 -> stringResource(resId, args[0])
        2 -> stringResource(resId, args[0], args[1])
        else -> stringResource(resId, *args.toTypedArray())
    }
}

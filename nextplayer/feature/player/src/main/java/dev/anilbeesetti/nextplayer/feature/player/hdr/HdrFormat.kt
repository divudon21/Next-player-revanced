package dev.anilbeesetti.nextplayer.feature.player.hdr

import androidx.annotation.OptIn
import androidx.media3.common.ColorInfo
import androidx.media3.common.Format
import androidx.media3.common.util.UnstableApi

internal const val HDR_VIDEO_KEY = "nextplayer.hdr_video"

@OptIn(UnstableApi::class)
internal fun isHdrVideo(format: Format?): Boolean = ColorInfo.isTransferHdr(format?.colorInfo)

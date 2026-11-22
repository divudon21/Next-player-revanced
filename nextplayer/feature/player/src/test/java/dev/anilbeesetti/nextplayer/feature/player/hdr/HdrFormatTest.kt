package dev.anilbeesetti.nextplayer.feature.player.hdr

import androidx.media3.common.C
import androidx.media3.common.ColorInfo
import androidx.media3.common.Format
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HdrFormatTest {
    private fun format(transfer: Int): Format = Format.Builder()
        .setColorInfo(ColorInfo.Builder().setColorTransfer(transfer).build())
        .build()

    @Test
    fun detectsPqAndHlgWithoutStaticMetadata() {
        assertTrue(isHdrVideo(format(C.COLOR_TRANSFER_ST2084)))
        assertTrue(isHdrVideo(format(C.COLOR_TRANSFER_HLG)))
    }

    @Test
    fun missingMetadataAndSdrAreNotHdr() {
        assertFalse(isHdrVideo(null))
        assertFalse(isHdrVideo(Format.Builder().build()))
        assertFalse(isHdrVideo(format(C.COLOR_TRANSFER_SDR)))
    }
}

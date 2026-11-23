package dev.anilbeesetti.nextplayer.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.anilbeesetti.nextplayer.core.model.AppThemeColor
import dev.anilbeesetti.nextplayer.core.ui.R

fun AppThemeColor.previewColor(): Color = when (this) {
    AppThemeColor.TEAL -> Color(0xFF006A68)
    AppThemeColor.CRIMSON -> Color(0xFFBA1A1A)
    AppThemeColor.OCEAN_BLUE -> Color(0xFF0061A4)
    AppThemeColor.EMERALD_GREEN -> Color(0xFF006D3E)
    AppThemeColor.VIOLET -> Color(0xFF7043C0)
    AppThemeColor.SUNSET_ORANGE -> Color(0xFFA14000)
    AppThemeColor.AMBER_GOLD -> Color(0xFF7C5800)
    AppThemeColor.ROSE_PINK -> Color(0xFF9E2A68)
    AppThemeColor.CYAN -> Color(0xFF00687A)
    AppThemeColor.INDIGO -> Color(0xFF3F51B5)
    AppThemeColor.LIME -> Color(0xFF4C6700)
    AppThemeColor.SAKURA -> Color(0xFF9B405A)
}

@Composable
fun AppThemeColor.displayName(): String = when (this) {
    AppThemeColor.TEAL -> stringResource(R.string.color_teal)
    AppThemeColor.CRIMSON -> stringResource(R.string.color_crimson)
    AppThemeColor.OCEAN_BLUE -> stringResource(R.string.color_ocean_blue)
    AppThemeColor.EMERALD_GREEN -> stringResource(R.string.color_emerald_green)
    AppThemeColor.VIOLET -> stringResource(R.string.color_violet)
    AppThemeColor.SUNSET_ORANGE -> stringResource(R.string.color_sunset_orange)
    AppThemeColor.AMBER_GOLD -> stringResource(R.string.color_amber_gold)
    AppThemeColor.ROSE_PINK -> stringResource(R.string.color_rose_pink)
    AppThemeColor.CYAN -> stringResource(R.string.color_cyan)
    AppThemeColor.INDIGO -> stringResource(R.string.color_indigo)
    AppThemeColor.LIME -> stringResource(R.string.color_lime)
    AppThemeColor.SAKURA -> stringResource(R.string.color_sakura)
}

fun getAppColorScheme(color: AppThemeColor, isDark: Boolean): ColorScheme {
    return when (color) {
        AppThemeColor.TEAL -> if (isDark) darkScheme else lightScheme
        AppThemeColor.CRIMSON -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFFB4AB),
                onPrimary = Color(0xFF690005),
                primaryContainer = Color(0xFF93000A),
                onPrimaryContainer = Color(0xFFFFDAD6),
                secondary = Color(0xFFE7BDB7),
                onSecondary = Color(0xFF442926),
                secondaryContainer = Color(0xFF5D3F3C),
                onSecondaryContainer = Color(0xFFFFDAD6),
                tertiary = Color(0xFFE0C38C),
                onTertiary = Color(0xFF3E2E04),
                tertiaryContainer = Color(0xFF564419),
                onTertiaryContainer = Color(0xFFFDDF9F),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFBA1A1A),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFDAD6),
                onPrimaryContainer = Color(0xFF410002),
                secondary = Color(0xFF775652),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFFFDAD6),
                onSecondaryContainer = Color(0xFF2C1512),
                tertiary = Color(0xFF705C2E),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFFDE1A6),
                onTertiaryContainer = Color(0xFF261A00),
            )
        }
        AppThemeColor.OCEAN_BLUE -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFF9ECAFF),
                onPrimary = Color(0xFF003258),
                primaryContainer = Color(0xFF00497D),
                onPrimaryContainer = Color(0xFFD1E4FF),
                secondary = Color(0xFFBBC7DB),
                onSecondary = Color(0xFF253140),
                secondaryContainer = Color(0xFF3C4858),
                onSecondaryContainer = Color(0xFFD7E3F7),
                tertiary = Color(0xFFD6BEE4),
                onTertiary = Color(0xFF3B2948),
                tertiaryContainer = Color(0xFF523F5F),
                onTertiaryContainer = Color(0xFFF2DAFF),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF0061A4),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFD1E4FF),
                onPrimaryContainer = Color(0xFF001D36),
                secondary = Color(0xFF535F70),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFD7E3F8),
                onSecondaryContainer = Color(0xFF101C2B),
                tertiary = Color(0xFF6B5778),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFF2DAFF),
                onTertiaryContainer = Color(0xFF251431),
            )
        }
        AppThemeColor.EMERALD_GREEN -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFF65DC91),
                onPrimary = Color(0xFF00391D),
                primaryContainer = Color(0xFF00532C),
                onPrimaryContainer = Color(0xFF82F8AB),
                secondary = Color(0xFFB7CCB8),
                onSecondary = Color(0xFF233526),
                secondaryContainer = Color(0xFF394B3C),
                onSecondaryContainer = Color(0xFFD3E8D3),
                tertiary = Color(0xFFA1CED6),
                onTertiary = Color(0xFF00363D),
                tertiaryContainer = Color(0xFF1F4D54),
                onTertiaryContainer = Color(0xFFBCEBF2),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF006D3E),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFF82F8AB),
                onPrimaryContainer = Color(0xFF00210E),
                secondary = Color(0xFF506352),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFD3E8D3),
                onSecondaryContainer = Color(0xFF0E1F12),
                tertiary = Color(0xFF3A656D),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFBCEBF2),
                onTertiaryContainer = Color(0xFF001F24),
            )
        }
        AppThemeColor.VIOLET -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFD4BBFF),
                onPrimary = Color(0xFF3F008F),
                primaryContainer = Color(0xFF5725A6),
                onPrimaryContainer = Color(0xFFECDCFF),
                secondary = Color(0xFFCCC2DB),
                onSecondary = Color(0xFF332D41),
                secondaryContainer = Color(0xFF4A4358),
                onSecondaryContainer = Color(0xFFE8DEF8),
                tertiary = Color(0xFFEFB8C8),
                onTertiary = Color(0xFF492532),
                tertiaryContainer = Color(0xFF633B48),
                onTertiaryContainer = Color(0xFFFFD8E4),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF7043C0),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFECDCFF),
                onPrimaryContainer = Color(0xFF25005A),
                secondary = Color(0xFF625B71),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFE8DEF8),
                onSecondaryContainer = Color(0xFF1E192B),
                tertiary = Color(0xFF7D5260),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFFFD8E4),
                onTertiaryContainer = Color(0xFF31111D),
            )
        }
        AppThemeColor.SUNSET_ORANGE -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFFB598),
                onPrimary = Color(0xFF581D00),
                primaryContainer = Color(0xFF7C2E00),
                onPrimaryContainer = Color(0xFFFFDBCD),
                secondary = Color(0xFFE7BEB0),
                onSecondary = Color(0xFF442A20),
                secondaryContainer = Color(0xFF5D4035),
                onSecondaryContainer = Color(0xFFFFDBCD),
                tertiary = Color(0xFFD7C78E),
                onTertiary = Color(0xFF3A3006),
                tertiaryContainer = Color(0xFF52461B),
                onTertiaryContainer = Color(0xFFF4E3A7),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFA14000),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFDBCD),
                onPrimaryContainer = Color(0xFF360F00),
                secondary = Color(0xFF77574B),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFFFDBCD),
                onSecondaryContainer = Color(0xFF2C160D),
                tertiary = Color(0xFF6B5E2F),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFF4E3A7),
                onTertiaryContainer = Color(0xFF231B00),
            )
        }
        AppThemeColor.AMBER_GOLD -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFF5BE48),
                onPrimary = Color(0xFF412D00),
                primaryContainer = Color(0xFF5E4200),
                onPrimaryContainer = Color(0xFFFFDF9E),
                secondary = Color(0xFFD6C5A0),
                onSecondary = Color(0xFF3A3016),
                secondaryContainer = Color(0xFF51462A),
                onSecondaryContainer = Color(0xFFF3E1BB),
                tertiary = Color(0xFFAECFA8),
                onTertiary = Color(0xFF1B361B),
                tertiaryContainer = Color(0xFF314D30),
                onTertiaryContainer = Color(0xFFCAEBC3),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF7C5800),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFDF9E),
                onPrimaryContainer = Color(0xFF261900),
                secondary = Color(0xFF6A5D3F),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFF3E1BB),
                onSecondaryContainer = Color(0xFF231B04),
                tertiary = Color(0xFF486546),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFCAEBC3),
                onTertiaryContainer = Color(0xFF062108),
            )
        }
        AppThemeColor.ROSE_PINK -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFFB0D0),
                onPrimary = Color(0xFF5E113E),
                primaryContainer = Color(0xFF7D1B53),
                onPrimaryContainer = Color(0xFFFFD8E6),
                secondary = Color(0xFFE3BDCB),
                onSecondary = Color(0xFF422934),
                secondaryContainer = Color(0xFF5B3F4B),
                onSecondaryContainer = Color(0xFFFFD8E7),
                tertiary = Color(0xFFF2B99E),
                onTertiary = Color(0xFF492613),
                tertiaryContainer = Color(0xFF633B27),
                onTertiaryContainer = Color(0xFFFFDBCF),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF9E2A68),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFD8E6),
                onPrimaryContainer = Color(0xFF3D0023),
                secondary = Color(0xFF745663),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFFFD8E7),
                onSecondaryContainer = Color(0xFF2B1520),
                tertiary = Color(0xFF7E543D),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFFFDBCF),
                onTertiaryContainer = Color(0xFF301305),
            )
        }
        AppThemeColor.CYAN -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFF54D7F3),
                onPrimary = Color(0xFF003641),
                primaryContainer = Color(0xFF004E5D),
                onPrimaryContainer = Color(0xFFB0ECFA),
                secondary = Color(0xFFB2CBD2),
                onSecondary = Color(0xFF1D343A),
                secondaryContainer = Color(0xFF334B51),
                onSecondaryContainer = Color(0xFFCEE7EE),
                tertiary = Color(0xFFBEC6DC),
                onTertiary = Color(0xFF283141),
                tertiaryContainer = Color(0xFF3E4759),
                onTertiaryContainer = Color(0xFFDAE2F9),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF00687A),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFB0ECFA),
                onPrimaryContainer = Color(0xFF001F26),
                secondary = Color(0xFF4B6269),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFCEE7EF),
                onSecondaryContainer = Color(0xFF061E24),
                tertiary = Color(0xFF565E71),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFDAE2F9),
                onTertiaryContainer = Color(0xFF131C2C),
            )
        }
        AppThemeColor.INDIGO -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFBAC3FF),
                onPrimary = Color(0xFF08218A),
                primaryContainer = Color(0xFF253AA2),
                onPrimaryContainer = Color(0xFFDEE0FF),
                secondary = Color(0xFFC4C5DD),
                onSecondary = Color(0xFF2D3042),
                secondaryContainer = Color(0xFF444659),
                onSecondaryContainer = Color(0xFFE0E1F9),
                tertiary = Color(0xFFE7BADA),
                onTertiary = Color(0xFF45263F),
                tertiaryContainer = Color(0xFF5D3C56),
                onTertiaryContainer = Color(0xFFFFD7F4),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF3F51B5),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFDEE0FF),
                onPrimaryContainer = Color(0xFF00105C),
                secondary = Color(0xFF5B5D72),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFE0E1F9),
                onSecondaryContainer = Color(0xFF181A2C),
                tertiary = Color(0xFF775370),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFFFD7F4),
                onTertiaryContainer = Color(0xFF2C112A),
            )
        }
        AppThemeColor.LIME -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFB3D463),
                onPrimary = Color(0xFF263500),
                primaryContainer = Color(0xFF384E00),
                onPrimaryContainer = Color(0xFFCEF17C),
                secondary = Color(0xFFC3CAAE),
                onSecondary = Color(0xFF2D331F),
                secondaryContainer = Color(0xFF434A34),
                onSecondaryContainer = Color(0xFFDFE6C9),
                tertiary = Color(0xFFA1D0C4),
                onTertiary = Color(0xFF03372F),
                tertiaryContainer = Color(0xFF204E45),
                onTertiaryContainer = Color(0xFFBCECE0),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF4C6700),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFCEF17C),
                onPrimaryContainer = Color(0xFF141F00),
                secondary = Color(0xFF5B6149),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFDFE6C9),
                onSecondaryContainer = Color(0xFF181E0B),
                tertiary = Color(0xFF38665C),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFBCECE0),
                onTertiaryContainer = Color(0xFF00201B),
            )
        }
        AppThemeColor.SAKURA -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFFB1C2),
                onPrimary = Color(0xFF5F112D),
                primaryContainer = Color(0xFF7D2943),
                onPrimaryContainer = Color(0xFFFFD9DF),
                secondary = Color(0xFFE4BDC4),
                onSecondary = Color(0xFF432930),
                secondaryContainer = Color(0xFF5C3F46),
                onSecondaryContainer = Color(0xFFFFD9DF),
                tertiary = Color(0xFFEFBD94),
                onTertiary = Color(0xFF472A0C),
                tertiaryContainer = Color(0xFF613F1F),
                onTertiaryContainer = Color(0xFFFFDCBE),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF9B405A),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFD9DF),
                onPrimaryContainer = Color(0xFF3F0018),
                secondary = Color(0xFF75565D),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFFFD9DF),
                onSecondaryContainer = Color(0xFF2B151B),
                tertiary = Color(0xFF7B5734),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFFFDCBE),
                onTertiaryContainer = Color(0xFF2D1600),
            )
        }
    }
}

package kz.taskbar.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object TB {
    val Lime = Color(0xFFCCFF72)
    val LimeInk = Color(0xFF101808)
    val Violet = Color(0xFFA69CFF)
    val Blue = Color(0xFF8DCCFF)
    val Coral = Color(0xFFFFA899)
    val Deep = Color(0xFF0B0E19)
    val Panel = Color(0xFF171B2A)
    val Panel2 = Color(0xFF202538)
    val Muted = Color(0xFF9FA7BB)
    val Border = Color(0xFF2B3044)
}

private val DarkScheme = darkColorScheme(
    primary = TB.Lime,
    onPrimary = TB.LimeInk,
    background = TB.Deep,
    onBackground = Color(0xFFF6F7FC),
    surface = TB.Panel,
    onSurface = Color(0xFFF5F5FC),
    surfaceVariant = TB.Panel2,
    onSurfaceVariant = TB.Muted,
    outline = TB.Border,
    secondary = TB.Violet,
    onSecondary = Color(0xFF1C1638),
    error = Color(0xFFFFA7A7),
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF4C6028),
    onPrimary = Color.White,
    background = Color(0xFFF6F7FA),
    onBackground = Color(0xFF151827),
    surface = Color.White,
    onSurface = Color(0xFF171B2A),
    surfaceVariant = Color(0xFFEAEDF5),
    onSurfaceVariant = Color(0xFF687083),
    outline = Color(0xFFDDE1E9),
    secondary = Color(0xFF5B4CC6),
    onSecondary = Color.White,
    error = Color(0xFFC33C3C),
)

private val TaskBarTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 42.sp, lineHeight = 46.sp, letterSpacing = (-1.6).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.9).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 25.sp, lineHeight = 30.sp, letterSpacing = (-0.7).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 1.sp),
)

@Composable
fun TaskBarTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = TaskBarTypography, content = content)
}

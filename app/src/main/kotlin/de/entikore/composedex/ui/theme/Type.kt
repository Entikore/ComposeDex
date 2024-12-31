/*
 * Copyright 2024 Entikore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.entikore.composedex.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import de.entikore.composedex.R

val provider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

val customFont = FontFamily(
    Font(
        googleFont = GoogleFont("Press Start 2P"),
        fontProvider = provider,
    )
)

val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = customFont),
    displayMedium = baseline.displayMedium.copy(fontFamily = customFont),
    displaySmall = baseline.displaySmall.copy(fontFamily = customFont),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = customFont),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = customFont),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = customFont),
    titleLarge = baseline.titleLarge.copy(fontFamily = customFont),
    titleMedium = baseline.titleMedium.copy(fontFamily = customFont),
    titleSmall = baseline.titleSmall.copy(fontFamily = customFont),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = customFont),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = customFont),
    bodySmall = baseline.bodySmall.copy(fontFamily = customFont),
    labelLarge = baseline.labelLarge.copy(fontFamily = customFont),
    labelMedium = baseline.labelMedium.copy(fontFamily = customFont),
    labelSmall = baseline.labelSmall.copy(fontFamily = customFont),
)

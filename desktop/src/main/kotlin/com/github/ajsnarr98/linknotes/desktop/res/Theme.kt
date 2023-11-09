package com.github.ajsnarr98.linknotes.desktop.res

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun LinkNotesDesktopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}

val typography: Typography
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.typography

val colors: Colors
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colors

val shapes: Shapes
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.shapes


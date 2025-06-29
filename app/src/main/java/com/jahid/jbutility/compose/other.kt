package com.jahid.jbutility.compose


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.jahid.jbutility.R
import java.io.File

@Composable
fun BigTitle(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
        fontSize = 65.sp,
        color = colorResource(R.color.light_grey)
    )
}

@Composable
fun Desc(name: String, modifier: Modifier = Modifier,
         fontSize: TextUnit = 25.sp) {
    Text(
        text = name,
        modifier = modifier,
        fontSize = fontSize,
        color = colorResource(R.color.slate_blue)
    )
}


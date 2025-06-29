package com.jahid.jbutility

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.jahid.jbutility.ui.theme.JbUtilityTheme
import com.jahid.jbutility.util.VibratorUtil.vibrate
import com.jahid.jbutility.util.hidestatusbar
import java.io.File
import java.io.FileOutputStream

data class WallpaperItem(val label: String, val drawable: Drawable?)

class WallpaperExtract : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hidestatusbar(window)
        setContent {
            JbUtilityTheme {
                WallpaperScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen(activity: ComponentActivity) {
    val context = LocalContext.current
    var wallpapers by remember { mutableStateOf<List<WallpaperItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var permissionDenied by remember { mutableStateOf(false) }

    // Permission check
    val hasPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request permission if not granted
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionDenied = true
        } else {
            wallpapers = loadWallpapers(context)
            isLoading = false
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text("Wallpaper Extract")
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Menu",
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clickable {
                                    vibrate(context)
                                    activity.finish()
                                }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(colorResource(R.color.dark))
                )

            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colorResource(id = R.color.grey_background))
            ) {
                when {
                    permissionDenied -> {
                        PermissionRequestView(activity)
                    }

                    isLoading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }

                    wallpapers.isEmpty() -> {
                        Text(
                            "No wallpapers found.",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                32.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            wallpapers.forEach { item ->
                                item.drawable?.let { drawable ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        elevation = CardDefaults.cardElevation(8.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(item.label, color = Color.White)
                                            Spacer(Modifier.height(8.dp))
                                            val previewBitmap = remember(drawable) {
                                                (drawable as? BitmapDrawable)?.bitmap
                                                    ?: drawableToBitmap(drawable)
                                            }
                                            Image(
                                                bitmap = previewBitmap.asImageBitmap(),
                                                contentDescription = "${item.label} Preview",
                                                modifier = Modifier
                                                    .height(180.dp)
                                                    .fillMaxWidth(),
                                                contentScale = ContentScale.Fit
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Button(onClick = {
                                                    vibrate(context)
                                                    saveDrawableToStorage(context, drawable)
                                                }) {
                                                    Text("Save")
                                                }
                                                Button(onClick = {
                                                    vibrate(context)
                                                    shareDrawable(context, drawable)
                                                }) {
                                                    Text("Share")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRequestView(activity: ComponentActivity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Permission required to access wallpapers.", color = Color.White)
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            activity.requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 0)
        }) {
            Text("Grant Permission")
        }
    }
}

fun loadWallpapers(context: Context): List<WallpaperItem> {
    val wm = WallpaperManager.getInstance(context)
    val list = mutableListOf<WallpaperItem>()

    // System wallpaper
    val sysDrawable = wm.drawable
    list.add(WallpaperItem("System Wallpaper", sysDrawable))

    // Built-in wallpaper (may be null on some devices)
    val builtInDrawable = try {
        wm.getBuiltInDrawable(3000, 3000, false, 0.5f, 0.5f)
    } catch (e: Exception) { null }
    list.add(WallpaperItem("Built-in Wallpaper", builtInDrawable))

    // Lockscreen wallpaper (if available)
    val lockDrawable =
        try {
            wm.getWallpaperFile(WallpaperManager.FLAG_LOCK)?.use {
                BitmapDrawable(context.resources, android.graphics.BitmapFactory.decodeFileDescriptor(it.fileDescriptor))
            }
        } catch (e: Exception) { null }
    list.add(WallpaperItem("Lockscreen Wallpaper", lockDrawable))

    return list
}

// Helper: Convert Drawable to Bitmap
fun drawableToBitmap(drawable: Drawable): Bitmap {
    val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
    val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
    return bitmap
}

// Save Drawable as PNG (matches WallpaperExport logic)
fun saveDrawableToStorage(context: Context, drawable: Drawable) {
    try {
        val dir = File(Environment.getExternalStorageDirectory(), "Utility_JB/wallpaper")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "wallpaper_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { stream ->
            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                Toast.makeText(context, "Saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

// Share Drawable as PNG (matches WallpaperExport logic)
fun shareDrawable(context: Context, drawable: Drawable) {
    try {
        val cachePath = File(context.externalCacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_wallpaper.png")
        FileOutputStream(file).use { stream ->
            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Wallpaper"))
    } catch (e: Exception) {
        Toast.makeText(context, "Share error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

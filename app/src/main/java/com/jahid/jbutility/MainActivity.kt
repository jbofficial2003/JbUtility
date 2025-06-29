package com.jahid.jbutility

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.jahid.jbutility.ui.theme.JbUtilityTheme
import com.jahid.jbutility.util.hidestatusbar
import java.io.File
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jahid.jbutility.util.VibratorUtil.vibrate

class MainActivity : ComponentActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hidestatusbar(window)
        initPermissionLauncher()
        makedir()
        checkAndRequestAllFilesAccess()
        checkPermissionsOnStartup()
        Toast.makeText(this, "Utility made by Jahid", Toast.LENGTH_SHORT).show()

        setContent {
            JbUtilityTheme {
                MyApp()
            }
        }
    }
    @Preview
    @Composable
    fun jb(){
        MyApp()
    }

    private fun initPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
            if (granted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun makedir() {
        if (Environment.isExternalStorageManager()) {
            val utilityDir = File(Environment.getExternalStorageDirectory(), "Utility_JB")
            val apkDir = File(utilityDir, "apk")
            val wallpaperDir = File(utilityDir, "wallpaper")
            apkDir.mkdirs()
            wallpaperDir.mkdirs()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun checkAndRequestAllFilesAccess() {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun checkPermissionsOnStartup() {
        if (hasStoragePermission()) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyApp() {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(modifier = Modifier.width(250.dp)) {

                        Text(
                            text = "Menu",
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        NavigationDrawerItem(
                            label = { Text("Home") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home"
                                )
                            },
                            selected = true,
                            onClick = {
                                vibrate(context)
                                scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        NavigationDrawerItem(
                            label = { Text("Settings") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            selected = false,
                            onClick = {
                                vibrate(context)
                                scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "Others",
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        NavigationDrawerItem(
                            label = { Text("About") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "About"
                                )
                            },
                            selected = false,
                            onClick = {
                                vibrate(context)
                                context.startActivity(Intent(context, About::class.java))
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(stringResource(R.string.app_name))
                                }
                            },
                            navigationIcon = {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .clickable {

                                            vibrate(context,30)
                                            scope.launch { drawerState.open() }
                                        }
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(colorResource(R.color.dark))
                        )
                    }
                ) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val backgroundColor = colorResource(id = R.color.grey_background)
        val lightGrey = colorResource(id = R.color.light_grey)
        val white = colorResource(id = R.color.white)

        val features = listOf(
            Feature("Wallpaper", R.drawable.wallpaper_icon),
            Feature("ApkExtract", R.drawable.apk_icon),
            Feature("Calculator", R.drawable.calc_icon),
            Feature("Coming soon", R.drawable.coming_icon)
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Text(
                text = "Hello Jahid Hussain",
                fontSize = 20.sp,
                color = white,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(features) { feature ->
                    FeatureCard(
                        title = feature.title,
                        icon = feature.iconRes,
                        backgroundColor = lightGrey,
                        textColor = backgroundColor,
                        onClick = {
                            vibrate(context)
                            when (feature.title) {
                                "Wallpaper" -> {
                                    context.startActivity(Intent(context, WallpaperExtract::class.java))
                                }
                                "ApkExtract" -> {
                                context.startActivity(Intent(context, ApkExtract::class.java))
                                }
                                "Calculator" -> {
                                    context.startActivity(Intent(context, CalC::class.java))
                                }
                                "Coming soon" -> {
                                    context.startActivity(Intent(context, Coming::class.java))
                                }

                            }
                        }

                    )
                }
            }
        }
    }


    @Composable
    fun FeatureCard(
        title: String,
        @DrawableRes icon: Int,
        backgroundColor: Color,
        textColor: Color,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { onClick() },
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(75.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = title, fontSize = 20.sp, color = textColor)
            }
        }
    }

    data class Feature(val title: String, @DrawableRes val iconRes: Int)

}




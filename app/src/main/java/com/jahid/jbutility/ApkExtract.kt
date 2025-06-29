package com.jahid.jbutility

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jahid.jbutility.ui.theme.JbUtilityTheme
import com.jahid.jbutility.util.hidestatusbar
import com.jahid.jbutility.util.VibratorUtil.vibrate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ApkExtract : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hidestatusbar(window)
        setContent {
            JbUtilityTheme {
                ApkExtractScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkExtractScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val pm = context.packageManager

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var allApps by remember { mutableStateOf<List<android.content.pm.ApplicationInfo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // Load apps asynchronously
    LaunchedEffect(Unit) {
        loading = true
        allApps = withContext(Dispatchers.IO) {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .sortedBy { it.loadLabel(pm).toString().lowercase() }
        }
        loading = false
    }


    val filteredApps = allApps.filter {
        it.loadLabel(pm).toString().contains(searchQuery.text, ignoreCase = true)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.grey_background))
        ) {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text("Apk Extract")
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable {
                                vibrate(context)
                                activity.finish()
                            }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.dark)
                )
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search apps...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (loading) {
                // Show progress bar while loading
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorResource(R.color.white))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredApps) { app ->
                        val appName = app.loadLabel(pm).toString()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = rememberRipple(color = Color.LightGray),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    try {
                                        val sourcePath = app.sourceDir
                                        val input = FileInputStream(sourcePath)
                                        val outputDir = File(
                                            Environment.getExternalStorageDirectory(),
                                            "utility_JB/apk"
                                        )
                                        outputDir.mkdirs()
                                        val outputFile = File(outputDir, "$appName.apk")
                                        val output = FileOutputStream(outputFile)

                                        input.copyTo(output)
                                        input.close()
                                        output.close()

                                        Toast.makeText(
                                            context,
                                            "Saved: ${outputFile.absolutePath}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    vibrate(context)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Text(
                                    text = appName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = colorResource(R.color.white)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

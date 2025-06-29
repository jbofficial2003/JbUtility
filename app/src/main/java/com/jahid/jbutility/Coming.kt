package com.jahid.jbutility

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jahid.jbutility.compose.BigTitle
import com.jahid.jbutility.compose.Desc
import com.jahid.jbutility.ui.theme.JbUtilityTheme
import com.jahid.jbutility.util.hidestatusbar
import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import com.jahid.jbutility.util.VibratorUtil.vibrate

class Coming : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hidestatusbar(window)
        setContent {
            JbUtilityTheme {
                ComingCompose()
            }
        }
    }
    @SuppressLint("ContextCastToActivity")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ComingCompose(){
        val context = LocalContext.current as Activity
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr){
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row (modifier = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalArrangement = Arrangement.End
                            ){
                                Text("Coming Soon")
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
                                        context.finish()
                                    }
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(colorResource(R.color.dark))
                    )

                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(colorResource(id = R.color.grey_background)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    BigTitle("Comming soon")
                    Spacer(modifier = Modifier.height(16.dp))
                    Desc(stringResource(R.string.coming_soon_line), modifier = Modifier, fontSize = 17.sp)
                }
            }
        }
    }

}






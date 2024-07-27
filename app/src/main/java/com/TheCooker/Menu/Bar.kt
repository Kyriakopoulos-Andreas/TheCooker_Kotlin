package com.TheCooker.Menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String,
           goBack: () -> Unit){

        NavigationBar(
            tonalElevation = 4.dp,
            containerColor = Color(0xFF202020),
            modifier = Modifier.height(54.dp)
        ) {
            IconButton(onClick = { goBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White)
            }
            Spacer(modifier = Modifier.padding(start = 100.dp))

                Text(text = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterVertically),
                    color = Color.White,
                )

            }


        }









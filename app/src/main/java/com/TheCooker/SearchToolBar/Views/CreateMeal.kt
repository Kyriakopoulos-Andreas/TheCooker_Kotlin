package com.TheCooker.SearchToolBar.Views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.TheCooker.Login.LoginViewModel
import com.TheCooker.R
import com.TheCooker.SearchToolBar.ViewModels.CreateMealViewModel
import com.example.cooker.ListView.CustomDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMeal(){
    val painter = "android.resource://com.TheCooker/" + R.drawable.addmeal
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val createMealViewModel :  CreateMealViewModel = viewModel()

    val mealName by createMealViewModel.mealName

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()){
            uri: Uri? ->
            uri?.let {
                imageUri = it
            }
        }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)) {
                    // Η εικόνα
                    AsyncImage(
                        model = painter,
                        contentDescription = "Add Meal Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = RoundedCornerShape(16.dp))
                            .clickable { launcher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )

                    // Το εικονίδιο της κάμερας πάνω από την εικόνα
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_a_photo_24),  // Φόρτωση του drawable ως Painter
                        contentDescription = "Camera Icon",
                        modifier = Modifier
                            .size(48.dp)  // Μέγεθος του εικονιδίου
                            .align(Alignment.BottomEnd)  // Θέση του εικονιδίου (π.χ. κάτω δεξιά)
                            .padding(8.dp),  // Απόσταση από τις άκρες
                        tint = Color(0xFFFFE000)  // Χρώμα του εικονιδίου
                    )

                }

            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            CustomDivider()


        }

        item {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Title",
                        color = Color(0xFFFFC107),
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))  // Εισαγωγή κενής απόστασης ανάμεσα στο Title και το TextField
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        value = mealName,
                        onValueChange = { createMealViewModel.onMealNameChange(it) },
                        label = { Text("Meal Title") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xAAFFC107),
                            cursorColor = Color(0xFFFFC107),
                            focusedLabelColor = Color(0xFFFFC107),
                            unfocusedLabelColor = Color(0xFFFFC107),
                            focusedTextColor = Color(0xFFFFC107),
                            unfocusedTextColor = Color(0xAAFFC107),
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
            CustomDivider()
        }
        item {
            Column(Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ingredients",
                        color = Color(0xFFFFC107),
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))  // Εισαγωγή κενής απόστασης ανάμεσα στο Title και το TextField

                // Χρησιμοποιούμε Column για να στοιχίσουμε τα TextFields κάθετα
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 64.dp, top = 8.dp, bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = mealName,
                        onValueChange = { createMealViewModel.onMealNameChange(it) },
                        label = { Text("Ingredient 1") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xAAFFC107),
                            cursorColor = Color(0xFFFFC107),
                            focusedLabelColor = Color(0xFFFFC107),
                            unfocusedLabelColor = Color(0xFFFFC107),
                            focusedTextColor = Color(0xFFFFC107),
                            unfocusedTextColor = Color(0xAAFFC107),
                            containerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))  // Απόσταση μεταξύ των TextFields

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = mealName,
                        onValueChange = { createMealViewModel.onMealNameChange(it) },
                        label = { Text("Ingredient 2") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xAAFFC107),
                            cursorColor = Color(0xFFFFC107),
                            focusedLabelColor = Color(0xFFFFC107),
                            unfocusedLabelColor = Color(0xFFFFC107),
                            focusedTextColor = Color(0xFFFFC107),
                            unfocusedTextColor = Color(0xAAFFC107),
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
        }

    }
}



/*
            LazyRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                item{
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center) {
                        TextField(value = {}, onValueChange = )
                    }
                }

            }
 */
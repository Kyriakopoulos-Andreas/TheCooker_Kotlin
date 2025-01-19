package com.TheCooker.Presentation.Views.Modules.ProfileModule.Views

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.R
import kotlinx.coroutines.launch
import okhttp3.internal.wait

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun ProfileInformationView(
    profileViewModel: ProfileViewModel,
) {
    val info by remember { profileViewModel.information }
    val editProfile by remember { profileViewModel.editProfile }
    var expandedChefLevel by remember { mutableStateOf(false) }
    val selectedOption = profileViewModel.chefLevel.value
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    val userInfo = listOfNotNull(
        profileViewModel.country.value.takeIf { it.isNotBlank() }?.let { "Country" to it },
        profileViewModel.city.value.takeIf { it.isNotBlank() }?.let { "City" to it },
        profileViewModel.chefLevel.value.takeIf { it.isNotBlank() }?.let { "Chef Level" to it },
        profileViewModel.specialties.value.takeIf { it.isNotBlank() }?.let { "Specialties" to it },
        profileViewModel.goldenChefHats.takeIf { it.value > 0 }
            ?.let { "Golden Chef Hats" to profileViewModel.goldenChefHats.value.toString() }
    )






    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (info) {
            userInfo.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "$label: ",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.yellow),
                        fontSize = 20.sp
                    )
                    Text(
                        text = value,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (userInfo.isEmpty()) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tap on Edit Profile to add information to your Profile",
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }
        }

        if (editProfile) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Edit Profile",
                    color = colorResource(id = R.color.white),
                    fontSize = 24.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = profileViewModel.country.value,
                    onValueChange = {
                        profileViewModel.setCountry(it)
                    },
                    singleLine = true,
                    label = {
                        Text(
                            text = "Country",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight(300)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = profileViewModel.city.value,
                    onValueChange = {
                        profileViewModel.setCity(it)
                    },
                    singleLine = true,
                    label = {
                        Text(
                            text = "City",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight(300)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = profileViewModel.specialties.value,
                    onValueChange = {
                        profileViewModel.setSpecialties(it)
                    },
                    singleLine = true,
                    label = {
                        Text(
                            text = "Specialties",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight(300)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = selectedOption,
                    onValueChange = {},
                    shape = RoundedCornerShape(16.dp),
                    readOnly = true,
                    label = { Text("Chef Level") },
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = if (expandedChefLevel) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                expandedChefLevel = !expandedChefLevel
                            },
                            tint = colorResource(id = R.color.yellow)
                        )
                    },
                    modifier = Modifier.clickable {
                        expandedChefLevel = true
                    },
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

                DropdownMenu(
                    expanded = expandedChefLevel,
                    onDismissRequest = { expandedChefLevel = false },
                    offset = DpOffset(300.dp, 12.dp),
                    modifier = Modifier
                        .background(color = colorResource(id = R.color.darkGrey))
                        .height(195.dp)
                        .border(
                            color = Color(0xAAFFC107),
                            width = 2.dp
                        )
                        .clip(shape = RoundedCornerShape(32.dp))
                ) {
                    profileViewModel.chefLevelList.forEach { label ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = label,
                                    color = Color.White,
                                )
                            },
                            onClick = {
                                profileViewModel.setChefLevel(label)
                                expandedChefLevel = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Save Changes",
                    color = colorResource(id = R.color.white),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        scope.launch {
                            profileViewModel.saveInformation()
                            if (!isInternetAvailable(context)) {
                                Toast.makeText(
                                    context,
                                    "No internet connection",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@launch
                            } else if (profileViewModel.saveInfoResult.value != null) {
                                Toast.makeText(
                                    context,
                                    profileViewModel.saveInfoResult.value,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                profileViewModel.setInformation(true)
                                profileViewModel.setProfileManagement(false)
                            }
                        }
                    }
                )
            }
        }
    }
}





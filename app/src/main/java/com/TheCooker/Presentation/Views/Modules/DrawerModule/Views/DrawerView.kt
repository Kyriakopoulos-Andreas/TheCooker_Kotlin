package com.example.cooker.ListView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.TheCooker.Domain.Layer.Models.ScreenModels.DrawerScreensModel
import com.TheCooker.R

@Composable
fun DrawerContent(
    title: MutableState<String>,
    onItemSelected: (String) -> Unit,
    closeDrawer: () -> Unit,
    currentRoute: String,
    navcontroller: NavController,
    dialogOpen: MutableState<Boolean>,
    topBarRoute: MutableState<Boolean>,

    ) {






    Column(modifier = Modifier
        .fillMaxSize()
        ){
        Box(modifier = Modifier.fillMaxWidth()){
            Image(painter = painterResource(id = R.drawable.logo_white),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 8.dp))
        }
        CustomDivider()
        Spacer(modifier = Modifier.height(84.dp))


        LazyColumn(modifier = Modifier
            .padding(16.dp)
            .background(color = Color(0xFF202020)),
        ) {
            items(DrawerScreensModel.drawerScreensList.size) { index ->
                val item = DrawerScreensModel.drawerScreensList[index]
                DrawerItem(
                    item = item,
                    selected = currentRoute == item.route, // Έλεγχος για την τρέχουσα διαδρομή
                    onClickItem = {
                        if(item.route == "LogOut"){
                            dialogOpen.value = true}
                        else {
                            onItemSelected(item.route)
                            title.value = item.title

                        }
                    }
                )
            }
        }

    }
}

@Composable
fun DrawerItem(
    onClickItem: () -> Unit,
    item: DrawerScreensModel,
    selected: Boolean
) {


    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 105.dp, vertical = 16.dp)
            .background(color = Color.Transparent)
            .clickable {
                onClickItem()
            },
    ) {
        Icon(
            painter = painterResource(id = item.icon!!), // Εξασφάλισε ότι το icon δεν είναι null
            contentDescription = item.title,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .padding(end = 8.dp, top = 4.dp)
                .background(color = Color(0xFF202020), shape = CircleShape),
        )

        Text(
            text = item.title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
    }


}

@Composable
fun CustomDivider() {
    val DividerAlpha = 0.5f // Αλλάξε την τιμή ανάλογα με τις προτιμήσεις σου
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = DividerAlpha)),
        color = Color(0xFFFFC107), // Χρησιμοποιούμε background αντί για το color εδώ
        thickness = 4.dp
    )
}
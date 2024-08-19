package com.example.cooker.ListView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.TheCooker.Drawer.DrawerScreens
import com.google.protobuf.Internal.BooleanList

@Composable
fun DrawerContent(
    onItemSelected: (String) -> Unit,
    closeDrawer: () -> Unit,
    currentRoute: String // Πρόσθεσε το currentRoute ως παράμετρο
) {
    Column(modifier = Modifier.fillMaxSize().background(color = Color(0xFF202020))){
        LazyColumn(modifier = Modifier.padding(16.dp).background(color = Color(0xFF202020)),
        ) {
            items(DrawerScreens.drawerScreensList.size) { index ->
                val item = DrawerScreens.drawerScreensList[index]
                DrawerItem(
                    item = item,
                    selected = currentRoute == item.route, // Έλεγχος για την τρέχουσα διαδρομή
                    onClickItem = {
                        onItemSelected(item.route)
                        closeDrawer()
                    }
                )
            }
        }

    }
}

@Composable
fun DrawerItem(
    onClickItem: () -> Unit,
    item: DrawerScreens,
    selected: Boolean
) {
    val background = if (selected) Color(0xFF202020) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .background(background)
            .clickable {
                onClickItem()
            },
    ) {
        Icon(
            painter = painterResource(id = item.icon!!), // Εξασφάλισε ότι το icon δεν είναι null
            contentDescription = item.title,
            modifier = Modifier.padding(end = 8.dp, top = 4.dp).background(color = Color(0xFF202020), shape = CircleShape),
        )

        Text(
            text = item.title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
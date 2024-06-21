package com.example.gasolinerias.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    companion object {
        fun Home(title: String) = BottomNavItem(title, Icons.Default.Home, "home")
        fun Favs(title: String) = BottomNavItem(title, Icons.Default.Star, "favs")
        fun Profile(title: String) = BottomNavItem(title, Icons.Default.Person, "profile")
    }
}

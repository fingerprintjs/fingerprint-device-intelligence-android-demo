@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.fingerprintjs.android.fpjs_pro_demo.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.verticalExpandTransition
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.verticalShrinkTransition
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.HomeScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.details.SettingsDetailsScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.main.SettingsScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var navigationBarHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = Screen.from(currentDestination?.route)?.withNavBar == true,
                enter = verticalExpandTransition(),
                exit = verticalShrinkTransition(),
            ) {
                NavigationBar(
                    modifier = Modifier
                        .onGloballyPositioned { navigationBarHeight = it.size.height },
                    containerColor = AppTheme.materialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Tab.entries.forEach { tab ->
                        val route = tab.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                )
                            },
                            label = { Text(tab.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = { navController.openTab(tab) },
                            colors = NavigationBarItemDefaults.colors().copy(
                                selectedIconColor = AppTheme.materialTheme.colorScheme.primary,
                                selectedTextColor = AppTheme.materialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = Tab.Home.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) {
                @Composable
                fun Modifier.respectNavBarSizeAccordingTo(screen: Screen): Modifier {
                    return if (screen.withNavBar) {
                        padding(bottom = with(LocalDensity.current) {
                            navigationBarHeight.toDp()
                        })
                    } else this
                }

                navigation(
                    route = Tab.Home.route,
                    startDestination = Screen.Home.absoluteRoute
                ) {
                    composable(Screen.Home.absoluteRoute) {
                        Column(
                            modifier = Modifier
                                .respectNavBarSizeAccordingTo(Screen.Home)
                        ) {
                            HomeScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                onGoToApiKeysSettings = { navController.openScreen(Screen.SettingsDetails) }
                            )
                        }
                    }
                }
                navigation(
                    route = Tab.Settings.route,
                    startDestination = Screen.Settings.absoluteRoute,
                ) {
                    composable(route = Screen.Settings.absoluteRoute) {
                        SettingsScreen(
                            modifier = Modifier
                                .respectNavBarSizeAccordingTo(Screen.Settings),
                            onGoToDetails = { navController.openScreen(Screen.SettingsDetails) },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this,
                        )
                    }
                    composable(
                        route = Screen.SettingsDetails.absoluteRoute,
                    ) {
                        SettingsDetailsScreen(
                            modifier = Modifier.respectNavBarSizeAccordingTo(Screen.SettingsDetails),
                            onGoBack = { navController.popBackStack() },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this,
                        )
                    }
                }
            }
        }
    }
}

private fun NavController.openTab(tab: Tab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavController.openScreen(screen: Screen) {
    openTab(screen.tab)
    navigate(screen.absoluteRoute) {
        launchSingleTop = true
    }
}

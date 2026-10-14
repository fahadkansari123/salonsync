package com.salonsync.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.salonsync.app.data.model.Role
import com.salonsync.app.ui.screens.admin.AdminDashboardScreen
import com.salonsync.app.ui.screens.admin.AdminServicesScreen
import com.salonsync.app.ui.screens.auth.LoginScreen
import com.salonsync.app.ui.screens.auth.RegisterScreen
import com.salonsync.app.ui.screens.customer.BookingSummaryScreen
import com.salonsync.app.ui.screens.customer.CustomerBookingsScreen
import com.salonsync.app.ui.screens.customer.CustomerHomeScreen
import com.salonsync.app.ui.screens.customer.SlotPickerScreen
import com.salonsync.app.ui.screens.staff.StaffScheduleScreen
import com.salonsync.app.ui.theme.CardBorder
import com.salonsync.app.ui.theme.SlateDark
import com.salonsync.app.ui.theme.SoftGold
import com.salonsync.app.ui.theme.SurfaceDark
import com.salonsync.app.ui.theme.TextMuted
import com.salonsync.app.ui.theme.WarmGold
import com.salonsync.app.ui.viewmodel.AdminViewModel
import com.salonsync.app.ui.viewmodel.AuthState
import com.salonsync.app.ui.viewmodel.AuthViewModel
import com.salonsync.app.ui.viewmodel.CustomerViewModel
import com.salonsync.app.ui.viewmodel.StaffViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CustomerHome : Screen("customer_home")
    object SlotPicker : Screen("slot_picker")
    object BookingSummary : Screen("booking_summary")
    object CustomerBookings : Screen("customer_bookings")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminServices : Screen("admin_services")
    object StaffSchedule : Screen("staff_schedule")
    object Profile : Screen("profile")
}

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel = viewModel(),
    customerViewModel: CustomerViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel(),
    staffViewModel: StaffViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val customerUiState by customerViewModel.uiState.collectAsState()
    val adminUiState by adminViewModel.uiState.collectAsState()
    val staffUiState by staffViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isAuthScreen = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route

    androidx.compose.runtime.LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                val targetRoute = when (state.user.roleEnum) {
                    Role.ADMIN -> {
                        adminViewModel.loadAllBookings()
                        Screen.AdminDashboard.route
                    }
                    Role.STAFF -> {
                        staffViewModel.loadStaffBookings()
                        Screen.StaffSchedule.route
                    }
                    else -> {
                        customerViewModel.loadMyBookings()
                        customerViewModel.loadUserProfile()
                        Screen.CustomerHome.route
                    }
                }
                if (currentRoute != targetRoute) {
                    navController.navigate(targetRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.Idle -> {
                if (currentRoute != null && currentRoute != Screen.Login.route && currentRoute != Screen.Register.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }

    Scaffold(
        bottomBar = {
            if (!isAuthScreen && authState is AuthState.Authenticated) {
                val user = (authState as AuthState.Authenticated).user
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 8.dp
                ) {
                    if (user.roleEnum == Role.CUSTOMER) {
                        NavigationBarItem(
                            selected = currentRoute == Screen.CustomerHome.route,
                            onClick = { navController.navigate(Screen.CustomerHome.route) },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text("Services", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = SlateDark, selectedTextColor = SoftGold, indicatorColor = WarmGold, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                        )
                        NavigationBarItem(
                            selected = currentRoute == Screen.CustomerBookings.route,
                            onClick = {
                                customerViewModel.loadMyBookings()
                                navController.navigate(Screen.CustomerBookings.route)
                            },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                            label = { Text("My Bookings", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = SlateDark, selectedTextColor = SoftGold, indicatorColor = WarmGold, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                        )
                    } else if (user.roleEnum == Role.ADMIN) {
                        NavigationBarItem(
                            selected = currentRoute == Screen.AdminDashboard.route,
                            onClick = { navController.navigate(Screen.AdminDashboard.route) },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text("Dashboard", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = SlateDark, selectedTextColor = SoftGold, indicatorColor = WarmGold, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                        )
                        NavigationBarItem(
                            selected = currentRoute == Screen.AdminServices.route,
                            onClick = { navController.navigate(Screen.AdminServices.route) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                            label = { Text("Services", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = SlateDark, selectedTextColor = SoftGold, indicatorColor = WarmGold, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                        )
                    } else if (user.roleEnum == Role.STAFF) {
                        NavigationBarItem(
                            selected = currentRoute == Screen.StaffSchedule.route,
                            onClick = { navController.navigate(Screen.StaffSchedule.route) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                            label = { Text("Schedule", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = SlateDark, selectedTextColor = SoftGold, indicatorColor = WarmGold, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                        )
                    }

                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = { navController.navigate(Screen.Profile.route) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SlateDark, selectedTextColor = SoftGold, indicatorColor = WarmGold, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authState is AuthState.Authenticated) {
                when ((authState as AuthState.Authenticated).user.roleEnum) {
                    Role.ADMIN -> Screen.AdminDashboard.route
                    Role.STAFF -> Screen.StaffSchedule.route
                    else -> Screen.CustomerHome.route
                }
            } else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    authState = authState,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    authState = authState,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.CustomerHome.route) {
                CustomerHomeScreen(
                    viewModel = customerViewModel,
                    uiState = customerUiState,
                    onSelectServiceForBooking = { navController.navigate(Screen.SlotPicker.route) }
                )
            }

            composable(Screen.SlotPicker.route) {
                SlotPickerScreen(
                    viewModel = customerViewModel,
                    uiState = customerUiState,
                    onNavigateToSummary = { navController.navigate(Screen.BookingSummary.route) }
                )
            }

            composable(Screen.BookingSummary.route) {
                BookingSummaryScreen(
                    viewModel = customerViewModel,
                    uiState = customerUiState,
                    onBookingSuccess = {
                        customerViewModel.clearBookingSuccess()
                        navController.navigate(Screen.CustomerBookings.route) {
                            popUpTo(Screen.CustomerHome.route)
                        }
                    }
                )
            }

            composable(Screen.CustomerBookings.route) {
                CustomerBookingsScreen(uiState = customerUiState)
            }

            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    viewModel = adminViewModel,
                    uiState = adminUiState,
                    onNavigateToServices = { navController.navigate(Screen.AdminServices.route) }
                )
            }

            composable(Screen.AdminServices.route) {
                AdminServicesScreen(
                    viewModel = adminViewModel,
                    uiState = adminUiState
                )
            }

            composable(Screen.StaffSchedule.route) {
                StaffScheduleScreen(
                    viewModel = staffViewModel,
                    uiState = staffUiState
                )
            }

            composable(Screen.Profile.route) {
                if (authState is AuthState.Authenticated) {
                    val user = (authState as AuthState.Authenticated).user
                    com.salonsync.app.ui.screens.profile.ProfileScreen(
                        user = user,
                        authViewModel = authViewModel,
                        onSignOut = {
                            authViewModel.signOut()
                        }
                    )
                }
            }
        }
    }
}

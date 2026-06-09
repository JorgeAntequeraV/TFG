package com.example.tfg.ui.nav

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.tfg.BuyNotesApp
import com.example.tfg.ui.components.BottomTab
import com.example.tfg.ui.screens.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun AppNavigation(intentsFlow: StateFlow<Intent?> = MutableStateFlow(null)) {
    val navController = rememberNavController()
    val token by BuyNotesApp.instance.sessionManager.tokenFlow.collectAsState(initial = null)

    val startDestination = if (token.isNullOrBlank()) Routes.LOGIN else Routes.LISTAS

    //Procesa deep links incluso cuando la app ya está abierta gracias al onNewIntent.
    LaunchedEffect(navController) {
        intentsFlow.filterNotNull().collect { intent ->
            if (intent.data != null) {
                navController.handleDeepLink(intent)
            }
        }
    }

    fun goTab(tab: BottomTab) {
        when (tab) {
            BottomTab.LISTAS -> navController.navigate(Routes.LISTAS) {
                popUpTo(Routes.LISTAS) { inclusive = true }
            }
            BottomTab.FAVORITOS -> navController.navigate(Routes.FAVORITOS) {
                popUpTo(Routes.LISTAS)
            }
            BottomTab.PERFIL -> navController.navigate(Routes.PERFIL) {
                popUpTo(Routes.LISTAS)
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.LISTAS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegistro = { navController.navigate(Routes.REGISTRO) },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.RESET_PASSWORD,
            arguments = listOf(navArgument("token") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "buynotes://reset-password?token={token}" }
            )
        ) { entry ->
            val token = entry.arguments?.getString("token").orEmpty()
            ResetPasswordScreen(
                token = token,
                onDone = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.REGISTRO) {
            RegistroScreen(
                onRegistroSuccess = {
                    navController.navigate(Routes.LISTAS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.LISTAS) {
            PantallaGeneralScreen(
                onAbrirLista = { id -> navController.navigate(Routes.dentroLista(id)) },
                onTabChange = ::goTab
            )
        }
        composable(
            Routes.DENTRO_LISTA,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            DentroListaScreen(
                listaId = id,
                onBack = { navController.popBackStack() },
                onAnadirItem = { navController.navigate(Routes.addItemLista(id)) }
            )
        }
        composable(
            Routes.ADD_ITEM_LISTA,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            AddItemScreen(listaId = id, onDone = { navController.popBackStack() })
        }
        composable(Routes.FAVORITOS) {
            FavoritosScreen(
                onAdd = { navController.navigate(Routes.ADD_FAVORITO) },
                onTabChange = ::goTab
            )
        }
        composable(Routes.ADD_FAVORITO) {
            AddFavoritoScreen(onDone = { navController.popBackStack() })
        }
        composable(Routes.PERFIL) {
            PerfilScreen(
                onCambiarCorreo = { navController.navigate(Routes.CAMBIAR_CORREO) },
                onCambiarContrasena = { navController.navigate(Routes.CAMBIAR_CONTRASENA) },
                onAdmin = { navController.navigate(Routes.ADMIN) },
                onAmigos = { navController.navigate(Routes.AMIGOS) },
                onNotificaciones = { navController.navigate(Routes.NOTIFICACIONES) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabChange = ::goTab
            )
        }
        composable(Routes.AMIGOS) {
            AmigosScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.NOTIFICACIONES) {
            NotificacionesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.CAMBIAR_CORREO) {
            CambiarCorreoScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.CAMBIAR_CONTRASENA) {
            CambiarContrasenaScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ADMIN) {
            AdminScreen(onBack = { navController.popBackStack() })
        }
    }
}


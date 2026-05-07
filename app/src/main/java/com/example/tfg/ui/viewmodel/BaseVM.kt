package com.example.tfg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tfg.BuyNotesApp
import com.example.tfg.data.repository.Repository
import com.example.tfg.data.session.SessionManager

abstract class BaseVM : ViewModel() {
    protected val repo: Repository = BuyNotesApp.instance.repository
    protected val session: SessionManager = BuyNotesApp.instance.sessionManager
}

inline fun <reified VM : ViewModel> simpleFactory(crossinline create: () -> VM) =
    viewModelFactory { initializer { create() } }

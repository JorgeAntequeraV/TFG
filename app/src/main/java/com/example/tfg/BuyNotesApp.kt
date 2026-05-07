package com.example.tfg

import android.app.Application
import com.example.tfg.data.api.ApiService
import com.example.tfg.data.api.NetworkModule
import com.example.tfg.data.repository.Repository
import com.example.tfg.data.session.SessionManager

class BuyNotesApp : Application() {
    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiService
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionManager = SessionManager(applicationContext)
        apiService = NetworkModule.provideApiService(sessionManager)
        repository = Repository(apiService, sessionManager)
    }

    companion object {
        lateinit var instance: BuyNotesApp
            private set
    }
}

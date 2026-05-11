package com.example.tfg.ui.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecaptchaController internal constructor() {
    internal var webView: WebView? = null

    fun execute() {
        webView?.evaluateJavascript("executeRecaptcha()", null)
    }
}

@Composable
fun rememberRecaptchaController(): RecaptchaController = remember { RecaptchaController() }


@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun RecaptchaWebView(
    controller: RecaptchaController,
    onTokenReceived: (String) -> Unit,
    onError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val bridge = remember {
        object {
            @JavascriptInterface
            fun onCaptchaSuccess(token: String) {
                Log.d("Recaptcha", "Token recibido (len=${token.length})")
                scope.launch { withContext(Dispatchers.Main) { onTokenReceived(token) } }
            }

            @JavascriptInterface
            fun onCaptchaError() {
                Log.e("Recaptcha", "Callback de error desde JS")
                scope.launch { withContext(Dispatchers.Main) { onError() } }
            }
        }
    }

    val html = remember {
        try {
            context.assets.open("recaptcha.html").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e("Recaptcha", "No se pudo leer recaptcha.html", e)
            ""
        }
    }

    Box(modifier = modifier.size(1.dp)) {
        AndroidView(factory = { ctx ->
            WebView(ctx).apply {
                setBackgroundColor(Color.TRANSPARENT)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                webViewClient = WebViewClient()
                // Logs JS para depurar problemas de reCAPTCHA
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
                        Log.d("Recaptcha-JS",
                            "[${msg.messageLevel()}] ${msg.message()} @ ${msg.sourceId()}:${msg.lineNumber()}")
                        return true
                    }
                }
                addJavascriptInterface(bridge, "AndroidBridge")

                loadDataWithBaseURL(
                    "https://localhost/",
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
                controller.webView = this
            }
        })
    }
}

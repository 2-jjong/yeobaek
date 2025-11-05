// PostcodeJibunWebView.kt
@file:Suppress("SetJavaScriptEnabled")

package com.ssafy.yeobaeg.presentation.home.cafeowner.register

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PostcodeJibunWebView(
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {}
                webChromeClient = WebChromeClient()

                addJavascriptInterface(
                    PostcodeJibunJsBridge(onSelected, onClose),
                    "AndroidBridge"
                )

                loadDataWithBaseURL(
                    "https://t1.daumcdn.net/", // 외부 스크립트 로드 base
                    html,
                    "text/html",
                    "utf-8",
                    null
                )
            }
        }
    )
}

@Keep
private class PostcodeJibunJsBridge(
    private val onSelectedAddress: (String) -> Unit,
    private val onCloseWebView: () -> Unit
) {
    @JavascriptInterface
    fun onSelected(jibun: String) {
        onSelectedAddress(jibun)
    }

    @JavascriptInterface
    fun onClose() {
        onCloseWebView()
    }
}

/** Daum 우편번호 임베드 - 지번 주소만 Android로 전달 */
val html = """
        <html>
        <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <style>
          html, body { margin:0; padding:0; height:100%; }
          #wrap {
            width:100%;
            height:100dvh;
          }
        </style>
        <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
        <script>
          function resizeWrap() {
            var h = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
            var el = document.getElementById('wrap');
            if (el) el.style.height = h + 'px'; // fallback 보정
          }
        
          function execDaumPostcode() {
            new daum.Postcode({
              oncomplete: function(data) {
                var jibun = data.jibunAddress || data.autoJibunAddress || "";
                if (window.AndroidBridge && window.AndroidBridge.onSelected) {
                  window.AndroidBridge.onSelected(jibun);
                }
                if (window.AndroidBridge && window.AndroidBridge.onClose) {
                  window.AndroidBridge.onClose();
                }
              }
            }).embed(document.getElementById('wrap'));
          }
        
          window.addEventListener('load', function() {
            resizeWrap();
            execDaumPostcode();
          });
          window.addEventListener('resize', resizeWrap); // 회전/키보드 변화 대응
        </script>
        </head>
        <body>
          <div id="wrap"></div>
        </body>
        </html>

    """.trimIndent()
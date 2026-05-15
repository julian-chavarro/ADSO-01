package com.example.adso_01.ui;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adso_01.R;

public class ChatActivity extends AppCompatActivity {

    private WebView chatWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatWebView = findViewById(R.id.chatWebView);
        WebSettings ws = chatWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);

        chatWebView.setWebViewClient(new WebViewClient());

        chatWebView.addJavascriptInterface(new ChatInterface(), "AndroidInterface");

        // Carga el chat completo
        chatWebView.loadUrl("file:///android_asset/chatbot_burbuja_AdsoFitnes.html");
    }

    private class ChatInterface {
        @android.webkit.JavascriptInterface
        public void closeChat() {
            runOnUiThread(() -> finish()); // Cierra ChatActivity y vuelve al Lobby
        }
    }
}

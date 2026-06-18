package com.example.adso_01.ui;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adso_01.R;

/**
 * Actividad que embebe el asistente virtual ChatFit mediante WebView.
 * <p>
 * Carga un archivo HTML local ({@code chatbot_burbuja_AdsoFitnes.html})
 * alojado en {@code assets/} que contiene la interfaz completa del chatbot.
 * La comunicación entre JavaScript y Android se realiza mediante
 * {@link android.webkit.JavascriptInterface}, permitiendo que el botón
 * de cierre dentro del HTML finalice esta actividad.
 * </p>
 *
 * <h3>Configuración del WebView:</h3>
 * <ul>
 *   <li>JavaScript habilitado.</li>
 *   <li>DOM storage habilitado.</li>
 *   <li>Acceso a archivos locales permitido.</li>
 *   <li>Interfaz JavaScript "AndroidInterface" para cerrar desde el HTML.</li>
 * </ul>
 */
public class ChatActivity extends AppCompatActivity {

    /** WebView que renderiza el chatbot HTML. */
    private WebView chatWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatWebView = findViewById(R.id.chatWebView);

        // Configurar WebView para funcionalidad completa
        WebSettings ws = chatWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);

        // Evitar que los enlaces se abran en el navegador externo
        chatWebView.setWebViewClient(new WebViewClient());

        // Registrar interfaz de puente JS → Android
        chatWebView.addJavascriptInterface(new ChatInterface(), "AndroidInterface");

        // Cargar interfaz del chatbot desde assets
        chatWebView.loadUrl("file:///android_asset/chatbot_burbuja_AdsoFitnes.html");
    }

    /**
     * Interfaz JavaScript → Android para cerrar el ChatActivity.
     * <p>
     * Expone el método {@code closeChat()} al JavaScript del WebView
     * bajo el nombre "AndroidInterface". Cuando el HTML llama a
     * {@code AndroidInterface.closeChat()}, se finaliza esta Activity.
     * </p>
     */
    private class ChatInterface {

        /**
         * Cierra el ChatActivity y vuelve al Lobby.
         * Este método es llamado desde JavaScript en el WebView.
         */
        @JavascriptInterface
        public void closeChat() {
            runOnUiThread(() -> finish());
        }
    }
}

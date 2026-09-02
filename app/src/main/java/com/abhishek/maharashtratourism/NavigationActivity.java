package com.abhishek.maharashtratourism;

import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {

    private EditText etSource, etDestination;
    private WebView webViewMap;
    String destName;
    double destLat, destLng;
    private Button btnFindRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        etSource=findViewById(R.id.etSource);
        etDestination=findViewById(R.id.etDestination);
        btnFindRoute = findViewById(R.id.btnFindRoute);
        webViewMap = findViewById(R.id.webViewMap);

        // Configure WebView settings
        WebSettings webSettings = webViewMap.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // FIX: Force Desktop User-Agent so Google Maps stays inside the WebView
        // instead of throwing ERR_UNKNOWN_URL_SCHEME via mobile app redirects
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        webViewMap.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false; // Keep normal HTTP/HTTPS links inside WebView
                }
                // Intercept and safely handle any unexpected custom schemes
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        if (fallbackUrl != null) {
                            view.loadUrl(fallbackUrl);
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        Intent intent=getIntent();
        if(intent != null){
            destName=intent.getStringExtra("destName");
            destLat=intent.getDoubleExtra("destLat", 0.0);
            destLng=intent.getDoubleExtra("destLng", 0.0);

            etSource.setText("Current Location");
            etDestination.setText(destName);

            // Load initial directions using coordinates
            loadMapRoute("Current Location", destLat + "," + destLng);
        }

        btnFindRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String source=etSource.getText().toString().trim();
                String destination=etDestination.getText().toString().trim();

                if (source.isEmpty()) source = "Current Location";
                if (destination.isEmpty()) destination = destName;

                loadMapRoute(source, destination);
            }
        });

    }
    private void loadMapRoute(String source, String destination){
        // Construct Google Maps directions URL dynamically
        String url = "https://www.google.com/maps/dir/" + Uri.encode(source) + "/" + Uri.encode(destination);
        webViewMap.loadUrl(url);
    }
}
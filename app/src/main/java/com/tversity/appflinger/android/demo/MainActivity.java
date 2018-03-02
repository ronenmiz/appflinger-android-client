package com.tversity.appflinger.android.demo;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.tversity.appflinger.android.AppflingerClient;
import com.tversity.appflinger.android.AppflingerSession;

public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {

    private static final String TAG = "MainActivity";

    // The base URL of the Appflinger host server
    private final String APPFLINGER_HOST_BASE_URL = "http://192.168.1.182:8080";

    // We use a specific session id so that running this when a session already exists will just
    // reconnect to the same session - this is convenient when developing/testing
    private final String APPFLINGER_SESSION_ID = "android";

    // The web page which the cloud browser will open
    private final String WEB_PAGE = "https://youtube.com/tv";

    private AppflingerSession session;
    private TextureView mPlaybackView;
    private TextView mAttribView;
    private boolean isUIRendering = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        mPlaybackView = (TextureView) findViewById(R.id.PlaybackView);
        mAttribView =  (TextView)findViewById(R.id.AttribView);
        mPlaybackView.setSurfaceTextureListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (session != null) {
            session.stop();
            session = null;
            isUIRendering = false;
        }
    }

    private void createSession() {
        session = AppflingerClient.getInstance().startSession(APPFLINGER_HOST_BASE_URL,
                APPFLINGER_SESSION_ID, WEB_PAGE, new AppflingerSessionListener());
        if (session == null) {
            // TODO render something on the screen to notify end-user
            return;
        }

        if (mPlaybackView.isAvailable()) {
            mAttribView.setVisibility(View.VISIBLE);
            isUIRendering = true;
            session.startUIRendering(new Surface(mPlaybackView.getSurfaceTexture()));
        } else {
            // We will start ui rendering later when onSurfaceTextureAvailable is invoked
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createSession();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent key) {
        if (session != null) {
            session.sendKeyEvent(key);
        }
        return true;
    }

    // Appflinger session listener
    class AppflingerSessionListener implements AppflingerSession.Listener {
        public AppflingerSessionListener() {

        }

        public String onReceivedMessage(String message) {
            Log.i(TAG, "Received message: " + message);
            return "Got it";
        }

        public void onAddressBarChanged(String url) {
            Log.i(TAG, "onAddressBarChanged: " + url);
        }

        public void onPageClose() throws Exception {
            Log.i(TAG, "onPageClose");
        }

        public void onPageLoad() throws Exception {
            Log.i(TAG, "onPageLoad");
        }

        public void onTitleChanged(String title) {
            Log.i(TAG, "onAddressBarChanged: " + title);
        }
    }

    // Surface Texture Listener

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (session != null && !isUIRendering) {
            mAttribView.setVisibility(View.VISIBLE);
            isUIRendering = true;
            session.startUIRendering(new Surface(mPlaybackView.getSurfaceTexture()));
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (session != null && isUIRendering) {
            isUIRendering = false;
            session.stopUIRendering();
        }
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}

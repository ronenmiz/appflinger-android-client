package com.tversity.appflinger.android.demo;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.tversity.appflinger.android.AppflingerClient;
import com.tversity.appflinger.android.AppflingerSession;

public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {

    private static final String TAG = "MainActivity";

    private final String APPFLINGER_HOST_BASE_URL = "http://192.168.1.182:8080"; // 10.0.2.2:8080
    private final String WEB_PAGE = "https://youtube.com/tv";

    private AppflingerSession session;
    private TextureView mPlaybackView;
    private TextView mAttribView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        mPlaybackView = (TextureView) findViewById(R.id.PlaybackView);
        mAttribView =  (TextView)findViewById(R.id.AttribView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (session != null) {
            session.stop();
            session = null;
        }
    }

    private void createSession() {
        session = AppflingerClient.getInstance().startSession(
                APPFLINGER_HOST_BASE_URL, WEB_PAGE);
        if (session == null) {
            // TODO render something on the screen to notify end-user
            return;
        }

        if (mPlaybackView.isAvailable()) {
            mAttribView.setVisibility(View.VISIBLE);
            session.startUIRendering(new Surface(mPlaybackView.getSurfaceTexture()));
        } else {
            // We will start ui rendering later when onSurfaceTextureAvailable is invoked
            mPlaybackView.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createSession();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    // Surface Texture Listener

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mAttribView.setVisibility(View.VISIBLE);
        session.startUIRendering(new Surface(mPlaybackView.getSurfaceTexture()));
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // TODO release relevant resources
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}

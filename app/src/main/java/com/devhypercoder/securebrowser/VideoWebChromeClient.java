package com.devhypercoder.securebrowser;

import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class VideoWebChromeClient
        extends WebChromeClient implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private View activityNonVideoView;
    private ViewGroup activityVideoView;
    private View loadingView;
    private WebView webView;

    private boolean isVideoFullscreen; // Indicates if the video is being displayed using a custom view (typically full-screen)
    private FrameLayout videoViewContainer;
    private CustomViewCallback videoViewCallback;


    /**
     * Builds a video enabled WebChromeClient.
     *
     * @param activityNonVideoView A View in the activity's layout that contains every other view that should be hidden when the video goes full-screen.
     * @param activityVideoView    A ViewGroup in the activity's layout that will display the video. Typically you would like this to fill the whole layout.
     */
    public VideoWebChromeClient(View activityNonVideoView, ViewGroup activityVideoView) {
        this.activityNonVideoView = activityNonVideoView;
        this.activityVideoView = activityVideoView;
        this.loadingView = null;
        this.webView = null;
        this.isVideoFullscreen = false;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (!(view instanceof FrameLayout)) {
            return;
        }

        // A video wants to be shown
        FrameLayout frameLayout = (FrameLayout) view;
        View focusedChild = frameLayout.getFocusedChild();

        // Save video related variables
        this.isVideoFullscreen = true;
        this.videoViewContainer = frameLayout;
        this.videoViewCallback = callback;

        // Hide the non-video view, add the video view, and show it
        activityNonVideoView.setVisibility(View.INVISIBLE);
        activityVideoView.addView(videoViewContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        activityVideoView.setVisibility(View.VISIBLE);

        if (focusedChild instanceof android.widget.VideoView) {
            // android.widget.VideoView (typically API level <11)
            android.widget.VideoView videoView = (android.widget.VideoView) focusedChild;

            // Handle all the required events
            videoView.setOnPreparedListener(this);
            videoView.setOnCompletionListener(this);
            videoView.setOnErrorListener(this);
        } else {
            // Other classes, including:
            // - android.webkit.HTML5VideoFullScreen$VideoSurfaceView, which inherits from android.view.SurfaceView (typically API level 11-18)
            // - android.webkit.HTML5VideoFullScreen$VideoTextureView, which inherits from android.view.TextureView (typically API level 11-18)
            // - com.android.org.chromium.content.browser.ContentVideoView$VideoSurfaceView, which inherits from android.view.SurfaceView (typically API level 19+)

            // Handle HTML5 video ended event only if the class is a SurfaceView
            // Test case: TextureView of Sony Xperia T API level 16 doesn't work fullscreen when loading the javascript below
            if (webView != null && webView.getSettings().getJavaScriptEnabled() && focusedChild != null) {
                // Run javascript code that detects the video end and notifies the Javascript interface
                String js = "javascript:";
                js += "var _ytrp_html5_video_last;";
                js += "var _ytrp_html5_video = document.getElementsByTagName('video')[0];";
                js += "if (_ytrp_html5_video != undefined && _ytrp_html5_video != _ytrp_html5_video_last) {";
                {
                    js += "_ytrp_html5_video_last = _ytrp_html5_video;";
                    js += "function _ytrp_html5_video_ended() {";
                    {
                        js += "_VideoEnabledWebView.notifyVideoEnd();"; // Must match Javascript interface name and method of VideoEnableWebView
                    }
                    js += "}";
                    js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);";
                }
                js += "}";
                webView.loadUrl(js);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) // Available in API level 14+, deprecated in API level 18+
    {
        onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        // This method should be manually called on video end in all cases because it's not always called automatically.
        // This method must be manually called on back key press (from this class' onBackPressed() method).

        if (!isVideoFullscreen) {
            return;
        }

        // Hide the video view, remove it, and show the non-video view
        activityVideoView.setVisibility(View.INVISIBLE);
        activityVideoView.removeView(videoViewContainer);
        activityNonVideoView.setVisibility(View.VISIBLE);

        // Call back (only in API level <19, because in API level 19+ with chromium webview it crashes)
        if (videoViewCallback != null && !videoViewCallback.getClass().getName().contains(".chromium.")) {
            videoViewCallback.onCustomViewHidden();
        }

        // Reset video related variables
        isVideoFullscreen = false;
        videoViewContainer = null;
        videoViewCallback = null;

    }

    @Override
    public View getVideoLoadingProgressView() // Video will start loading
    {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
            return loadingView;
        } else {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) // Video will start playing, only called in the case of android.widget.VideoView (typically API level <11)
    {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) // Video finished playing, only called in the case of android.widget.VideoView (typically API level <11)
    {
        onHideCustomView();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) // Error while playing video, only called in the case of android.widget.VideoView (typically API level <11)
    {
        return false; // By returning false, onCompletion() will be called
    }
}



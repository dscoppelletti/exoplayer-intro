/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.exoplayer;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/* DOC STEP 6 - Listening for events
ExoPlayer is doing a lot of work for us behind the scenes, including:

. memory allocation
. downloading container files
. extracting metadata from the container
. decoding data
. rendering video, audio and text to the screen and loudspeakers

Sometimes it's useful to know what ExoPlayer is doing at run-time in order to
understand and improve the playback experience for our users.

For example, we might want to reflect playback state changes in the user
interface by:

. displaying a loading spinner when the player goes into buffering state
. showing an overlay with "watch next" options when the track has ended
DOC END STEP 6 */

/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  private PlayerView playerView;
  private SimpleExoPlayer player;
  private boolean playWhenReady = true;
  private int currentWindow = 0;
  private long playbackPosition = 0;

  // TODO STEP 6.1.1 - Listen up!
  private PlaybackStateListener playbackStateListener;
  // TODO END STEP 6.1.1

  // TODO STEP 6.1.2 - Listen up!
  private static final String TAG = PlayerActivity.class.getName();
  // TODO END STEP 6.1.2

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    playerView = findViewById(R.id.video_view);

    // TODO STEP 6.1.3 - Listen up!
    playbackStateListener = new PlaybackStateListener();
    // TODO END STEP 6.1.3
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    hideSystemUi();
    if ((Util.SDK_INT <= 23 || player == null)) {
      initializePlayer();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }

  private void initializePlayer() {
    if (player == null) {
      DefaultTrackSelector trackSelector = new DefaultTrackSelector();
      trackSelector.setParameters(
              trackSelector.buildUponParameters().setMaxVideoSizeSd());
      player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
    }

    playerView.setPlayer(player);
    Uri uri = Uri.parse(getString(R.string.media_url_dash));
    MediaSource mediaSource = buildMediaSource(uri);

    player.setPlayWhenReady(playWhenReady);
    player.seekTo(currentWindow, playbackPosition);

    // TODO STEP 6.2.1 - Register our listener
    player.addListener(playbackStateListener);
    // TODO END STEP 6.2.1

    player.prepare(mediaSource, false, false);
  }

  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      playWhenReady = player.getPlayWhenReady();

      // TODO STEP 6.2.2 - Register our listener
      player.removeListener(playbackStateListener);
      // TODO END STEP 6.2.2

      player.release();
      player = null;
    }
  }

  private MediaSource buildMediaSource(Uri uri) {
    DataSource.Factory dataSourceFactory =
      new DefaultDataSourceFactory(this, "exoplayer-codelab");
    DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
    return mediaSourceFactory.createMediaSource(uri);
  }

  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

  /* DOC STEP 6.1.6 - Listen up!
  onPlayerStateChanged is called when:

  . play/pause state changes, given by the playWhenReady parameter
  . playback state changes, given by the playbackState parameter

  The player can be in one of the following 4 states:

  - ExoPlayer.STATE_IDLE
  The player has been instantiated but has not yet been prepared with a
  MediaSource.

  - ExoPlayer.STATE_BUFFERING
  The player is not able to play from the current position because not enough
  data has been buffered.

  - ExoPlayer.STATE_READY
  The player is able to immediately play from the current position. This means
  the player will start playing media automatically if playWhenReady is true. If
  it is false the player is paused.

  - ExoPlayer.STATE_ENDED
  The player has finished playing the media.

  How do you know if your player is actually playing media? Well, all of the
  following conditions must be met:

  . playback state is STATE_READY
  . playWhenReady is true
  . playback is not suppressed for some other reason (e.g. loss of audio focus)

  Luckily, ExoPlayer provides a convenience method ExoPlayer.isPlaying for
  exactly this purpose! Or, if you want to be kept informed when isPlaying
  changes, you can listen for onIsPlayingChanged.
  DOC END STEP 6.1.6 */

  // TODO STEP 6.1.4/5 - Listen up!
  private class PlaybackStateListener implements Player.EventListener {

    // TODO STEP 6.1.6 - Listen up!
    @Override
    public void onPlayerStateChanged(boolean playWhenReady,
            int playbackState) {
      String stateString;
      switch (playbackState) {
        case ExoPlayer.STATE_IDLE:
          stateString = "ExoPlayer.STATE_IDLE      -";
          break;
        case ExoPlayer.STATE_BUFFERING:
          stateString = "ExoPlayer.STATE_BUFFERING -";
          break;
        case ExoPlayer.STATE_READY:
          stateString = "ExoPlayer.STATE_READY     -";
          break;
        case ExoPlayer.STATE_ENDED:
          stateString = "ExoPlayer.STATE_ENDED     -";
          break;
        default:
          stateString = "UNKNOWN_STATE             -";
          break;
      }
      Log.d(TAG, "changed state to " + stateString
              + " playWhenReady: " + playWhenReady);
    }
    // TODO END STEP 6.1.6
  }
  // TODO END STEP 6.1.4/5
}

/* DOC STEP 6.3 - Going deeper
ExoPlayer offers a number of other listeners which are useful in understanding
the user's playback experience. There are listeners for audio and video, as well
as an AnalyticsListener which contains the callbacks from all the listeners.
Some of the most important methods are:

- onRenderedFirstFrame
is called when the first frame of a video is rendered. With this you can
calculate how long the user had to wait to see meaningful content on the screen.

- onDroppedVideoFrames
is called when video frames have been dropped. Dropped frames indicate that
playback is janky and the user experience is likely to be poor.

- onAudioUnderrun
is called when there has been an audio underrun. Underruns cause audible
glitches in the sound and are more noticeable than dropped video frames.
DOC END STEP 6.3 */

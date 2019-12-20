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

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  // TODO STEP 3.2.2a - Add the PlayerView
  private PlayerView playerView;
  // TODO END STEP 3.2.2a

  // TODO STEP 3.3a - Create an ExoPlayer
  private SimpleExoPlayer player;
  // TODO END STEP 3.3a

  // TODO STEP 3.5.6a - Playing nice with the Activity lifecycle
  private boolean playWhenReady = true;
  private int currentWindow = 0;
  private long playbackPosition = 0;
  // TODO END STEP 3.5.6a

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    // TODO STEP 3.2.2b - Add the PlayerView
    playerView = findViewById(R.id.video_view);
    // TODO END STEP 3.2.2b
  }

  /* DOC STEP 3.3 - Create an ExoPlayer
  To play streaming media we need an ExoPlayer object. The simplest way of
  creating one is to call ExoPlayerFactory.newSimpleInstance which gives us a
  SimpleExoPlayer.
  SimpleExoPlayer is a convenient, all-purpose implementation of the ExoPlayer
  interface.

  1) We pass our context to newSimpleInstance which creates our SimpleExoPlayer
  object. This is then assigned to player.
  2) We use playerView.setPlayer to bind the player to its corresponding view.
  DOC END STEP 3.3 */

  // TODO STEP 3.3b - Create an ExoPlayer
  private void initializePlayer() {
    player = ExoPlayerFactory.newSimpleInstance(this);
    playerView.setPlayer(player);

    // TODO STEP 3.4b - Creating a media source
    // Replaced by 3.10
//    Uri uri = Uri.parse(getString(R.string.media_url_mp3));
//    MediaSource mediaSource = buildMediaSource(uri);
    // TODO END STEP 3.4b

    // TODO STEP 3.10 - Play video!
    Uri uri = Uri.parse(getString(R.string.media_url_mp4));
    MediaSource mediaSource = buildMediaSource(uri);
    // TODO END STEP 3.10

    // TODO STEP 3.7 - Final preparation
    player.setPlayWhenReady(playWhenReady);
    player.seekTo(currentWindow, playbackPosition);
    player.prepare(mediaSource, false, false);
    // TODO END STEP 3.7
  }
  // TODO END STEP 3.3b

  /* DOC STEP 3.4 - Creating a media source
  1) We create a DefaultDataSourceFactory, specifying our context and the
  user-agent string which will be used when making the HTTP request for the
  media file.
  2) We pass our newly created dataSourceFactory to
  ProgressiveMediaSource.Factory (a factory for creating progressive media data
  sources).
  3) We call createMediaSource, supplying our uri, to obtain a MediaSource
  object.

  Multimedia data is usually stored using a container format, such as MP4 or
  Ogg. Before the video and/or audio data can be played it must be extracted
  from the container. ExoPlayer is capable of extracting data from many
  different container formats using a variety of Extractor classes.
  DOC END STEP 3.4 */

  // TODO STEP 3.4a - Creating a media source
  private MediaSource buildMediaSource(Uri uri) {
    DataSource.Factory dataSourceFactory =
            new DefaultDataSourceFactory(this, "exoplayer-codelab");
    return new ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri);
  }
  // TODO END STEP 3.4a

  /* DOC STEP 3.5 - Playing nice with the Activity lifecycle
  Our player can hog a lot of resources including memory, CPU, network
  connections and hardware codecs. Many of these resources are in short supply,
  particularly for hardware codecs where there may only be one. It's important
  that we release those resources for other apps to use when we're not using
  them, such as when our app is put into the background.
  Put another way, our player's lifecycle should be tied to the lifecycle of our
  app.
  DOC END STEP 3.5 */

  /* DOC STEP 3.5.3 - Playing nice with the Activity lifecycle
  Starting with API level 24 Android supports multiple windows. As our app can
  be visible but not active in split window mode, we need to initialize the
  player in onStart. Before API level 24 we wait as long as possible until we
  grab resources, so we wait until onResume before initializing the player.
  DOC END STEP 3.5.3 */

  // TODO STEP 3.5.3 - Playing nice with the Activity lifecycle
  @Override
  protected void onStart() {
    super.onStart();
    if (Util.SDK_INT >= 24) {
      initializePlayer();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    hideSystemUi();
    if ((Util.SDK_INT < 24 || player == null)) {
      initializePlayer();
    }
  }
  // TODO END STEP 3.5.3

  /* DOC STEP 3.5.4 - Playing nice with the Activity lifecycle
  hideSystemUi is a helper method called in onResume which allows us to have a
  full screen experience.
  DOC END STEP 3.5.4 */

  // TODO STEP 3.5.4 - Playing nice with the Activity lifecycle
  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }
  // TODO END STEP 3.5.4

  /* DOC STEP 3.5.5 - Playing nice with the Activity lifecycle
  Before API Level 24 there is no guarantee of onStop being called. So we have
  to release the player as early as possible in onPause. Starting with API Level
  24 (which brought multi and split window mode) onStop is guaranteed to be
  called. In the paused state our activity is still visible so we wait to
  release the player until onStop.
  DOC END STEP 3.5.5 */

  // TODO STEP 3.5.5 - Playing nice with the Activity lifecycle
  @Override
  protected void onPause() {
    if (Util.SDK_INT < 24) {
      releasePlayer();
    }
    super.onPause();
  }

  @Override
  protected void onStop() {
    if (Util.SDK_INT >= 24) {
      releasePlayer();
    }
    super.onStop();
  }
  // TODO STEP END 3.5.5

  /* DOC STEP 3.5.6
  Before we release and destroy the player we store the following information:

  . play/pause state using getPlayWhenReady
  . current playback position using getCurrentPosition
  . current window index using getCurrentWindowIndex.

  This will allow us to resume playback from where the user left off. All we
  need to do is supply this state information when we initialize our player.
  DOC END STEP 3.5.6 */

  // TODO STEP 3.5.6b - Playing nice with the Activity lifecycle
  private void releasePlayer() {
    if (player != null) {
      playWhenReady = player.getPlayWhenReady();
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      player.release();
      player = null;
    }
  }
  // TODO STEP 3.5.6b

  /* DOC STEP 3.7 - Final preparation
  All we need to do now is to supply the state information we saved in
  releasePlayer to our player during initialization.

  - setPlayWhenReady
  tells the player whether to start playing as soon as all resources for
  playback have been acquired. Since playWhenReady is initially true, playback
  will start automatically the first time the app is run.

  - seekTo
  tells the player to seek to a certain position within a specific window. Both
  currentWindow and playbackPosition were initialized to zero so that playback
  starts from the very start the first time the app is run.

  - prepare
  tells the player to acquire all the resources for the given mediaSource, and
  additionally tells it not to reset the position or state, since we already set
  these in the two previous lines.
  DOC END STEP 3.7 */
}

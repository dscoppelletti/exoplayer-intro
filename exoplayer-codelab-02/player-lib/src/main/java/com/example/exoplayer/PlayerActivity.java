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

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/* DOC STEP 5 - Adaptive streaming
Adaptive streaming is a technique for streaming media by varying the quality of
the stream based on the available network bandwidth. This allows the user to
experience the best quality media that their bandwidth will allow.

Typically, the same media content is split into multiple tracks with different
qualities (bit rates and resolutions). The player chooses a track based on the
available network bandwidth.

Each track is split into chunks of a given duration, typically between 2 and 10
seconds. This allows the player to quickly switch between tracks as available
bandwidth changes. The player is responsible for stitching these chunks together
for seamless playback.
DOC END STEP 5 */

/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  private PlayerView playerView;
  private SimpleExoPlayer player;
  private boolean playWhenReady = true;
  private int currentWindow = 0;
  private long playbackPosition = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    playerView = findViewById(R.id.video_view);
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

  /* DOC STEP 5.1 - Adaptive track selection
  At the heart of adaptive streaming is selecting the most appropriate track for
  the current environment.

  1) We create a DefaultTrackSelector which is responsible for choosing tracks
  in the media source.
  2) We tell our trackSelector to only pick tracks of standard definition or
  lower - a good way of saving our user's data at the expense of quality.
  3) We use our trackSelector to create a SimpleExoPlayer instance.
  DOC END STEP 5.1 */

  private void initializePlayer() {
    // TODO STEP 5.1 - Adaptive track selection
    if (player == null) {
      DefaultTrackSelector trackSelector = new DefaultTrackSelector();
      trackSelector.setParameters(
              trackSelector.buildUponParameters().setMaxVideoSizeSd());
      player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
    }
//    player = ExoPlayerFactory.newSimpleInstance(this);
    // TODO END STEP 5.1
    playerView.setPlayer(player);

    // TODO STEP 5.2.2 - Building an adaptive MediaSource
    Uri uri = Uri.parse(getString(R.string.media_url_dash));
//    Uri uri = Uri.parse(getString(R.string.media_url_mp4));
    // TODO STEP 5.2.2
    MediaSource mediaSource = buildMediaSource(uri);

    player.setPlayWhenReady(playWhenReady);
    player.seekTo(currentWindow, playbackPosition);
    player.prepare(mediaSource, false, false);
  }

  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      playWhenReady = player.getPlayWhenReady();
      player.release();
      player = null;
    }
  }

  /* DOC STEP 5.2 - Building an adaptive MediaSource
  DASH is a widely used adaptive streaming format. To stream DASH content we
  need to create a DashMediaSource.
  DOC END STEP 5.2 */

  private MediaSource buildMediaSource(Uri uri) {
    // TODO STEP 5.2.1 - Building an adaptive MediaSource
    DataSource.Factory dataSourceFactory =
            new DefaultDataSourceFactory(this, "exoplayer-codelab");
    DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
    return mediaSourceFactory.createMediaSource(uri);
    // TODO END STEP 5.2.1
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
}

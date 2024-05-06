package com.ertaqy.recorder.di

import android.content.Context
import cafe.adriel.voyager.core.screen.Screen
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import com.ertaqy.recorder.base.service.RecorderService
import com.ertaqy.recorder.features.uploading.RecordingViewModel
import com.ertaqy.recorder.features.uploading.UploadingScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAndroidAudioRecorder( context: Context) : AndroidAudioRecorder
    = AndroidAudioRecorder(context )

    @Provides
    @Singleton
    fun provideAndroidAudioPlayer(context: Context) : AndroidAudioPlayer
    = AndroidAudioPlayer(context)


}
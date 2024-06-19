package com.ertaqy.recorder.di

import android.content.Context
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
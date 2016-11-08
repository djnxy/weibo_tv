package com.example.xinyu10.newir;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.IOException;

/**
 * Created by xinyu10 on 2016/10/24.
 */
public class VoiceRecorder {

    static private MediaRecorder voiceRecorder = null;
    static private MediaPlayer player = null;

    static public void init(){
        if(voiceRecorder == null){
            voiceRecorder = new MediaRecorder();
        }
        if(player == null){
            player = new MediaPlayer();
        }
    }

    static public void start(String fileName){
        voiceRecorder.reset();
        voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        voiceRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);
        try {
            voiceRecorder.prepare();
            voiceRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void stop(){
        try {
            voiceRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    static public void play(String fileName){
        try{
            player.reset();
            player.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);
            player.prepare();
            player.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

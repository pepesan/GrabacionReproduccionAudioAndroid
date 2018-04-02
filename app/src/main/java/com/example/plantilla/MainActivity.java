package com.example.plantilla;

import java.io.File;
import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button graba;
	private Button para;
	private Button reproduce;
	MediaPlayer mp;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupViews();
	}
	private void setupViews() {
		graba=(Button)findViewById(R.id.record);
		reproduce=(Button)findViewById(R.id.play);
		para=(Button)findViewById(R.id.para);
		if(mp==null){
			mp = new MediaPlayer();
		}
		
	}
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
	    @Override
	    public void onError(MediaRecorder mr, int what, int extra) {
	    	graba.setEnabled(true);
	    	para.setEnabled(false);
	        Toast.makeText(MainActivity.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
	    @Override
	    public void onInfo(MediaRecorder mr, int what, int extra) {
	        Toast.makeText(MainActivity.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};
	private String lastFileName;
	public void recordAudio(View v){

		recorder = new MediaRecorder();
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    //grabacion en mp4
	    currentFormat=0;
	    recorder.setOutputFormat(output_formats[currentFormat]);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    lastFileName=getFilename();
	    recorder.setOutputFile(lastFileName);
	    recorder.setOnErrorListener(errorListener);
	    recorder.setOnInfoListener(infoListener);
	    try {
	        recorder.prepare();
	        recorder.start();
	        graba.setEnabled(false);
	        para.setEnabled(true);
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	}
	private String getFilename() {
	    String filepath = Environment.getExternalStorageDirectory().getPath();
	    File file = new File(filepath, AUDIO_RECORDER_FOLDER);
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
	}
	
	public void stopAudioRecording(View v) {
	    if (null != recorder) {
	        recorder.stop();
	        recorder.reset();
	        recorder.release();
	        recorder = null;
	        para.setEnabled(false);
	        graba.setEnabled(true);
	        reproduce.setEnabled(true);
	    }
	}
	private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

	public void requestAudioPermissions(View v) {
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED
				|| ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED
				) {

			//When permission is not granted by user, show them message why this permission is needed.
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.RECORD_AUDIO) || ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

				//Give user option to still opt-in the permissions
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_RECORD_AUDIO);

			} else {
				// Show user dialog to grant permission to record audio
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_RECORD_AUDIO);
			}
		}
		//If permission is granted, then go ahead recording audio
		else if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.RECORD_AUDIO)
				== PackageManager.PERMISSION_GRANTED) {

			//Go ahead with recording audio now
			Toast.makeText(this, "Permissions granted to record audio", Toast.LENGTH_LONG).show();
			recordAudio(null);
		}
	}
	//Handling callback
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_RECORD_AUDIO: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED
						&& grantResults[1] == PackageManager.PERMISSION_GRANTED
						) {
					// permission was granted, yay!
					recordAudio(null);
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
				}
				return;
			}
		}
	}
	public void playAudio(View v){


		if(!mp.isPlaying()){

			try {
				mp = new MediaPlayer();
				mp.setDataSource(lastFileName);
				mp.prepare();
				mp.setOnCompletionListener(new OnCompletionListener() {

		            @Override
		            public void onCompletion(MediaPlayer mp) {
		                reproduce.setText("Reproduce");
		            }

		            });
				mp.start();
				reproduce.setText("Pausa");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			mp.pause();
			reproduce.setText("Continua");
		}

	}
}

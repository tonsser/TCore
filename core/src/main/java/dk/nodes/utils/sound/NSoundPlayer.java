package dk.nodes.utils.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

import java.io.File;
import java.io.FileInputStream;

import dk.nodes.utils.NLog;

/**
 * @author Johnny Sørensen - 2013
 */

public class NSoundPlayer {

	private MediaPlayer mPlayer;
	private Context mContext;
	
	public NSoundPlayer( Context context ) {
		mPlayer = new MediaPlayer();
		mContext = context;
	}
	
	/**
	 * Useful for smaller audio files, use playFileAsyncFromAssets instead for larger files
	 * 
	 * @param file File from assets directory in the format "file.mp3"
	 */
	public void playFileFromAssets( String file ) {
		try {
			mPlayer.reset();
			AssetFileDescriptor afd = mContext.getAssets().openFd( file );
			mPlayer.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength() );
			afd.close();
			
			mPlayer.prepare();
			mPlayer.start();
		} catch( Exception e ) {
			NLog.e( "NMediaPlayerController", e.toString() );
		}
	}
	
	/**
	 * Useful for smaller audio files, use playFileAsyncFromAssets instead for larger files
	 * 
	 * @param file File from assets directory in the format "file.mp3"
	 * @param listener Callback when playing is done
	 */
	public void playFileFromAssets( String file, final SoundPlayerListener listener ) {
		mPlayer.setOnCompletionListener( new OnCompletionListener() {
			
			@Override
			public void onCompletion( MediaPlayer mp ) {
				listener.onComplete();
				mPlayer.setOnCompletionListener( null );
			}
		} );
		this.playFileFromAssets( file );
	}
	
	/**
	 * Useful when loading larger files where loading can block the UI thread for too long
	 * 
	 * @param file File from assets directory in the format "file.mp3"
	 */
	public void playFileAsyncFromAssets( String file ) {
		try {
			mPlayer.reset();
			AssetFileDescriptor afd = mContext.getAssets().openFd( file );
			mPlayer.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength() );
			afd.close();
			
			mPlayer.setOnPreparedListener( new OnPreparedListener() {
				@Override
				public void onPrepared( MediaPlayer mp ) {
					mp.start();
				}
			});
			
			mPlayer.prepareAsync();
		} catch( Exception e ) {
			NLog.e( "NMediaPlayerController", e.toString() );
		}
	}
	
	/**
	 * Useful for smaller audio files, use playFileAsyncFromAssets instead for larger files
	 * 
	 * @param file File from assets directory in the format "file.mp3"
	 * @param listener Callback when playing is done
	 */
	public void playFileAsyncFromAssets( String file, final SoundPlayerListener listener ) {
		mPlayer.setOnCompletionListener( new OnCompletionListener() {
			
			@Override
			public void onCompletion( MediaPlayer mp ) {
				listener.onComplete();
				mPlayer.setOnCompletionListener( null );
			}
		} );
		this.playFileAsyncFromAssets( file );
	}
	
	/**
	 * Useful for smaller audio files, use playFileAsyncFromAssets instead for larger files
	 * 
	 * @param file File object from external storage or internal storage
	 */
	public void playFile( File file ) {
		try {
			mPlayer.reset();
			FileInputStream in = new FileInputStream(file);
			mPlayer.setDataSource( in.getFD() );
			in.close();
			
			mPlayer.prepare();
			mPlayer.start();
		} catch( Exception e ) {
			NLog.e( "NMediaPlayerController", e.toString() );
		}
	}
	
	/**
	 * Useful when loading larger files where loading can block the UI thread for too long
	 * 
	 * @param file File object from external storage or internal storage
	 */
	public void playFileAsync( File file ) {
		try {
			mPlayer.reset();
			FileInputStream in = new FileInputStream(file);
			mPlayer.setDataSource( in.getFD() );
			in.close();
			
			mPlayer.setOnPreparedListener( new OnPreparedListener() {
				@Override
				public void onPrepared( MediaPlayer mp ) {
					mp.start();
				}
			});
			
			mPlayer.prepareAsync();
		} catch( Exception e ) {
			NLog.e( "NMediaPlayerController", e.toString() );
		}
	}
	
	public void stop() {
		mPlayer.stop();
	}
	
	public MediaPlayer getMediaPlayer() {
		return mPlayer;
	}
	
	public void setVolume( int currentVolume, int maxVolume ) {
		final float volume = (float) (1 - (Math.log(maxVolume - currentVolume) / Math.log(maxVolume)));
		mPlayer.setVolume(volume, volume);
	}
	
	public interface SoundPlayerListener {
		public void onComplete();
	}
	
	public static void setVolume( MediaPlayer mediaplayer, int currentVolume, int maxVolume ) {
		final float volume = (float) (1 - (Math.log(maxVolume - currentVolume) / Math.log(maxVolume)));
		mediaplayer.setVolume(volume, volume);
	}
}

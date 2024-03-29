package cn.cz.tetris.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Random;

import cn.cz.tetris.R;
import cn.cz.tetris.utils.DebugLog;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MusicService";

    public static final String STR_MUSIC_ID = "str_music_id";

    private static final String STR_COMMAND = "str_command";

    private static final int COMMAND_NONE = 0;
    private static final int COMMAND_START = 1;
    private static final int COMMAND_STOP = 2;
    private static final int COMMAND_RESUME = 3;
    private static final int COMMAND_PAUSE = 4;

    private MediaPlayer mMediaPlayer;
    private int[] mMusicResources;
    private int mIndex = 0;
    private boolean mStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.i(TAG, "MusicService created");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMusicResources = new int[] {
                R.raw.aigei_com,
                R.raw.kqs,
                R.raw.slj,
                R.raw.slfjxq,
                R.raw.hjzqd
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getIntExtra(STR_COMMAND, COMMAND_NONE)) {
                case COMMAND_START:
                    int index = intent.getIntExtra(STR_MUSIC_ID, 0);
                    if (mStarted && mIndex == index) {
                        break;
                    }

                    try {
                        if (mMediaPlayer.isPlaying() || mIndex != index) {
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();
                        }
                        mIndex = index;

                        int resourceId;
                        if (mIndex == 0) {
                            Random random = new Random();
                            resourceId = mMusicResources[random.nextInt(mMusicResources.length)];
                        } else {
                            resourceId = mMusicResources[mIndex - 1];
                        }
                        mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + resourceId));
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        mMediaPlayer.setLooping(mIndex != 0);
                        mStarted = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case COMMAND_STOP:
                    stopSelf();
                    break;
                case COMMAND_RESUME:
                    if (mStarted && !mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                    }
                    break;
                case COMMAND_PAUSE:
                    if (mStarted && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DebugLog.i(TAG, "MusicService destroyed");
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mStarted = false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mIndex == 0) {
            mMediaPlayer.reset();
            Random random = new Random();
            try {
                mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + mMusicResources[random.nextInt(mMusicResources.length)]));
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startMusic(Context context, int id) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_START);
        intent.putExtra(STR_MUSIC_ID, id);
        context.startService(intent);
    }

    public static void stopMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_STOP);
        context.startService(intent);
    }

    public static void resumeMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_RESUME);
        context.startService(intent);
    }

    public static void pauseMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_PAUSE);
        context.startService(intent);
    }
}

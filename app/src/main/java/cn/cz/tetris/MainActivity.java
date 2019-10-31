package cn.cz.tetris;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import cn.cz.tetris.game.GameEngine;
import cn.cz.tetris.game.IGameInterface;
import cn.cz.tetris.game.Piece;
import cn.cz.tetris.music.MusicService;
import cn.cz.tetris.renderer.GameRenderer;
import cn.cz.tetris.view.GameSurfaceView;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GameSurfaceView.ITouchInterface,
        IGameInterface {
    private static final String TAG = "MainActivity";

    private static final int REQ_SETTING = 1000;

    private GameSurfaceView mSurfaceView;
    private GameRenderer mRenderer;
    private GameEngine mGameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGameEngine = new GameEngine(this, this);
        initView(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSurfaceView.onResume();
        MusicService.resumeMusic(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameEngine.resumeGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameEngine.pauseGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSurfaceView.onPause();
        MusicService.pauseMusic(this);
    }

    @Override
    public void onBackPressed() {
        MusicService.stopMusic(this);
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SETTING && resultCode == RESULT_OK) {
            mGameEngine.readSetting();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game:
                startGame();
                break;
            case R.id.setting:
                startActivityForResult(new Intent(this, SettingActivity.class), REQ_SETTING);
                break;
        }
    }

    @Override
    public void onRotate() {
        mGameEngine.rotate();
    }

    @Override
    public void onDropDown() {
        mGameEngine.setFastMode();
    }

    @Override
    public void onTranslate(boolean isLeft) {
        mGameEngine.translate(isLeft);
    }

    @Override
    public void onFailed(int score) {

    }

    @Override
    public void onPieceChanged(Piece piece) {

    }

    @Override
    public void onScoreAdded(int score) {

    }

    private void initView(Bundle savedInstanceState) {
        findViewById(R.id.start_game).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);

        mSurfaceView = findViewById(R.id.gl_surface_view);
        mSurfaceView.setTouchInterface(this);
        mRenderer = new GameRenderer(this, mGameEngine);
        mSurfaceView.setRenderer(mRenderer);

        if (savedInstanceState == null) {
            MusicService.startMusic(this);
        } else {

        }
    }

    private void startGame() {
        findViewById(R.id.button_layout).setVisibility(View.GONE);
        findViewById(R.id.game_layout).setVisibility(View.VISIBLE);

        mGameEngine.startGame();
    }
}

package com.heshicaihao.popstars;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.heshicaihao.popstars.constant.MyConstant;
import com.heshicaihao.popstars.util.GameSoundPool;
import com.heshicaihao.popstars.util.Utils;
import com.heshicaihao.popstars.ui.CommomDialog;
import com.heshicaihao.popstars.ui.CustomDialog;
import com.heshicaihao.popstars.widget.FireworkView;
import com.heshicaihao.popstars.widget.MainView;
import com.heshicaihao.popstars.widget.StartView;

import java.util.HashMap;


public class MainActivity extends Activity {

    int n;
    int score;
    private static final int REQUEST_CODE = 0; // 请求码

    private GameSoundPool sounds;
    private MainView mainView;
    private StartView startView;
    private int view = 1;
    private boolean isResumeLive = false;
    private int currentGuanKa;
    private int preScore;
    boolean isResume = false;
    private Handler handler = new Handler() {
        @SuppressLint({"WrongConstant"})
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MyConstant.TO_MAIN_VIEW) {
                toMainView();
            } else if (msg.what == MyConstant.START_GAME) {
                startview();
            } else if (msg.what == MyConstant.RESUME_GAME) {
                toMainView();
                Toast.makeText(MainActivity.this, "rsume", Toast.LENGTH_SHORT).show();
            } else if (msg.what == MyConstant.END_GAME) {
                endGame();
            } else if (msg.what == MyConstant.BLOCK_BOMB_GAME) {
                if (mainView != null) {
                    mainView.updateBlockBomb();
                }
            } else if (msg.what == MyConstant.UPDATE_SHOW_SCORE) {
                if (mainView != null) {
                    //mainView.updateCurrentScore();
                }
            } else if (msg.what == MyConstant.UPDATE_NEXT) {
                if (mainView != null) {
                    mainView.updateNext();
                }
            } else if (msg.what == MyConstant.UPDATE_FIREWORK) {
                if (mainView != null) {
                    //mainView.updateScore();
                }
            } else if (msg.what == MyConstant.UPDATE_BOMB) {
                if (mainView != null) {
                    mainView.updateBlock();
                }
            } else if (msg.what == MyConstant.SHOW_DIEDIALOG) {
                Toast.makeText(MainActivity.this, "闯关失败再来一局", Toast.LENGTH_SHORT).show();
//				showDieDialog();
                toMainView();
            } else if (msg.what == MyConstant.WELCOME_SOUND) {
                sounds.playSound(8, 0);
            }
        }
    };

    //点击两次
    final long DOUBLE_CLICK = 1500;
    long startTime;
    boolean isClick = false;
    boolean pause = false;

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (view == 1) {
            long endTime = System.currentTimeMillis();
            if (!isClick) {
                isClick = true;
                startTime = System.currentTimeMillis();
                Toast.makeText(this, this.getString(R.string.on_back_exit), Toast.LENGTH_SHORT).show();
            } else {
                Log.d("zxc11", "onBackPressed isClick = " + isClick);
                isClick = false;
                if (endTime - startTime <= DOUBLE_CLICK) {
                    endGame();
                } else {
                    isClick = true;
                    startTime = System.currentTimeMillis();
                    Toast.makeText(this, this.getString(R.string.on_back_exit), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            } else if (mainView != null && mainView.isDie()) {
                startview();
            } else {
                showAlertDialog();
            }

        }
        return;
    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(this.getResources().getString(R.string.message_hint));
        builder.setTitle(this.getResources().getString(R.string.hint));
        builder.setPositiveButton(this.getResources().getString(R.string.exit_save_game), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mainView.exitAndSave();
                startview();
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton(this.getResources().getString(R.string.exit_game),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        startview();
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    CustomDialog dialog;
    CustomDialog.Builder builder = null;

    public void showIntrduceDialog() {
        if (builder == null)
            builder = new CustomDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.introduce_game));

        builder.setFeedBack(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d("zxc224", "setFeedBack");
            }
        });

        builder.setcheckEdition(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d("zxc224", "setcheckEdition");
            }
        });
        dialog = builder.create(R.layout.dialog_about, 1);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        //window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        dialog.show();
    }

    public boolean dieDialogIsShow() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                return true;
            }
        }
        return false;
    }

    int singleClick;
    int voiceClick;
    CommomDialog.Builder builderMenu = null;

    public void showMenuDialog() {
        if (builderMenu == null)
            builderMenu = new CommomDialog.Builder(this);
        builderMenu.setTitle(this.getResources().getString(R.string.introduce_title));
        builderMenu.setResumeButton(this.getResources().getString(R.string.continue_click), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        singleClick = Utils.getKey(this, MyConstant.SINGLEDOUBLEKEY);
        builderMenu.setDoubleButton(this.getResources().getString(singleClick == 0 ? R.string.single_click : R.string.double_click), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                singleClick = singleClick == 0 ? 1 : 0;
                builderMenu.setDoubleButtonTitle(MainActivity.this.getResources().getString(singleClick == 0 ? R.string.single_click : R.string.double_click));
                Utils.saveKey(MainActivity.this, MyConstant.SINGLEDOUBLEKEY, singleClick);
                builderMenu.setDoubleDrable(singleClick == 0 ? R.mipmap.double_click1 : R.mipmap.double_click2);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("doubleClice", "" + singleClick);
                if (singleClick == 0) {
                    if (mainView != null) {
                        mainView.clearDoubleClickLabel();
                    }
                }
            }
        }, singleClick == 0 ? R.mipmap.double_click1 : R.mipmap.double_click2);

        voiceClick = Utils.getKeyDefault(this, MyConstant.VOICEKEY);
        builderMenu.setVoiceButton(this.getResources().getString(R.string.voice), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                voiceClick = voiceClick == 0 ? 1 : 0;
                Utils.saveKey(MainActivity.this, MyConstant.VOICEKEY, voiceClick);
                builderMenu.setVoiceDrable(voiceClick == 0 ? R.mipmap.close_voice : R.mipmap.open_voice);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("voiceClice", "" + voiceClick);
            }
        }, voiceClick == 0 ? R.mipmap.close_voice : R.mipmap.open_voice);

        dialog = builderMenu.create(R.layout.dialog_commom);
        dialog.show();
    }


    boolean firstInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 分数计算公式
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sounds = new GameSoundPool(this);
        sounds.initGameSound();
//        PermissionsUtil.requestPermission(this);

        handler.postDelayed(runnable, 1000);

        if (view == 1) {
            startview();
        }
        firstInit = true;
        Message msg = new Message();
        msg.what = MyConstant.WELCOME_SOUND;
        handler.sendMessageDelayed(msg, 1000);

    }

    FireworkView fireworkView;

    public void startview() {
        isClick = false;
        view = 1;

        if (startView == null) {
            startView = new StartView(this, sounds, fireworkView);
        } else {
            startView.release();
            startView = new StartView(this, sounds, fireworkView);
        }
        this.setContentView(startView);
        //addMiniAdv();
        //showDieDialog();
    }

    public boolean getDialogIsShow() {
        if (dialog != null && dialog.isShowing())
            return true;
        return false;
    }

    public void setResumeLive(boolean isResumeLive, int currentGuanKa) {
        this.isResumeLive = isResumeLive;
        this.currentGuanKa = currentGuanKa;
    }

    public void setResume(boolean isResume) {
        this.isResume = isResume;
    }

    public void endGame() {
        if (startView != null) {
            startView.setThreadFlag(false);
            startView.release();
        }
        if (mainView != null) {
            mainView.setThreadFlag(false);
            mainView.release();
        }
        //Log.d("zxc","mainactivity endGame");
        this.finish();
    }

    public void toMainView() {
        isClick = false;
        view = 2;
        if (startView != null) {
            startView.release();
            startView = null;
        }

        if (mainView == null) {
            mainView = new MainView(this, sounds, fireworkView);
        } else {
            mainView.release();
            mainView = null;
            mainView = new MainView(this, sounds, fireworkView);
        }
        setContentView(mainView);
        mainView.setResume(isResume);
        mainView.setResumeLive(isResumeLive, currentGuanKa, preScore);

        isResume = false;
        isResumeLive = false;

        //addMiniAdv();
    }

    public void addMiniAdv() {
        LinearLayout adlayout = new LinearLayout(this);
        adlayout.setGravity(Gravity.TOP);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP);
        this.addContentView(adlayout, layoutParams);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //UMGameAgent.onResume(this);
        pause = false;
        if (startView != null && !firstInit) {
            startView.setPause(false);
        }
        if (mainView != null && !firstInit) {
            mainView.setPause(false);
        }
        if (view == 1 && !firstInit) {
            startview();
        }
        firstInit = false;


    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mainView != null) {
            mainView.release();
        }
        if (startView != null) {
            startView.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        pause = true;
        if (mainView != null) {
            mainView.setPause(true);
        }
        if (startView != null) {
            startView.setPause(true);
        }

    }

    // getter和setter方法
    // getter��setter����
    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            // 要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            if (startView != null && !pause) {
                startView.bombFireworks();
            }
            handler.postDelayed(this, 2000);
        }
    };


    public void getUpdatePoints(String currencyName, int pointTotal) {
        final int glod = Utils.getKey(this, MyConstant.GOLDKEY);
        Utils.saveKey(MainActivity.this, MyConstant.GOLDKEY, (glod + pointTotal));
        if (builder != null)
            builder.setGlod(glod + pointTotal);
        Log.d("zxc117", "getUpdatePoints pointTotal = " + pointTotal);

    }

    public void getUpdatePointsFailed(String error) {

    }

}

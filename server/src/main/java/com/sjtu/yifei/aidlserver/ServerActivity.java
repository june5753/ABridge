package com.sjtu.yifei.aidlserver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sjtu.yifei.AbridgeCallBack;
import com.sjtu.yifei.IBridge;
import com.sjtu.yifei.aidl.Msg;

/**
 * Server端
 */
public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private EditText mTvContent;
    private TextView mTvShow;

    private AbridgeCallBack callBack;
    private int receiveCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        mTvContent = findViewById(R.id.tv_content);
        mTvShow = findViewById(R.id.tv_show);
        findViewById(R.id.btn_add).setOnClickListener(this);

        IBridge.registerAIDLCallBack(callBack = new AbridgeCallBack() {
                    @Override
                    public void receiveMessage(String message) {
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void receiveMsg(Msg msg) {
                        receiveCount++;
                        if (receiveCount > 5) {
                            return;
                        }

                        mTvShow.setText("收到次数：" + receiveCount +
                                msg.getMsg() + "发送时间：" + msg.getTime() + "消息来自：" + msg.getFrom() + ",发送的目标：" + msg.getTo());

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Msg msg2 = new Msg("收到后响应数据", System.currentTimeMillis(), "Game", "Game");
                        IBridge.sendAIDLMsg(msg2);
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_add) {
            String message = "内容：" + mTvContent.getText().toString();
            Msg msg = new Msg(message, System.currentTimeMillis(), "Server", "Game");
            IBridge.sendAIDLMsg(msg);
            receiveCount = 0;
        }
    }

    @Override
    protected void onDestroy() {
        IBridge.uRegisterAIDLCallBack(callBack);
        super.onDestroy();
    }
}

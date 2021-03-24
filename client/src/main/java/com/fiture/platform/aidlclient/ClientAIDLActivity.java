package com.fiture.platform.aidlclient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fiture.platform.AbridgeCallBack;
import com.fiture.platform.IBridge;
import com.fiture.platform.aidl.Msg;

/**
 * 客户端
 *
 * @author juneyang
 */
public class ClientAIDLActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvShowMsg;
    private EditText etShowMsg;
    private AbridgeCallBack callBack;
    private int receiveCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        findViewById(R.id.acquire_info).setOnClickListener(this);
        tvShowMsg = findViewById(R.id.tv_show_in_message);
        etShowMsg = findViewById(R.id.et_show_out_message);
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

                tvShowMsg.setText(
                        "收到次数：" + receiveCount + msg.getMsg() + ",发送时间：" + msg.getTime() + ",消息来自：" + msg.getFrom()
                                + ",发送的目标：" + msg.getTo());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Msg newMsg = new Msg("收到后响应数据", System.currentTimeMillis(), "Client", "Game");
                IBridge.sendAIDLMsg(newMsg);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.acquire_info) {
            String message = etShowMsg.getText().toString().trim();
            Msg msg = new Msg(message, System.currentTimeMillis(), "Client", "Game");
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

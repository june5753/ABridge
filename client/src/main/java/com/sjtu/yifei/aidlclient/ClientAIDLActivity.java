package com.sjtu.yifei.aidlclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sjtu.yifei.AbridgeCallBack;
import com.sjtu.yifei.IBridge;
import com.sjtu.yifei.aidl.Msg;

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

            @Override
            public void receiveMsg(Msg msg) {
                receiveCount++;
                if (receiveCount > 5) {
                    return;
                }

                tvShowMsg.setText("收到的次数：" + receiveCount +
                        msg.getMsg() + "时间：" + msg.getTime() + "From:" + msg.getFrom() + "消息目标：" + msg.getTo());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Msg msg2 = new Msg("收到后响应数据", System.currentTimeMillis(), "Client", "Game");
                IBridge.sendAIDLMsg(msg2);


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

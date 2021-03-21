package com.fiture.platform.aidlserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.fiture.platform.AbridgeCallBack;
import com.fiture.platform.IBridge;

public class TestAIDLActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = "MainActivity";
  private EditText tv_name;
  private EditText tv_user;

  private AbridgeCallBack callBack;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_aidl);
    tv_name = findViewById(R.id.tv_name);
    tv_user = findViewById(R.id.tv_user);
    findViewById(R.id.btn_add).setOnClickListener(this);
    IBridge.registerAIDLCallBack(callBack = new AbridgeCallBack() {
      @Override
      public void receiveMessage(String message) {
        tv_user.setText(message);
      }
    });
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.btn_add) {
      String message = "输入的内容:" + tv_name.getText().toString();
      IBridge.sendAIDLMessage(message);
    }
  }

  @Override
  protected void onDestroy() {
    IBridge.uRegisterAIDLCallBack(callBack);
    super.onDestroy();
  }
}

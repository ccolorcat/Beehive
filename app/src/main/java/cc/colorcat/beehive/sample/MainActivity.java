package cc.colorcat.beehive.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.beehive.Observer;

public class MainActivity extends AppCompatActivity {
    private GiftSubject mSubject = new GiftSubject();

    private List<Message> mMessages = new ArrayList<>();
    private RecyclerView.Adapter mAdapter = new MsgBoxAdapter(mMessages);

    private EditText mTomMsgBox;
    private EditText mJerryMsgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView previewMsgBox = findViewById(R.id.rv_preview_message_box);
        previewMsgBox.setLayoutManager(new LinearLayoutManager(this));
        previewMsgBox.setAdapter(mAdapter);
        GlobalBus.get().bind(this, mPreviewMsgBoxObserver);
        GlobalBus.get().post(new TomMessage("Hi, Jerry, this is a box message."), true);
        GlobalBus.get().post(new JerryMessage("Hi, Tom, this is a box message."), true);

        mTomMsgBox = findViewById(R.id.et_tom_message_box);
        findViewById(R.id.iv_tom).setOnClickListener(mClick);
        GlobalBus.get().bind(true, this, mTomMsgBoxObserver);
        mSubject.bind(this, mTomGiftObserver);

        mSubject.startProduce();

        mJerryMsgBox = findViewById(R.id.et_jerry_message_box);
        findViewById(R.id.iv_jerry).setOnClickListener(mClick);
        GlobalBus.get().bind(this, mJerryMsgBoxObserver);
        mSubject.bind(true, this, mJerryGiftObserver);

        findViewById(R.id.btn_stop).setOnClickListener(mClick);
    }

    @Override
    protected void onDestroy() {
        mSubject.stopProduce();
        super.onDestroy();
    }

    private View.OnClickListener mClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_tom:
                    GlobalBus.get().post(new TomMessage(mTomMsgBox.getText().toString()));
                    mTomMsgBox.setText("");
                    break;
                case R.id.iv_jerry:
                    GlobalBus.get().post(new JerryMessage(mJerryMsgBox.getText().toString()));
                    mJerryMsgBox.setText("");
                    break;
                case R.id.btn_stop:
                    mSubject.stopProduce();
                    break;
                default:
                    break;
            }
        }
    };

    private Observer<Message> mPreviewMsgBoxObserver = new Observer<Message>() {
        @Override
        public void onReceive(@NonNull Message event) {
            int size = mMessages.size();
            mMessages.add(event);
            mAdapter.notifyItemInserted(size);
        }
    };

    private Observer<JerryMessage> mTomMsgBoxObserver = new Observer<JerryMessage>() {
        @Override
        public void onReceive(@NonNull JerryMessage event) {
            mTomMsgBox.setText(event.getContent());
        }
    };

    private Observer<Gift> mTomGiftObserver = new Observer<Gift>() {
        @Override
        public void onReceive(@NonNull Gift event) {
            String msg = "received a " + event.getName();
            mTomMsgBox.setText(msg);
        }
    };

    private Observer<TomMessage> mJerryMsgBoxObserver = new Observer<TomMessage>() {
        @Override
        public void onReceive(@NonNull TomMessage event) {
            mJerryMsgBox.setText(event.getContent());
        }
    };

    private Observer<Gift> mJerryGiftObserver = new Observer<Gift>() {
        @Override
        public void onReceive(@NonNull Gift event) {
            String msg = "received a " + event.getName();
            mJerryMsgBox.setText(msg);
        }
    };
}

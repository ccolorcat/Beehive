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
    private final GiftSubject mSubject = new GiftSubject();

    private final List<Message> mMessages = new ArrayList<>();
    private final RecyclerView.Adapter<?> mAdapter = new MsgBoxAdapter(mMessages);

    private EditText mTomMsgBoxEt;
    private EditText mJerryMsgBoxEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView previewMsgBox = findViewById(R.id.rv_preview_message_box);
        previewMsgBox.setLayoutManager(new LinearLayoutManager(this));
        previewMsgBox.setAdapter(mAdapter);
        GlobalBus.get().bind(this, mPreviewMsgBox);
        GlobalBus.get().post(new TomMessage("Hi, Jerry, this is a box message."), true);
        GlobalBus.get().post(new JerryMessage("Hi, Tom, this is a box message."), true);

        mTomMsgBoxEt = findViewById(R.id.et_tom_message_box);
        findViewById(R.id.iv_tom).setOnClickListener(mClick);
        GlobalBus.get().bind(true, this, mTomMsgBox);
        mSubject.bind(this, mTomGiftBox);

        mJerryMsgBoxEt = findViewById(R.id.et_jerry_message_box);
        findViewById(R.id.iv_jerry).setOnClickListener(mClick);
        GlobalBus.get().bind(this, mJerryMsgBox);
        mSubject.bind(true, this, mJerryGiftBox);

        findViewById(R.id.btn_stop).setOnClickListener(mClick);
        mSubject.startProduce();
    }

    @Override
    protected void onDestroy() {
        mSubject.stopProduce();
        super.onDestroy();
    }

    private final View.OnClickListener mClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_tom) {
                GlobalBus.get().post(new TomMessage(mTomMsgBoxEt.getText().toString()));
                mTomMsgBoxEt.setText("");
            } else if (id == R.id.iv_jerry) {
                GlobalBus.get().post(new JerryMessage(mJerryMsgBoxEt.getText().toString()));
                mJerryMsgBoxEt.setText("");
            } else if (id == R.id.btn_stop) {
                mSubject.stopProduce();
            }
        }
    };

    private final Observer<Message> mPreviewMsgBox = new Observer<Message>() {
        @Override
        public void onReceive(@NonNull Message event) {
            int size = mMessages.size();
            mMessages.add(event);
            mAdapter.notifyItemInserted(size);
        }
    };

    private final Observer<JerryMessage> mTomMsgBox = new Observer<JerryMessage>() {
        @Override
        public void onReceive(@NonNull JerryMessage event) {
            mTomMsgBoxEt.setText(event.getContent());
        }
    };

    private final Observer<Gift> mTomGiftBox = new Observer<Gift>() {
        @Override
        public void onReceive(@NonNull Gift event) {
            String msg = "received a " + event.getName();
            mTomMsgBoxEt.setText(msg);
        }
    };

    private final Observer<TomMessage> mJerryMsgBox = new Observer<TomMessage>() {
        @Override
        public void onReceive(@NonNull TomMessage event) {
            mJerryMsgBoxEt.setText(event.getContent());
        }
    };

    private final Observer<Gift> mJerryGiftBox = new Observer<Gift>() {
        @Override
        public void onReceive(@NonNull Gift event) {
            String msg = "received a " + event.getName();
            mJerryMsgBoxEt.setText(msg);
        }
    };
}

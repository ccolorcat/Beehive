package cc.colorcat.beehive.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.beehive.HandlerSubject;
import cc.colorcat.beehive.Observer;
import cc.colorcat.beehive.Producer;

public class MainActivity extends AppCompatActivity {
    private boolean mContinueGift = true;
    private String[] mGiftName = {"apple", "pencil", "book", "phone", "computer"};
    private int mIndex = 0;
    private HandlerSubject<Gift> mSubject = new HandlerSubject<>(new Handler(Looper.getMainLooper()), new Producer<Gift>() {
        @NonNull
        @Override
        public Gift produce() {
            return new Gift(mGiftName[mIndex]);
        }
    });

    private List<Message> mMessages = new ArrayList<>();
    private RecyclerView.Adapter<MsgBoxHolder> mAdapter = new RecyclerView.Adapter<MsgBoxHolder>() {
        @NonNull
        @Override
        public MsgBoxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_text, parent, false);
            return new MsgBoxHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MsgBoxHolder holder, int position) {
            holder.textView.setText(mMessages.get(position).said());
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    };

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

        startProduce();

        mJerryMsgBox = findViewById(R.id.et_jerry_message_box);
        findViewById(R.id.iv_jerry).setOnClickListener(mClick);
        GlobalBus.get().bind(this, mJerryMsgBoxObserver);
        mSubject.bind(true, this, mJerryGiftObserver);

        findViewById(R.id.btn_stop).setOnClickListener(mClick);
    }

    private void startProduce() {
        new Thread("produce gift") {
            @Override
            public void run() {
                while (mContinueGift && !MainActivity.this.isFinishing()) {
                    mIndex = (mIndex + 1) % mGiftName.length;
                    mSubject.notifyChanged();
                    SystemClock.sleep(3000);
                }
            }
        }.start();
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
                    mContinueGift = false;
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

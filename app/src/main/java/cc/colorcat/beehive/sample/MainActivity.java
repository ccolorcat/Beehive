package cc.colorcat.beehive.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cc.colorcat.beehive.Observer;

public class MainActivity extends AppCompatActivity {
    private TextView mMessageBox;

    private TextView mTomReceived;
    private EditText mTomPosted;

    private TextView mJerryReceived;
    private EditText mJerryPosted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageBox = findViewById(R.id.tv_message_box);
        GlobalBus.get().bind(this, new Observer<Message>() {
            @Override
            public void onReceive(@NonNull Message event) {
                String message = mMessageBox.getText().toString() + '\n'
                        + event.getName() + " said: " + event.getContent();
                mMessageBox.setText(message);
            }
        });
        GlobalBus.get().post(new TomMessage("Hi, Jerry, this is box message."), true);
        GlobalBus.get().post(new JerryMessage("Hi, Tom, this is box message."), true);

        mTomReceived = findViewById(R.id.tv_tom_received);
        mTomPosted = findViewById(R.id.et_tom_posted);
        findViewById(R.id.iv_tom).setOnClickListener(mClick);
        GlobalBus.get().bind(true, this, new Observer<JerryMessage>() {
            @Override
            public void onReceive(@NonNull JerryMessage event) {
                mTomReceived.setText(event.getContent());
            }
        });

        mJerryReceived = findViewById(R.id.tv_jerry_received);
        mJerryPosted = findViewById(R.id.et_jerry_posted);
        findViewById(R.id.iv_jerry).setOnClickListener(mClick);
        GlobalBus.get().bind(true, this, new Observer<TomMessage>() {
            @Override
            public void onReceive(@NonNull TomMessage event) {
                mJerryReceived.setText(event.getContent());
            }
        });
    }

    private View.OnClickListener mClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_tom:
                    GlobalBus.get().post(new TomMessage(mTomPosted.getText().toString()));
                    break;
                case R.id.iv_jerry:
                    GlobalBus.get().post(new JerryMessage(mJerryPosted.getText().toString()));
                    break;
                default:
                    break;
            }
        }
    };
}

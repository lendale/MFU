package com.gabisan.os.mfu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InitializerActivity extends AppCompatActivity {

    @BindView(R.id.et_input_buffer_size)
    EditText etBufferSize;
    @BindView(R.id.et_input_reference_string)
    EditText etRefStr;
    @BindView(R.id.btn_start_simulation)
    AppCompatButton btnStartSim;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private String TAG = InitializerActivity.class.getSimpleName();
    private String inRefStr[];
    private String inputStr;
    private int size = 0;
    private int buffer;
    private int MAX = 40; //MAX number of references (with commas)
    private int MAX_BUFF = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initializer);

        ButterKnife.bind(this);

        etRefStr.addTextChangedListener(new NumberTextWatcher(etRefStr));

        btnStartSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    public void start() {
        btnStartSim.setEnabled(false);

        if (!validate()) {
            btnStartSim.setEnabled(true);
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(InitializerActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Initializing...");
        progressDialog.show();

        buffer = Integer.parseInt(String.valueOf(etBufferSize.getText()));
        inputStr = NumberTextWatcher.trimCommaOfString(String.valueOf(etRefStr.getText()));

        // LINE 58 - 62 (Reference array size initialization)
        for (int i = 0; i < inputStr.length(); i++) {
            size++;
        }
        inRefStr = new String[size];

        for (int i = 0; i < inRefStr.length; i++) {
            inRefStr[i] = String.valueOf(inputStr.charAt(i));
        }
        Log.d(TAG, "" + inRefStr[1]);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("INPUT_REF", inRefStr);
                        intent.putExtra("INPUT_BUFFSIZE", buffer);
                        btnStartSim.setEnabled(true);
                        startActivity(intent);
                    }
                }, 1500);
    }

    public boolean validate() {
        boolean valid = true;

        String buff = String.valueOf(etBufferSize.getText());
        String refStr = String.valueOf(etRefStr.getText());
        int buffSize = 0;

        if (buff.isEmpty()) {
            etBufferSize.setError("Please enter buffer size");
            valid = false;
        }else {
            buffSize = Integer.parseInt(String.valueOf(etBufferSize.getText()));
            if (buffSize <= 0) {
                Snackbar.make(coordinatorLayout, "Buffer Size should not be less than zero", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                valid = false;
            }
            if (buffSize >= 1 && buffSize <3) {
                Snackbar.make(coordinatorLayout, "Minimum Buffer Size should be 3", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                valid = false;
            }
            if (buffSize > MAX_BUFF) {
                Snackbar.make(coordinatorLayout, "Maximum Buffer Size is 10", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                valid = false;
            }
        }

        if ((refStr.isEmpty() || refStr.length() < buffSize + 2) && buffSize <= MAX_BUFF) {
            etRefStr.setError("Please enter at least " + buffSize + " references");
            valid = false;
        } else if (refStr.length() >= MAX) {
            Snackbar.make(coordinatorLayout, "References should not exceed 20 items", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            valid = false;
        }

        return valid;
    }
}

package com.egneese.sellers.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.egneese.sellers.R;
import com.egneese.sellers.asynctasks.ConfirmOTPAsyncTask;
import com.egneese.sellers.asynctasks.ResendOTPAsyncTask;
import com.egneese.sellers.dto.MessageCustomDialogDTO;
import com.egneese.sellers.dto.RegistrationDTO;
import com.egneese.sellers.ui.CustomTouchListener;
import com.egneese.sellers.ui.MessageDialog;
import com.egneese.sellers.ui.SnackBar;
import com.egneese.sellers.ui.TypefaceSpan;
import com.egneese.sellers.util.ClockUpdate;
import com.egneese.sellers.util.NetworkCheck;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Dell on 1/6/2016.
 */
public class ConfirmOTPActivity extends ActionBarActivity {
    private RegistrationDTO registrationDTO;
    private CountDownTimer countDownTimer;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.etOTP)
    EditText etOTP;
    @InjectView(R.id.etPassword)
    EditText etPassword;
    @InjectView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @InjectView(R.id.btnConfirmOTP)
    Button btnConfirmOTP;
    @InjectView(R.id.txtTimer)
    TextView txtTimer;
    @InjectView(R.id.txtResendOTP)
    TextView txtResendOTP;
    @InjectView(R.id.txtShowPassword)
    TextView txtShowPassword;
    @InjectView(R.id.txtShowConfirmPassword)
    TextView txtShowConfirmPassword;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            try{
                String message1 = message.substring(message.length() - 6);
                etOTP.setText(message1);
            }catch(Exception e){

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(!NetworkCheck.isNetworkAvailable(this))
            SnackBar.noInternet(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        registrationDTO = gson.fromJson(bundle.getString("registrationDTO"), RegistrationDTO.class);
        populate();

        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
        messageCustomDialogDTO.setTitle(getResources().getString(R.string.success));
        messageCustomDialogDTO.setMessage(getResources().getString(R.string.otp_sent));
        messageCustomDialogDTO.setContext(ConfirmOTPActivity.this);
        messageCustomDialogDTO.setButton("OK");
        MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
        messageCustomDialog.success();


    }

    private void populate() {
        ButterKnife.inject(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("otp-received"));

        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            btnConfirmOTP.setBackgroundResource(R.drawable.ripple);
        }

        txtResendOTP.setOnTouchListener(new CustomTouchListener());
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SpannableString s = new SpannableString("Confirm OTP");
        s.setSpan(new TypefaceSpan(this, "LatoLatin-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)toolbar.findViewById(R.id.toolbarTitle)).setText(s);

        getSupportActionBar().setTitle("");

        countDownTimer = new CountDownTimer(60 * 500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = ClockUpdate.clock(millisUntilFinished);
                txtTimer.setText(time + "Seconds");
            }

            @Override
            public void onFinish() {
                txtTimer.setVisibility(View.GONE);
                txtResendOTP.setVisibility(View.VISIBLE);

            }
        };
        countDownTimer.start();




        txtShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtShowPassword.getText().toString().equals("show")) {
                    etPassword.setTransformationMethod(null);
                    txtShowPassword.setText("hide");
                    etPassword.setSelection(etPassword.getText().toString().length());
                } else {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    txtShowPassword.setText("show");
                    etPassword.setSelection(etPassword.getText().toString().length());
                }
            }
        });

        txtShowConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtShowConfirmPassword.getText().toString().equals("show")){
                    etConfirmPassword.setTransformationMethod(null);
                    txtShowConfirmPassword.setText("hide");
                    etConfirmPassword.setSelection(etConfirmPassword.getText().toString().length());
                }
                else {
                    etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    txtShowConfirmPassword.setText("show");
                    etConfirmPassword.setSelection(etConfirmPassword.getText().toString().length());
                }
            }
        });

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer = new CountDownTimer(30 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String time = ClockUpdate.clock(millisUntilFinished);
                        txtTimer.setText(time + "Seconds");
                    }

                    @Override
                    public void onFinish() {
                        txtTimer.setVisibility(View.GONE);
                        txtResendOTP.setVisibility(View.VISIBLE);
                    }
                };
                countDownTimer.start();
                txtTimer.setVisibility(View.VISIBLE);
                txtResendOTP.setVisibility(View.GONE);
                ResendOTPAsyncTask resendOTPAsyncTask = new ResendOTPAsyncTask(ConfirmOTPActivity.this);
                resendOTPAsyncTask.execute();

            }
        });

        btnConfirmOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString().trim();
                String rePassword = etConfirmPassword.getText().toString().trim();
                String otp = etOTP.getText().toString().trim();
                if(NetworkCheck.isNetworkAvailable(ConfirmOTPActivity.this)) {
                    if(password.equals(rePassword)) {
                        if (password.length() >= 6) {
                            if(otp.length() == 6) {
                                registrationDTO.setPassword(password);
                                ConfirmOTPAsyncTask confirmOTPAsyncTask = new ConfirmOTPAsyncTask(ConfirmOTPActivity.this, registrationDTO, otp);
                                confirmOTPAsyncTask.execute();
                            }else{
                                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                                messageCustomDialogDTO.setTitle(getResources().getString(R.string.confirm_otp_auth_error));
                                messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                                messageCustomDialogDTO.setMessage(getResources().getString(R.string.confirm_invalid_otp));
                                messageCustomDialogDTO.setContext(ConfirmOTPActivity.this);
                                SnackBar.show(ConfirmOTPActivity.this, messageCustomDialogDTO);
                            }
                        } else {
                            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                            messageCustomDialogDTO.setTitle(getResources().getString(R.string.confirm_otp_auth_error));
                            messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                            messageCustomDialogDTO.setMessage(getResources().getString(R.string.confirm_otp_small_password));
                            messageCustomDialogDTO.setContext(ConfirmOTPActivity.this);
                            SnackBar.show(ConfirmOTPActivity.this, messageCustomDialogDTO);
                        }
                    }else{
                        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                        messageCustomDialogDTO.setTitle(getResources().getString(R.string.confirm_otp_auth_error));
                        messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                        messageCustomDialogDTO.setMessage(getResources().getString(R.string.confirm_otp_diff_password));
                        messageCustomDialogDTO.setContext(ConfirmOTPActivity.this);
                        SnackBar.show(ConfirmOTPActivity.this, messageCustomDialogDTO);
                    }
                }else{
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle(getResources().getString(R.string.register_activity_no_internet_title));
                    messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                    messageCustomDialogDTO.setMessage(getResources().getString(R.string.register_activity_no_internet));
                    messageCustomDialogDTO.setContext(ConfirmOTPActivity.this);
                    SnackBar.show(ConfirmOTPActivity.this, messageCustomDialogDTO);
                }
            }
        });



}
}

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

import com.egneese.sellers.R;
import com.egneese.sellers.asynctasks.ResendOTPResetAsyncTask;
import com.egneese.sellers.asynctasks.ResetPasswordAsyncTask;
import com.egneese.sellers.constants.EggWallet;
import com.egneese.sellers.dto.SellerDTO;
import com.egneese.sellers.dto.MessageCustomDialogDTO;
import com.egneese.sellers.dto.RequiredDTO;
import com.egneese.sellers.ui.MessageDialog;
import com.egneese.sellers.ui.SnackBar;
import com.egneese.sellers.ui.TypefaceSpan;
import com.egneese.sellers.util.ClockUpdate;
import com.egneese.sellers.util.NetworkCheck;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Dell on 1/8/2016.
 */
public class ResetPasswordActivity extends ActionBarActivity {
    private SellerDTO sellerDTO;
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
            try {
                String message1 = message.substring(message.length() - 4);
                etOTP.setText(message1);
            } catch (Exception e) {

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        populate();
        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
        messageCustomDialogDTO.setTitle("Success");
        messageCustomDialogDTO.setMessage(getResources().getString(R.string.otp_sent));
        messageCustomDialogDTO.setContext(ResetPasswordActivity.this);
        messageCustomDialogDTO.setButton("OK");
        MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
        messageCustomDialog.success();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkCheck.isNetworkAvailable(this))
            SnackBar.noInternet(this);
    }

    private void populate() {
        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        sellerDTO = gson.fromJson(bundle.getString("sellerDTO"), SellerDTO.class);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("otp-received"));
        ButterKnife.inject(this);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SpannableString s = new SpannableString("Reset Password");
        s.setSpan(new TypefaceSpan(this, "LatoLatin-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle("");
        ((TextView) toolbar.findViewById(R.id.toolbarTitle)).setText(s);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnConfirmOTP.setBackgroundResource(R.drawable.ripple);
        }

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
                if (txtShowConfirmPassword.getText().toString().equals("show")) {
                    etConfirmPassword.setTransformationMethod(null);
                    txtShowConfirmPassword.setText("hide");
                    etConfirmPassword.setSelection(etConfirmPassword.getText().toString().length());

                } else {
                    etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    txtShowConfirmPassword.setText("show");
                    etConfirmPassword.setSelection(etConfirmPassword.getText().toString().length());
                }
            }
        });
        countDownTimer = new CountDownTimer(60 * 500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = ClockUpdate.clock(millisUntilFinished);
                txtTimer.setText(time + "  Seconds");

            }

            @Override
            public void onFinish() {
                txtTimer.setVisibility(View.GONE);
                txtResendOTP.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer = new CountDownTimer(30 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String time = ClockUpdate.clock(millisUntilFinished);
                        txtTimer.setText(time + "  Seconds");
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

                RequiredDTO requiredDTO = new RequiredDTO();
                requiredDTO.setId(sellerDTO.getId());
                requiredDTO.setAccessToken(sellerDTO.getAccessToken());
                requiredDTO.setMobile(sellerDTO.getMobile());
                requiredDTO.setScope(EggWallet.SCOPE);

                ResendOTPResetAsyncTask resendOTPResetAsyncTask = new ResendOTPResetAsyncTask(ResetPasswordActivity.this, requiredDTO);
                resendOTPResetAsyncTask.execute();
            }
        });

        btnConfirmOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString().trim();
                String rePassword = etConfirmPassword.getText().toString().trim();
                String otp = etOTP.getText().toString().trim();
                if (NetworkCheck.isNetworkAvailable(ResetPasswordActivity.this)) {
                    if (password.equals(rePassword)) {
                        if (password.length() >= 6) {
                            if (otp.length() == 6) {
                                ResetPasswordAsyncTask resetPasswordAsyncTask = new ResetPasswordAsyncTask(ResetPasswordActivity.this, sellerDTO, password, otp);
                                resetPasswordAsyncTask.execute();
                            } else {
                                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                                messageCustomDialogDTO.setTitle(getResources().getString(R.string.confirm_otp_auth_error));
                                messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                                messageCustomDialogDTO.setMessage(getResources().getString(R.string.confirm_invalid_otp));
                                messageCustomDialogDTO.setContext(ResetPasswordActivity.this);
                                SnackBar.show(ResetPasswordActivity.this, messageCustomDialogDTO);
                            }
                        } else {
                            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                            messageCustomDialogDTO.setTitle(getResources().getString(R.string.confirm_otp_auth_error));
                            messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                            messageCustomDialogDTO.setMessage(getResources().getString(R.string.confirm_otp_small_password));
                            messageCustomDialogDTO.setContext(ResetPasswordActivity.this);
                            SnackBar.show(ResetPasswordActivity.this, messageCustomDialogDTO);
                        }
                    } else {
                        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                        messageCustomDialogDTO.setTitle(getResources().getString(R.string.confirm_otp_auth_error));
                        messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                        messageCustomDialogDTO.setMessage(getResources().getString(R.string.confirm_otp_diff_password));
                        messageCustomDialogDTO.setContext(ResetPasswordActivity.this);
                        SnackBar.show(ResetPasswordActivity.this, messageCustomDialogDTO);
                    }
                } else {
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle(getResources().getString(R.string.register_activity_no_internet_title));
                    messageCustomDialogDTO.setButton(getResources().getString(R.string.ok));
                    messageCustomDialogDTO.setMessage(getResources().getString(R.string.register_activity_no_internet));
                    messageCustomDialogDTO.setContext(ResetPasswordActivity.this);
                    SnackBar.show(ResetPasswordActivity.this, messageCustomDialogDTO);
                }
            }
        });


    }
}

package com.along.android.healthmanagement.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.along.android.healthmanagement.R;
import com.along.android.healthmanagement.apis.Apis;
import com.along.android.healthmanagement.common.JsonCallback;
import com.along.android.healthmanagement.entities.User;
import com.along.android.healthmanagement.helpers.Validation;
import com.along.android.healthmanagement.models.UserEntity;
import com.along.android.healthmanagement.network.BaseResponse;
import com.along.android.healthmanagement.network.SimpleResponse;
import com.along.android.healthmanagement.utils.JSONUtil;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {
    //DatabaseHelper helper = new DatabaseHelper(this);

    private RadioGroup radioGroupGender;
    private RadioButton radioButtonGender;
    EditText etEmail, etPassword, etConfirmpassword, etRealname, etAge, etPhonenumber;
    Button btn_register, btn_cancel;
    private TextInputLayout emailLayout;

    //EditText[] etUserinfo = {etUsername, etPassword, etEmail, etRealname, etGender, etAge, etPhonenumber, etWeight, etHeight};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("HealthManagement");

        emailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);

        //etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        //etEmail.addTextChangedListener(new MyTextWatcher(etEmail));

        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmpassword = (EditText) findViewById(R.id.etConfirmpassword);
        etRealname = (EditText) findViewById(R.id.etRealname);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        etAge = (EditText) findViewById(R.id.etAge);
        etPhonenumber = (EditText) findViewById(R.id.etPhonenumber);
//        etWeight = (EditText) findViewById(R.id.etWeight);
//        etHeight = (EditText) findViewById(R.id.etHeight);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                radioButtonGender = (RadioButton) findViewById(selectedId);

                User user = new User();

                //user.setUsername(etUsername.getText().toString());
                user.setEmail(etEmail.getText().toString());
                user.setPassword(etPassword.getText().toString());
                user.setRealname(etRealname.getText().toString());
                user.setGender(radioButtonGender.getText().toString());
                user.setAge(etAge.getText().toString());
                user.setPhonenumber(etPhonenumber.getText().toString());
//                user.setWeight(etWeight.getText().toString());
//                user.setHeight(etHeight.getText().toString());
                user.setPasswordExpirationTime(0);

                if (Validation.isEmpty(user, RegisterActivity.this) &&
                        Validation.isValidPassword(user.getPassword(), RegisterActivity.this) &&
                        Validation.isPasswordMatch(etPassword.getText().toString(), etConfirmpassword.getText().toString(), RegisterActivity.this) &&
                        /*Validation.isUserExsist(user.getEmail(), RegisterActivity.this) &&*/
                        Validation.isValidEmail(user.getEmail(), RegisterActivity.this) &&
                        Validation.isValidPhonenumber(user.getPhonenumber(), RegisterActivity.this) &&
                        Validation.isNumeric(user.getAge(), "age", RegisterActivity.this)) {
                    // helper.insertContact(user);

                    try{
                        long userId = user.save();

                        SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        sp.edit().putLong("uid", userId).apply();

                        /*Intent intent = new Intent();
                        intent.setClass(RegisterActivity.this, NavigationDrawerActivity.class);
                        startActivity(intent);*/
                        postRegister(user);

                    }catch (Exception e){
                        Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                        Log.e("Register Error", "Register Error", e);
                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "Form filling is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public void postRegister(User user) {
        user.setPasswordExpirationTime(0);
        JSONObject jsonObject = JSONUtil.toJSONObject(user);
        if (jsonObject == null) {
            return;
        }
        OkGo.<SimpleResponse>post(Apis.getRegisterUrl())
                .tag(this)
                .upJson(jsonObject)
                /*.params("email", user.getEmail())
                .params("password", user.getPassword())
                .params("realname", user.getRealname())
                .params("gender", user.getGender())
                .params("age", user.getAge())
                .params("phonenumber", user.getPhonenumber())
                .params("passwordExpirationTime", user.getPasswordExpirationTime())*/
                .execute(new JsonCallback<SimpleResponse>() {
                    @Override
                    public void onSuccess(Response<SimpleResponse> response) {
                        if (response != null && response.body() != null) {
                            SimpleResponse result = response.body();
                            if (result.code == 0) {
                                Intent intent = new Intent();
                                intent.setClass(RegisterActivity.this, NavigationDrawerActivity.class);
                                startActivity(intent);
                                return;
                            }
                        }
                        Toast.makeText(RegisterActivity.this, "register error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Response<SimpleResponse> response) {
                        super.onError(response);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etEmail:
                    validateEmailInline();
                    break;

            }
        }
    }

    private void validateEmailInline() {
        String email = etEmail.getText().toString();
        if(null != email && !email.equals("") && Validation.isValidEmail(email, RegisterActivity.this)) {
            emailLayout.setErrorEnabled(false);
        }
        else {
            emailLayout.setError(getString(R.string.invalid_email));
        }
    }
}

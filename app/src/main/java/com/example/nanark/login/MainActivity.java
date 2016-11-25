package com.example.nanark.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.example.nanark.login.R.id.progressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnsignup, btnlogin;
    EditText etemail, etpass;
    private Context context;
    private UserApi user_api;
    private Call<User> call;
    TextView tvregis;
    AwesomeValidation mAwesomeValidation;
    Retrofit retrofit;
    String check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etemail = (EditText) findViewById(R.id.edtex_email_login);
        etpass = (EditText) findViewById(R.id.edtex_password_login);
        btnsignup = (Button) findViewById(R.id.btn_signup);
        btnlogin = (Button) findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(this);
        btnsignup.setOnClickListener(this);


        context = this;
        Gson gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://private-bf3035-login338.apiary-mock.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(etemail, android.util.Patterns.EMAIL_ADDRESS, "Masukan Email");
        mAwesomeValidation.addValidation(etpass, "^.*(?=.{8,}).*$", "Masukan Password");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (mAwesomeValidation.validate() == true) {
                    final AlertDialog dialog = new SpotsDialog(context);
                    dialog.show();
                    // // implement interface for get all user
                    user_api = retrofit.create(UserApi.class);
                    call = user_api.getUser();
                    call.enqueue(new Callback<User>() {

                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            int status = response.code();
                            String email = String.valueOf(response.body().getEmail());
                            String password = String.valueOf(response.body().getPassword());
                            if (etemail.getText().toString().equals(email) && etpass.getText().toString().equals(password)) {
                                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("email", etemail.getText().toString());
                                editor.apply();

                                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                                startActivity(i);
                                dialog.dismiss();
                                finish();
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        MainActivity.this).create();
                                // Setting Dialog Title
                                alertDialog.setTitle("Alert Dialog");
                                // Setting Dialog Message
                                alertDialog.setMessage("Incorrect Email or Password");
                                // Setting OK Button
                                alertDialog.setButton("Try", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to execute after dialog closed
                                        Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                // Showing Alert Message
                                alertDialog.show();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            //tv_respond.setText(String.valueOf(t));
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    MainActivity.this).create();

                            // Setting Dialog Message
                            alertDialog.setMessage("Error your Call");
                            // Setting OK Button
                        }
                    });
                }
                break;
            case R.id.btn_signup:
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
                break;
        }

    }
}




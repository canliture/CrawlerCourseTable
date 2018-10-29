package com.shengid.liture.coursetable.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shengid.liture.coursetable.Entity.CourseTable;
import com.shengid.liture.coursetable.FromHandler.From;
import com.shengid.liture.coursetable.Helper.Alert;
import com.shengid.liture.coursetable.R;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private EditText userNameView;
    private EditText passwordView;
    private EditText verifyTextView;
    private ImageView verifyImage;
    private Button loginBtn;

    private From form;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)  actionBar.hide();

        //get components
        userNameView = findViewById(R.id.username_view);
        passwordView = findViewById(R.id.password_view);
        verifyTextView = findViewById(R.id.verify_text);
        verifyImage = findViewById(R.id.verify_image);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener( view -> loginBtnHandler(view) );   // submit onClick
        verifyImage.setOnClickListener( (view) -> freshForm() );

        pref = getSharedPreferences("SomeData", MODE_PRIVATE);
        if( pref != null && !"".equals( pref.getString("responseBody", "") ) ){
            userNameView.setText( pref.getString("username", "") );
            passwordView.setText( pref.getString("password", "") );
            handlerSubmit(this, pref.getString("responseBody", "") );
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        freshForm();
    }

    public void freshForm(){
        form = new From();
        form.getVerifyCodeImage(this);                        // get and load
    }

    private void loginBtnHandler(View view){
        //if form is incomplete
        if( userNameView.getText().length() == 0 || passwordView.getText().length() == 0 || verifyTextView.getText().length() == 0 ){
            Alert.info( view.getContext(), "Please complete your information!");
            return;
        }
        //check the signature
        form.submit(this, userNameView.getText().toString(), passwordView.getText().toString(), verifyTextView.getText().toString() );
    }

    public void loadImage(Bitmap bitMap){
        runOnUiThread( ()-> {
            if (bitMap == null)    Alert.info( this, "Network error!" );
            else  verifyImage.setImageBitmap(bitMap);
        });
    }

    public void handlerSubmit(Context context, String responseBody ){

        if( responseBody == null || "".equals( responseBody )  ){
            Alert.info( (LoginActivity)context, "Information Error Or Network Error!");
            freshForm();
        } else {
            saveData(responseBody);         //default save username and password
            runOnUiThread(() -> {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("responseBody", responseBody);
                startActivity(intent);
            });
        }
    }

    private void saveData(String responseBody){
        SharedPreferences.Editor editor = getSharedPreferences("SomeData", MODE_PRIVATE).edit();
        editor.putString("username", userNameView.getText().toString() );
        editor.putString("password", passwordView.getText().toString() );
        editor.putString("responseBody", responseBody);
        editor.apply();
    }

}

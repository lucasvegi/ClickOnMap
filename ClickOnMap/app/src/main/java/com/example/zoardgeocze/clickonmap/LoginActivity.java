package com.example.zoardgeocze.clickonmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoardgeocze.clickonmap.Model.SystemTile;
import com.example.zoardgeocze.clickonmap.Model.VGISystem;
import com.example.zoardgeocze.clickonmap.Retrofit.RetrofitClientInitializer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by ZoardGeocze on 28/04/17.
 */

public class LoginActivity extends AppCompatActivity {

    private VGISystem vgiSystem;
    private Intent intent;
    private Bundle bundle;

    private EditText loginUserEmail;
    private EditText loginUserPassword;

    private Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView title = (TextView) findViewById(R.id.login_title);
        this.loginUserEmail = (EditText) findViewById(R.id.login_user);
        this.loginUserPassword = (EditText) findViewById(R.id.login_password);

        this.intent = this.getIntent();
        this.bundle = intent.getExtras();

        this.vgiSystem = (VGISystem) this.bundle.getSerializable("vgiSystem");

        title.setText(this.vgiSystem.getName());

        frontToRegister();
        verifyUser();

    }

    private void frontToRegister() {
        com.example.zoardgeocze.clickonmap.Design.AvenirBookTextView loginToRegisterButton = (com.example.zoardgeocze.clickonmap.Design.AvenirBookTextView)
                findViewById(R.id.login_register_btn);
        loginToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),RegisterActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
    }

    public void backToMenu(View view) {
        finish();
    }

    //TODO: Finalizar implementação de Login
    //Botao para verificar usuario no sistema
    private void verifyUser() {

        this.loginButton = (Button) findViewById(R.id.login_btn);
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = String.valueOf(loginUserEmail.getText());
                String userPassword = String.valueOf(loginUserPassword.getText());

                if(!userEmail.equals("") && !userPassword.equals("")) {

                    final ProgressDialog mProgressDialog = new ProgressDialog(LoginActivity.this);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setMessage("Verificando Usuário...");
                    mProgressDialog.show();

                    String base_url = vgiSystem.getAdress() + "/";

                    Call<String> call = new RetrofitClientInitializer(base_url).getUserService().verifyUser("verifyUser",userEmail,userPassword);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            Log.i("verify_user:", response.body());

                            if(response.body().equals("true")) {

                            } else {
                                Toast.makeText(getBaseContext(),"Usuário não existe ou senha incorreta.",Toast.LENGTH_SHORT).show();
                            }

                            if (mProgressDialog.isShowing()){
                                mProgressDialog.dismiss();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Log.i("verify_user:", t.getMessage());

                            Toast.makeText(getBaseContext(),"Sem conexão de rede.",Toast.LENGTH_SHORT).show();

                            if (mProgressDialog.isShowing()){
                                mProgressDialog.dismiss();
                            }

                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(),"Todos os campos são obrigatórios.",Toast.LENGTH_SHORT).show();
                }



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0) {
            setResult(0,this.intent);
            finish();
        }
        else if(resultCode == 1) {
            Bundle bundle = data.getExtras();
            this.intent.putExtras(bundle);
            setResult(1,this.intent);
            finish();
        }
    }
}

package com.example.skylar.earn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.skylar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.stellar.sdk.KeyPair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseDatabase userDatabase;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String firebaseId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        KeyPair keyPair = KeyPair.random();
        char[] secret = keyPair.getSecretSeed();
        String privateKey = new String(secret);
        String publicKey = keyPair.getAccountId();

        userDatabase = FirebaseDatabase.getInstance();
        reference = userDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseId = user.getUid();

        SharedPreferences sharedPreferences = getSharedPreferences("myKeys", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();



        Button btnAccount = findViewById(R.id.buttonAccount);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("publicKey", publicKey);
                editor.putString("privateKey", privateKey);
                editor.apply();
                Intent intent = new Intent(CreateAccountActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addingPublicKey(publicKey);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    createAccount(publicKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }


    private void addingPublicKey(String publicKey){
        reference.child("users").child(firebaseId).child("publicKey").setValue(publicKey);
    }

    private void createAccount(String publicKey) throws IOException {
        String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s", publicKey);

        InputStream response = new URL(friendbotUrl).openStream();
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();

        Thread thread = new Thread(){
        public void run(){
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CreateAccountActivity.this, "SUCCESS! You have a new account \n" , Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
thread.start();

    }



}
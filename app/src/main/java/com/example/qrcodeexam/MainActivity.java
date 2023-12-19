package com.example.qrcodeexam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button btnScan;
    private Button btnToList;
    private TextView txtCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("QR Code Scanner by Peter Kozma");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();
            }
        });

        btnToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeString = txtCode.getText().toString();
                if (codeString.length() < 1){
                    //error goes here
                }else {
                    SharedPreferences sharedPref = getSharedPreferences("SharedPreferences",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("scannedCode", codeString);
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, ListaAdatok.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null) {
                Toast.makeText(this,"Returned to the app!", Toast.LENGTH_SHORT).show();
            }else {
                txtCode.setText(result.getContents());
            }
        }
        else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void init(){
        btnScan = findViewById(R.id.btnScan);
        btnToList = findViewById(R.id.btnToList);
        txtCode = findViewById(R.id.txtCode);
    }
}
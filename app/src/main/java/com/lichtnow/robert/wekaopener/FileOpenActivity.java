package com.lichtnow.robert.wekaopener;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import udc.edu.weka.classifiers.Classifier;


public class FileOpenActivity extends AppCompatActivity {

    protected Button loadBtn = null;
    protected Classifier cls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_open);
        this.loadBtn = this.findViewById(R.id.loadBtn);

        this.loadBtn.setOnClickListener((View v) -> {
            int permissionCheck = ContextCompat.checkSelfPermission(FileOpenActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(FileOpenActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        123);
            }
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            try{
                startActivityForResult(intent, 123);

            } catch (ActivityNotFoundException e) {
                Toast.makeText(FileOpenActivity.this, "There are no file explorer clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(FileOpenActivity.this, "Para selecionar o arquivo, deve ser permitida a leitura do disco interno", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 123:
                if (resultCode == RESULT_OK) {
                    String filePath = data.getData().getPath().split(":")[1];
                    this.cls = loadClassifier(filePath);

                    Intent clsActivity = new Intent(this, ClassifierActivity.class);
                    clsActivity.putExtra("cls",(Serializable)this.cls);
                    startActivity(clsActivity);
                }
        }
    }

    public Classifier loadClassifier(String filePath){
        File dir = Environment.getExternalStorageDirectory();
        filePath = new File(dir + File.separator + filePath).getAbsolutePath();
        Classifier cls = null;
        try{
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);
            cls = (Classifier) in.readObject();
            Toast.makeText(FileOpenActivity.this,"Carreguei o arquivo",Toast.LENGTH_SHORT).show();
        }
        catch(Exception ex){
            Toast.makeText(FileOpenActivity.this,"Não foi possível ler o arquivo",Toast.LENGTH_SHORT).show();
        }
        return cls;
    }




}

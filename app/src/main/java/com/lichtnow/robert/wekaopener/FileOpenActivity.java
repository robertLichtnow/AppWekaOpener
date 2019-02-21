package com.lichtnow.robert.wekaopener;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileOpenActivity extends AppCompatActivity {

    protected String fileContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_open);

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

        } catch (ActivityNotFoundException e){
            Toast.makeText(FileOpenActivity.this, "There are no file explorer clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {
                    Toast.makeText(FileOpenActivity.this, "BOOOOOO ALLOW ME TO READ", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 123:
                if (resultCode == RESULT_OK) {
                    String filePath = data.getData().getPath().split(":")[1];
                    Log.d("FilePath",filePath);
                    this.fileContent = getFileContent(filePath);
                    Log.d("FileContent:",this.fileContent);

                    //TODO aqui eu vou iniciar a próxima tela
                    //TODO refatorar classe para conter botão que abre browser de arquivos
                }
        }
    }

    public String getFileContent(String filePath){
        StringBuilder str = new StringBuilder();
        BufferedReader br;
        try{
            File dir = Environment.getExternalStorageDirectory();
            File file = new File(dir + File.separator + filePath);
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while((line = br.readLine()) != null) {
                str.append(line);
                str.append("\n");
            }
            br.close();
        }
        catch(Exception ex){
            Log.d("ExceptionDoFileContent:",ex.getMessage());
            return "";
        }
        return str.toString();
    }
}

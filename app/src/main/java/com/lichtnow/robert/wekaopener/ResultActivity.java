package com.lichtnow.robert.wekaopener;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.xml.transform.Result;

import udc.edu.weka.attribute.Attribute;
import udc.edu.weka.classifiers.Classifier;

public class ResultActivity extends AppCompatActivity {

    protected Classifier cls = null;
    protected double[] resultados = null;

    protected LinearLayout ll = null;
    protected TextView content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        this.cls = loadClassifier(getIntent().getStringExtra("file"));
        this.resultados = getIntent().getDoubleArrayExtra("resultados");

        this.ll = this.findViewById(R.id.layout);
        this.ll.setBackgroundColor(Color.LTGRAY);
        this.content = this.findViewById(R.id.content);
        this.content.setText(this.createResultText());

    }

    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }

    private Classifier loadClassifier(String filePath){
        File dir = Environment.getExternalStorageDirectory();
        filePath = new File(dir + File.separator + filePath).getAbsolutePath();
        Classifier cls = null;
        try{
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);
            cls = (Classifier) in.readObject();
        }
        catch(Exception ex){
            Toast.makeText(ResultActivity.this,"Não foi possível ler o arquivo",Toast.LENGTH_SHORT).show();
            Log.d("EXCEPTION",ex.getMessage());
        }
        return cls;
    }

    private String createResultText(){
        StringBuilder sb = new StringBuilder();

        sb.append("Modelo: ");
        sb.append(this.cls.getClass().getSimpleName());
        sb.append("\n\n");
        sb.append("Resultados\n");
        List<String> possibleValues = this.cls.getAttributes().get(this.cls.getClassIndex()).getPossibleValues();
        DecimalFormat df = new DecimalFormat("#0.00%");
        for(int i=0;i<possibleValues.size();i++){
            sb.append(possibleValues.get(i));
            sb.append(": ");
            sb.append(df.format(resultados[i]));
            if(i != possibleValues.size()-1)
                sb.append("\n");
        }

        return sb.toString();
    }
}

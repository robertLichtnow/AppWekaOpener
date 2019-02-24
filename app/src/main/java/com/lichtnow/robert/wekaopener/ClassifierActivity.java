package com.lichtnow.robert.wekaopener;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import udc.edu.weka.classifiers.Classifier;

public class ClassifierActivity extends AppCompatActivity {

    protected ListView camposList = null;
    protected Classifier cls = null;
    protected TextView modelNameTxt = null;
    protected ArrayList<String> listaInputs = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);
        this.cls = (Classifier) getIntent().getSerializableExtra("cls");

        if(this.cls == null){
            Toast.makeText(ClassifierActivity.this, "Não foi possível ler o modelo", Toast.LENGTH_SHORT).show();
        }

        this.camposList = this.findViewById(R.id.camposInput);
        this.modelNameTxt = this.findViewById(R.id.modelNameTxt);
        this.modelNameTxt.setText(this.cls.getClass().getSimpleName());

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listaInputs);
        this.camposList.setAdapter(adapter);

        for(int i=0; i<10; i++){
            adapter.add("Rodei: " + i);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onNavigateUp(){
        this.cls = null;
        this.listaInputs = null;
        finish();
        return true;
    }

}

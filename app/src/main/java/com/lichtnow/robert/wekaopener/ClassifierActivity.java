package com.lichtnow.robert.wekaopener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import udc.edu.weka.attribute.Attribute;
import udc.edu.weka.attribute.AttributeType;
import udc.edu.weka.attribute.DenseAttribute;
import udc.edu.weka.attribute.DenseInstance;
import udc.edu.weka.classifiers.Classifier;

public class ClassifierActivity extends AppCompatActivity {

    protected LinearLayout camposList = null;
    protected Classifier cls = null;
    protected String fileName = "";
    protected String filePath = "";
    protected TextView modelNameTxt = null;
    protected TextView fileNameTxt = null;
    private TextWatcher tw = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);

        this.cls = this.loadClassifier(getIntent().getStringExtra("cls"));
        if(cls == null){
            finish();
        }
        this.filePath = getIntent().getStringExtra("cls");
        this.fileName = getIntent().getStringExtra("file");

        if(this.cls == null){
            Toast.makeText(ClassifierActivity.this, "Não foi possível ler o modelo", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.camposList = this.findViewById(R.id.camposInput);
        this.modelNameTxt = this.findViewById(R.id.modelNameTxt);
        this.fileNameTxt = this.findViewById(R.id.fileNameTxt);
        this.modelNameTxt.setText(this.cls.getClass().getSimpleName());
        this.fileNameTxt.setText(this.fileName);

        this.loadAtributos();

    }

    private void loadAtributos(){
        List<Attribute> atributos = this.cls.getAttributes();
        for(int i=0;i<atributos.size();i++){
            if(cls.getClassIndex() != i) {
                TextView tv = new TextView(this);
                tv.setWidth(this.camposList.getWidth());
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setText(atributos.get(i).getName());
                tv.setTextSize(17);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                llp.setMargins(0, i == 0 ? 0 : 15, 0, 0);
                tv.setLayoutParams(llp);
                this.camposList.addView(tv);

                if (atributos.get(i).getAttributeType().equals(AttributeType.NOMINAL)) {
                    Spinner s = new Spinner(this);
                    s.setMinimumWidth(this.camposList.getWidth());
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, atributos.get(i).getPossibleValues());
                    s.setAdapter(spinnerArrayAdapter);
                    s.setBackgroundResource(R.drawable.spinner_background);
                    this.camposList.addView(s);
                } else {
                    EditText et = new EditText(this);
                    et.setWidth(this.camposList.getWidth());
                    et.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    et.setText("0");
                    tw = new TextWatcher() {
                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            if (s.length() > 0 && !s.toString().equals("-")) {
                                StringBuilder sb = new StringBuilder(s.length());
                                sb.append(s);
                                try {
                                    Double.parseDouble(sb.toString());
                                } catch (NumberFormatException ex) {
                                    et.setText(s.toString().substring(0, s.length() - 1));
                                    et.setSelection(et.getText().length());
                                }
                            }
                        }
                    };
                    et.addTextChangedListener(tw);
                    this.camposList.addView(et);
                }
            }
        }
    }

    @Override
    public boolean onNavigateUp(){
        this.cls = null;
        finish();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.send_btn:
                int index = 0;
                try{
                    List<DenseAttribute> attrs = new ArrayList<>();
                    for(int i=0; i<this.camposList.getChildCount();i++){
                        if(this.camposList.getChildAt(i).getClass() == Spinner.class){
                            DenseAttribute attr = new DenseAttribute();
                            attr.setType(AttributeType.NOMINAL);
                            Spinner s = (Spinner)this.camposList.getChildAt(i);
                            attr.setNominalValue(s.getSelectedItem().toString());
                            attrs.add(attr);
                        }else if(this.camposList.getChildAt(i).getClass() == EditText.class){
                            DenseAttribute attr = new DenseAttribute();
                            attr.setType(AttributeType.NUMERIC);
                            EditText et = (EditText)this.camposList.getChildAt(i);
                            if(et.getText().toString().trim().equals("")){
                                et.requestFocus();
                                TextView nomeCampo = (TextView)this.camposList.getChildAt(i-1);
                                String nome = nomeCampo.getText().toString();
                                Toast.makeText(ClassifierActivity.this, "Campo " + nome + " vazio", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            double valor = Double.parseDouble(et.getText().toString());
                            attr.setNumericalValue(valor);
                            attrs.add(attr);
                        }
                    }
                    DenseInstance ins = new DenseInstance(1,(DenseAttribute[])attrs.toArray(new DenseAttribute[attrs.size()]),this.cls);
                    DenseAttribute classe = this.cls.classifyInstance(ins);

                    double resultados[] = cls.distributionForInstance(ins);

                    Intent resultActivity = new Intent(this, ResultActivity.class);
                    resultActivity.putExtra("resultados",resultados);
                    resultActivity.putExtra("file",this.filePath);
                    startActivity(resultActivity);

                }catch(Exception ex){
                    Toast.makeText(ClassifierActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
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
            Toast.makeText(ClassifierActivity.this,"Não foi possível ler o arquivo",Toast.LENGTH_SHORT).show();
        }
        return cls;
    }
}

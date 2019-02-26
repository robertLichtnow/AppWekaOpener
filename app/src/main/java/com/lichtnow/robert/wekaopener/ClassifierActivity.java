package com.lichtnow.robert.wekaopener;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import udc.edu.weka.attribute.Attribute;
import udc.edu.weka.attribute.AttributeType;
import udc.edu.weka.classifiers.Classifier;

public class ClassifierActivity extends AppCompatActivity {

    protected LinearLayout camposList = null;
    protected Classifier cls = null;
    protected TextView modelNameTxt = null;
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
        this.cls = (Classifier) getIntent().getSerializableExtra("cls");

        if(this.cls == null){
            Toast.makeText(ClassifierActivity.this, "Não foi possível ler o modelo", Toast.LENGTH_SHORT).show();
        }

        this.camposList = this.findViewById(R.id.camposInput);
        this.modelNameTxt = this.findViewById(R.id.modelNameTxt);
        this.modelNameTxt.setText(this.cls.getClass().getSimpleName());

        List<Attribute> atributos = this.cls.getAttributes();
        for(int i=0;i<atributos.size();i++){
            TextView tv = new TextView(this);
            tv.setWidth(this.camposList.getWidth());
            tv.setMaxLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setText(atributos.get(i).getName());
            tv.setTextSize(17);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            llp.setMargins(0,i == 0? 0 : 15,0,0);
            tv.setLayoutParams(llp);
            this.camposList.addView(tv);

            if(atributos.get(i).getAttributeType().equals(AttributeType.NOMINAL)){
                Spinner s = new Spinner(this);
                s.setMinimumWidth(this.camposList.getWidth());
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, atributos.get(i).getPossibleValues());
                s.setAdapter(spinnerArrayAdapter);
                s.setBackgroundResource(R.drawable.spinner_background);
                this.camposList.addView(s);
            }
            else {
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
                Toast.makeText(ClassifierActivity.this, "Clicou para classificar", Toast.LENGTH_SHORT).show();
                //TODO aqui vem a lógica de classificar instância
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}

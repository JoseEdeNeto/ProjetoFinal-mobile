package br.edu.utfpr.neto.projetofinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

public class CorActivity extends AppCompatActivity {

    private RadioGroup radioGroupCores;
    public static final String COR = "cor";

    private int cor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cor);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        radioGroupCores = findViewById(R.id.radioGroupCores);

        setTitle(getString(R.string.cores));

        lerInformacoes();

        radioGroupCores.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                salvaCor(group.getCheckedRadioButtonId());
            }
        });
    }
    private void lerInformacoes() {
        SharedPreferences shared = getSharedPreferences(COR, Context.MODE_PRIVATE);;
        cor = shared.getInt(COR, R.id.radioButtonAzul);

        setaCor();
    }

    private void setaCor() {
        ActionBar actionBar = getSupportActionBar();
        mudaCor(actionBar, cor);
        radioGroupCores = findViewById(R.id.radioGroupCores);
        radioGroupCores.check(cor);
    }

    private void salvaCor(int corNova){
        SharedPreferences shared = getSharedPreferences(COR, Context.MODE_PRIVATE);;
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(COR, corNova);
        editor.commit();
        cor = corNova;
        ActionBar actionBar = getSupportActionBar();
        mudaCor(actionBar, cor);
    }

    public static void mudaCor(ActionBar actionBar, int idRadio){
        switch (idRadio) {
            case R.id.radioButtonAzul:
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                return;

            case R.id.radioButtonVerde:
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.GREEN));
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                return;

            case R.id.radioButtonVermelho:
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                return;

            default:
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                return;
        }
    }
}

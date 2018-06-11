package br.edu.utfpr.neto.projetofinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SobreActivity extends AppCompatActivity {

    private int cor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sobre);
        setTitle(getString(R.string.sobre));

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        lerInformacoes();
        CorActivity.mudaCor(actionBar, cor);
    }

    private void lerInformacoes() {
        SharedPreferences sharedCor = getSharedPreferences(CorActivity.COR, Context.MODE_PRIVATE);

        cor = sharedCor.getInt(CorActivity.COR, R.id.radioButtonAzul);
    }
}

package br.edu.utfpr.neto.projetofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.neto.projetofinal.modelo.Estado;
import br.edu.utfpr.neto.projetofinal.modelo.Plataforma;
import br.edu.utfpr.neto.projetofinal.persistencia.DatabaseHelper;
import br.edu.utfpr.neto.projetofinal.utils.UtilsGUI;

public class PlataformaActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;
    private static final String ARQUIVO_NOVO = "shared_novo";
    private static final String ARQUIVO_ALTERAR = "shared_alterar";
    private static final String NOME = Plataforma.NOME;
    private int cor;

    private EditText editTextNomePlataforma;

    private int  modo;
    private Plataforma plataforma;
    private String nome;

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, PlataformaActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Plataforma plataforma){

        Intent intent = new Intent(activity, PlataformaActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, plataforma.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plataforma);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNomePlataforma = findViewById(R.id.editTextNomePlataforma);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO);

        editTextNomePlataforma.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                salvarInformacaoNome(editTextNomePlataforma.getText().toString());
            }
        });

        if (modo == ALTERAR){

            int id = bundle.getInt(ID);

            try {

                DatabaseHelper conexao = DatabaseHelper.getInstance(this);
                plataforma =  conexao.getPlataformaDao().queryForId(id);

                editTextNomePlataforma.setText(plataforma.getNome());

            } catch (SQLException e) {
                e.printStackTrace();
            }

            setTitle(R.string.alterar_plataforma);

            lerInformacoes();
            CorActivity.mudaCor(actionBar, cor);

        }else{

            plataforma = new Plataforma();

            setTitle(R.string.nova_plataforma);

            lerInformacoes();
            CorActivity.mudaCor(actionBar, cor);

        }
    }

    private void salvarInformacaoNome(String novoNome) {
        SharedPreferences shared;
        if (modo == ALTERAR){
            shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);

        } else {
            shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = shared.edit();

        editor.putString(NOME, novoNome);

        editor.commit();

        nome = novoNome;
    }

    private void lerInformacoes() {
        SharedPreferences shared;
        if (modo == ALTERAR){
            shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);

        } else {
            shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
        }
        nome = shared.getString(NOME, "");
        SharedPreferences sharedCor = getSharedPreferences(CorActivity.COR, Context.MODE_PRIVATE);

        cor = sharedCor.getInt(CorActivity.COR, R.id.radioButtonAzul);

        setaInformacoesNome();
    }

    private void setaInformacoesNome() {
        editTextNomePlataforma = findViewById(R.id.editTextNomePlataforma);
        editTextNomePlataforma.setText(nome);
    }

    private void salvar(){

        String nome  = UtilsGUI.validaCampoTexto(this,
                editTextNomePlataforma,
                R.string.plataforma_vazia);
        if (nome == null){
            return;
        }

        try {

            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            List<Plataforma> lista = conexao.getPlataformaDao()
                    .queryBuilder()
                    .where().eq(Plataforma.NOME, nome)
                    .query();

            if (modo == NOVO) {

                if (lista.size() > 0){
                    UtilsGUI.avisoErro(this, R.string.plataforma_usada);
                    return;
                }

                plataforma.setNome(nome);

                conexao.getPlataformaDao().create(plataforma);

                SharedPreferences shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.clear();
                editor.commit();

            } else {

                if (!nome.equals(plataforma.getNome())){

                    if (lista.size() >= 1){
                        UtilsGUI.avisoErro(this, R.string.plataforma_usada);
                        return;
                    }

                    plataforma.setNome(nome);

                    conexao.getPlataformaDao().update(plataforma);

                    SharedPreferences shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.clear();
                    editor.commit();
                }
            }

            setResult(Activity.RESULT_OK);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

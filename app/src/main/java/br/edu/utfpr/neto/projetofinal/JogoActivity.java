package br.edu.utfpr.neto.projetofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.neto.projetofinal.modelo.Estado;
import br.edu.utfpr.neto.projetofinal.modelo.Jogo;
import br.edu.utfpr.neto.projetofinal.modelo.Plataforma;
import br.edu.utfpr.neto.projetofinal.persistencia.DatabaseHelper;
import br.edu.utfpr.neto.projetofinal.utils.UtilsGUI;

public class JogoActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;
    private static final String ARQUIVO_NOVO = "shared_novo";
    private static final String ARQUIVO_ALTERAR = "shared_alterar";
    private static final String NOME = Jogo.NOME;
    private static final String PRECO = Jogo.PRECO;
    private static final String ESTADO = Jogo.ESTADO_ID;
    private static final String PLATAFORMA = Jogo.PLATAFORMA_ID;
    private static final String MIDIA = Jogo.MIDIA;

    private EditText editTextNome;
    private EditText editTextPreco;
    private Spinner spinnerPlataforma;
    private Spinner spinnerEstado;
    private Spinner spinnerMidia;

    private List<Plataforma> listaPlataforma;
    private List<Estado> listaEstado;

    private int modo;
    private Jogo jogo;
    private String nome;
    private float preco;
    private int estado;
    private int plataforma;
    private int cor;
    private int midia;

    public static void novo(Activity activity, int requestCode){

        Intent intent = new Intent(activity, JogoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Jogo jogo){

        Intent intent = new Intent(activity, JogoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, jogo.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNome = findViewById(R.id.editTextNomeJogo);
        editTextPreco = findViewById(R.id.editTextPreco);
        spinnerPlataforma = findViewById(R.id.spinnerPlataforma);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerMidia = findViewById(R.id.spinnerMidia);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        popularSpinner();

        modo = bundle.getInt(MODO);

        editTextNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                salvarInformacaoNome(editTextNome.getText().toString());
            }
        });

        editTextPreco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editTextPreco.getText().toString().equals("")) {
                    salvarInformacaoPreco(editTextPreco.getText().toString());
                }
            }
        });

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                salvarInformacaoEstado(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPlataforma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                salvarInformacaoPlataforma(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMidia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                salvarInformacaoMidia(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (modo == ALTERAR){

            int id = bundle.getInt(ID);

            try {
                DatabaseHelper conexao = DatabaseHelper.getInstance(this);

                jogo = conexao.getJogoDao().queryForId(id);

                editTextNome.setText(jogo.getNome());
                editTextPreco.setText(String.valueOf(jogo.getPreco()));

                conexao.getEstadoDao().refresh(jogo.getEstado());
                conexao.getPlataformaDao().refresh(jogo.getPlataforma());
                conexao.getEstadoDao().refresh(jogo.getEstado());

            } catch (SQLException e) {
                e.printStackTrace();
            }

            int posicao1 = posicaoEstado(jogo.getEstado());
            spinnerEstado.setSelection(posicao1);
            int posicao2 = posicaoPlataforma(jogo.getPlataforma());
            spinnerPlataforma.setSelection(posicao2);

            lerInformacoes();
            CorActivity.mudaCor(actionBar, cor);

            setTitle(R.string.alterar_jogo);

        }else{

            jogo = new Jogo();

            setTitle(R.string.novo_jogo);

            lerInformacoes();
            CorActivity.mudaCor(actionBar, cor);

        }
    }

    private void salvarInformacaoMidia(int position) {
        SharedPreferences shared;
        if (modo == ALTERAR){
            shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);

        } else {
            shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(MIDIA, position);

        editor.commit();

        midia = position;
    }

    private void salvarInformacaoPlataforma(int position) {
        SharedPreferences shared;
        if (modo == ALTERAR){
            shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);

        } else {
            shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(PLATAFORMA, position);

        editor.commit();

        plataforma = position;
    }

    private void salvarInformacaoEstado(int position) {
        SharedPreferences shared;
        if (modo == ALTERAR){
            shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);

        } else {
            shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(ESTADO, position);

        editor.commit();

        estado = position;
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

    private void salvarInformacaoPreco(String novoPreco) {
        SharedPreferences shared;
        if (modo == ALTERAR){
            shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);

        } else {
            shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = shared.edit();

        editor.putString(PRECO, novoPreco);

        editor.commit();

        preco = Float.parseFloat(novoPreco);
    }

    private void lerInformacoes() {
        SharedPreferences shared;
        if (modo == ALTERAR){
            shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);

        } else {
            shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
        }
        nome = shared.getString(NOME, "");
        preco = Float.parseFloat(shared.getString(PRECO, "0.0"));
        estado = shared.getInt(ESTADO, 0);
        plataforma = shared.getInt(PLATAFORMA, 0);
        midia = shared.getInt(MIDIA, 0);

        setaInformacoesNome();
        setaInformacoesPreco();
        setaInformacoesEstado();
        setaInformacoesPlataforma();
        setaInformacoesMidia();

        SharedPreferences sharedCor = getSharedPreferences(CorActivity.COR, Context.MODE_PRIVATE);

        cor = sharedCor.getInt(CorActivity.COR, R.id.radioButtonAzul);
    }

    private void setaInformacoesMidia() {
        spinnerMidia = findViewById(R.id.spinnerMidia);
        spinnerMidia.setSelection(midia);
    }

    private void setaInformacoesPlataforma() {
        spinnerPlataforma = findViewById(R.id.spinnerPlataforma);
        spinnerPlataforma.setSelection(plataforma);
    }

    private void setaInformacoesEstado() {
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerEstado.setSelection(estado);
    }

    private void setaInformacoesPreco() {
        editTextPreco = findViewById(R.id.editTextPreco);
        editTextPreco.setText(String.valueOf(preco));
    }

    private void setaInformacoesNome() {
        editTextNome = findViewById(R.id.editTextNomeJogo);
        editTextNome.setText(nome);
    }

    private int posicaoEstado(Estado estado){

        for (int pos = 0; pos < listaEstado.size(); pos++){

            Estado e = listaEstado.get(pos);

            if (e.getId() == estado.getId()){
                return pos;
            }
        }

        return -1;
    }

    private int posicaoPlataforma(Plataforma plataforma){

        for (int pos = 0; pos < listaPlataforma.size(); pos++){

            Plataforma p = listaPlataforma.get(pos);

            if (p.getId() == plataforma.getId()){
                return pos;
            }
        }

        return -1;
    }

    private void popularSpinner(){

        listaEstado = null;
        listaPlataforma = null;
        List<String> listaMidia = new ArrayList<>();
        listaMidia.add(getResources().getString(R.string.digital));
        listaMidia.add(getResources().getString(R.string.fisica));

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            listaEstado = conexao.getEstadoDao()
                    .queryBuilder()
                    .orderBy(Estado.NOME, true)
                    .query();
            listaPlataforma = conexao.getPlataformaDao()
                    .queryBuilder()
                    .orderBy(Plataforma.NOME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayAdapter<Estado> spinnerAdapterEstado = new ArrayAdapter<Estado>(this,
                android.R.layout.simple_list_item_1,
                listaEstado);

        spinnerEstado.setAdapter(spinnerAdapterEstado);

        ArrayAdapter<Plataforma> spinnerAdapterPlataforma = new ArrayAdapter<Plataforma>(this,
                android.R.layout.simple_list_item_1,
                listaPlataforma);

        spinnerPlataforma.setAdapter(spinnerAdapterPlataforma);

        ArrayAdapter<String> stringAdapterMidia = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMidia);

        spinnerMidia.setAdapter(stringAdapterMidia);
    }

    private void salvar(){
        String nome  = UtilsGUI.validaCampoTexto(this,
                editTextNome,
                R.string.nome_vazio);
        if (nome == null){
            return;
        }

        String txtPreco = UtilsGUI.validaCampoTexto(this,
                editTextPreco,
                R.string.preco_vazio);
        if (txtPreco == null){
            return;
        }

        float preco = Float.parseFloat(txtPreco);

        if (preco <= 0){
            UtilsGUI.avisoErro(this, R.string.preco_invalido);
            editTextPreco.requestFocus();
            return;
        }

        jogo.setNome(nome);
        jogo.setPreco(preco);

        Estado estado = (Estado) spinnerEstado.getSelectedItem();
        if (estado == null){
            UtilsGUI.avisoErro(this,R.string.estado_vazio);
            return;
        }
        jogo.setEstado(estado);

        Plataforma plataforma = (Plataforma) spinnerPlataforma.getSelectedItem();
        if (plataforma == null){
            UtilsGUI.avisoErro(this,R.string.plataforma_vazia);
            return;
        }
        jogo.setPlataforma(plataforma);

        String midia = (String) spinnerMidia.getSelectedItem();
        if (midia == null){
            UtilsGUI.avisoErro(this,R.string.midia_vazia);
            return;
        }
        jogo.setMidia(midia);

        try {

            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            if (modo == NOVO) {

                conexao.getJogoDao().create(jogo);
                SharedPreferences shared = getSharedPreferences(ARQUIVO_NOVO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.clear();
                editor.commit();

            } else {

                conexao.getJogoDao().update(jogo);
                SharedPreferences shared = getSharedPreferences(ARQUIVO_ALTERAR, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.clear();
                editor.commit();

            }

            setResult(Activity.RESULT_OK);
            finish();

        } catch (SQLException e) {
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

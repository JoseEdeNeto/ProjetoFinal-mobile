package br.edu.utfpr.neto.projetofinal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.neto.projetofinal.modelo.Estado;
import br.edu.utfpr.neto.projetofinal.modelo.Jogo;
import br.edu.utfpr.neto.projetofinal.persistencia.DatabaseHelper;
import br.edu.utfpr.neto.projetofinal.utils.UtilsGUI;

public class EstadosActivity extends AppCompatActivity {

    private ListView listViewEstados;
    private ArrayAdapter<Estado> listaAdapter;

    private static final int REQUEST_NOVO_ESTADO    = 1;
    private static final int REQUEST_ALTERAR_ESTADO = 2;

    private int cor;

    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, EstadosActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewEstados = findViewById(R.id.listViewItens);

        listViewEstados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Estado estado = (Estado) parent.getItemAtPosition(position);

                EstadoActivity.alterar(EstadosActivity.this,
                        REQUEST_ALTERAR_ESTADO,
                        estado);
            }
        });

        popularLista();

        registerForContextMenu(listViewEstados);

        setTitle(R.string.estados);

        lerInformacoes();
        CorActivity.mudaCor(actionBar, cor);
    }

    private void lerInformacoes() {
        SharedPreferences sharedCor = getSharedPreferences(CorActivity.COR, Context.MODE_PRIVATE);

        cor = sharedCor.getInt(CorActivity.COR, R.id.radioButtonAzul);
    }

    private void popularLista(){

        List<Estado> lista = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            lista = conexao.getEstadoDao()
                    .queryBuilder()
                    .orderBy(Estado.NOME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Estado>(this,
                android.R.layout.simple_list_item_1,
                lista);

        listViewEstados.setAdapter(listaAdapter);
    }

    private void excluirEstado(final Estado estado){

        try {

            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            List<Jogo> lista = conexao.getJogoDao()
                    .queryBuilder()
                    .where().eq(Jogo.ESTADO_ID, estado.getId())
                    .query();

            if (lista != null && lista.size() > 0){
                UtilsGUI.avisoErro(this, R.string.estado_usado);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + estado.getNome();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    DatabaseHelper conexao =
                                            DatabaseHelper.getInstance(EstadosActivity.this);

                                    conexao.getEstadoDao().delete(estado);

                                    listaAdapter.remove(estado);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_NOVO_ESTADO || requestCode == REQUEST_ALTERAR_ESTADO)
                && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_estados, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                EstadoActivity.novo(this, REQUEST_NOVO_ESTADO);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Estado estado = (Estado) listViewEstados.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                EstadoActivity.alterar(this,
                        REQUEST_ALTERAR_ESTADO,
                        estado);
                return true;

            case R.id.menuItemApagar:
                excluirEstado(estado);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}

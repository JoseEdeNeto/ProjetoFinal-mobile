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

import br.edu.utfpr.neto.projetofinal.modelo.Jogo;
import br.edu.utfpr.neto.projetofinal.modelo.Plataforma;
import br.edu.utfpr.neto.projetofinal.persistencia.DatabaseHelper;
import br.edu.utfpr.neto.projetofinal.utils.UtilsGUI;

public class PlataformasActivity extends AppCompatActivity{

    private ListView listViewPlataformas;
    private ArrayAdapter<Plataforma> listaAdapter;

    private static final int REQUEST_NOVA_PLATAFORMA    = 1;
    private static final int REQUEST_ALTERAR_PLATAFORMA = 2;

    private int cor;

    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, PlataformasActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewPlataformas = findViewById(R.id.listViewItens);

        listViewPlataformas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Plataforma plataforma = (Plataforma) parent.getItemAtPosition(position);

                PlataformaActivity.alterar(PlataformasActivity.this,
                        REQUEST_ALTERAR_PLATAFORMA,
                        plataforma);
            }
        });

        popularLista();

        registerForContextMenu(listViewPlataformas);

        setTitle(R.string.plataformas);

        lerInformacoes();
        CorActivity.mudaCor(actionBar, cor);
    }

    private void lerInformacoes() {
        SharedPreferences sharedCor = getSharedPreferences(CorActivity.COR, Context.MODE_PRIVATE);

        cor = sharedCor.getInt(CorActivity.COR, R.id.radioButtonAzul);
    }

    private void popularLista(){

        List<Plataforma> lista = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            lista = conexao.getPlataformaDao()
                    .queryBuilder()
                    .orderBy(Plataforma.NOME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Plataforma>(this,
                android.R.layout.simple_list_item_1,
                lista);

        listViewPlataformas.setAdapter(listaAdapter);
    }

    private void excluirPlataforma(final Plataforma plataforma){

        try {

            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            List<Jogo> lista = conexao.getJogoDao()
                    .queryBuilder()
                    .where().eq(Jogo.PLATAFORMA_ID, plataforma.getId())
                    .query();

            if (lista != null && lista.size() > 0){
                UtilsGUI.avisoErro(this, R.string.plataforma_usada);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + plataforma.getNome();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    DatabaseHelper conexao =
                                            DatabaseHelper.getInstance(PlataformasActivity.this);

                                    conexao.getPlataformaDao().delete(plataforma);

                                    listaAdapter.remove(plataforma);

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

        if ((requestCode == REQUEST_NOVA_PLATAFORMA || requestCode == REQUEST_ALTERAR_PLATAFORMA)
                && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_plataformas, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                PlataformaActivity.novo(this, REQUEST_NOVA_PLATAFORMA);
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

        Plataforma plataforma = (Plataforma) listViewPlataformas.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                PlataformaActivity.alterar(this,
                        REQUEST_ALTERAR_PLATAFORMA,
                        plataforma);
                return true;

            case R.id.menuItemApagar:
                excluirPlataforma(plataforma);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}

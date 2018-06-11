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

public class JogosActivity extends AppCompatActivity {

    private ListView listViewJogo;
    private ArrayAdapter<Jogo> listaAdapter;

    private static final int REQUEST_NOVO_JOGO    = 1;
    private static final int REQUEST_ALTERAR_JOGO = 2;

    private int cor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        final ActionBar actionBar = getSupportActionBar();

        listViewJogo = findViewById(R.id.listViewItens);

        listViewJogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Jogo jogo = (Jogo) parent.getItemAtPosition(position);

                JogoActivity.alterar(JogosActivity.this,
                        REQUEST_ALTERAR_JOGO,
                        jogo);
            }
        });

        popularLista();

        registerForContextMenu(listViewJogo);

        lerInformacoes();
        CorActivity.mudaCor(actionBar, cor);
    }

    private void lerInformacoes() {
        SharedPreferences sharedCor = getSharedPreferences(CorActivity.COR, Context.MODE_PRIVATE);

        cor = sharedCor.getInt(CorActivity.COR, R.id.radioButtonAzul);
    }

    private void popularLista(){

        List<Jogo> lista = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            lista = conexao.getJogoDao()
                    .queryBuilder()
                    .orderBy(Jogo.NOME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Jogo>(this,
                android.R.layout.simple_list_item_1,
                lista);

        listViewJogo.setAdapter(listaAdapter);
    }

    private void excluirJogo(final Jogo jogo){

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + jogo.getNome();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    DatabaseHelper conexao =
                                            DatabaseHelper.getInstance(JogosActivity.this);

                                    conexao.getJogoDao().delete(jogo);

                                    listaAdapter.remove(jogo);

                                } catch (SQLException e) {
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

        if ((requestCode == REQUEST_NOVO_JOGO || requestCode == REQUEST_ALTERAR_JOGO)
                && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_jogos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                JogoActivity.novo(this, REQUEST_NOVO_JOGO);
                return true;

            case R.id.menuItemEstado:
                EstadosActivity.abrir(this);
                return true;

            case R.id.menuItemPlataforma:
                PlataformasActivity.abrir(this);
                return true;

            case R.id.menuItemSobre:
                chamarSobreActivity(findViewById(R.id.all));

            case R.id.menuItemPreferencias:
                chamarPreferenciasActivity(findViewById(R.id.all));

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chamarPreferenciasActivity(View viewById) {
        Intent intent = new Intent(this, CorActivity.class);
        startActivity(intent);
    }

    private void chamarSobreActivity(View view) {
        Intent intent = new Intent(this, SobreActivity.class);
        startActivity(intent);
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

        Jogo jogo = (Jogo) listViewJogo.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                JogoActivity.alterar(this,
                        REQUEST_ALTERAR_JOGO,
                        jogo);
                return true;

            case R.id.menuItemApagar:
                excluirJogo(jogo);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

}

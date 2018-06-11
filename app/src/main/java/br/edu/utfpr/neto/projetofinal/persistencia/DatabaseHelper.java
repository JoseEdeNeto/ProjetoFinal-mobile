package br.edu.utfpr.neto.projetofinal.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.neto.projetofinal.R;
import br.edu.utfpr.neto.projetofinal.modelo.Estado;
import br.edu.utfpr.neto.projetofinal.modelo.Jogo;
import br.edu.utfpr.neto.projetofinal.modelo.Plataforma;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME    = "jogos.db";
    private static final int    DB_VERSION = 1;

    private static DatabaseHelper instance;

    private Context context;
    private Dao<Estado, Integer> estadoDao;
    private Dao<Plataforma, Integer> plataformaDao;
    private Dao<Jogo, Integer> jogoDao;

    public static DatabaseHelper getInstance(Context contexto){

        if (instance == null){
            instance = new DatabaseHelper(contexto);
        }

        return instance;
    }
    
    private DatabaseHelper(Context contexto) {
        super(contexto, DB_NAME, null, DB_VERSION);
        context = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, Estado.class);

            String[] tiposBasicos = context.getResources().getStringArray(R.array.estados);

            List<Estado> lista = new ArrayList<Estado>();

            for(int cont = 0; cont < tiposBasicos.length; cont++){

                Estado estado = new Estado(tiposBasicos[cont]);
                lista.add(estado);
            }

            getEstadoDao().create(lista);

            TableUtils.createTable(connectionSource, Jogo.class);

            TableUtils.createTable(connectionSource, Plataforma.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "onCreate", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            TableUtils.dropTable(connectionSource, Jogo.class, true);
            TableUtils.dropTable(connectionSource, Estado.class, true);
            TableUtils.dropTable(connectionSource, Plataforma.class, true);

            onCreate(database, connectionSource);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Jogo, Integer> getJogoDao() throws SQLException {

        if (jogoDao == null) {
            jogoDao = getDao(Jogo.class);
        }
        
        return jogoDao;
    }

    public Dao<Estado, Integer> getEstadoDao() throws SQLException {

        if (estadoDao == null) {
            estadoDao = getDao(Estado.class);
        }

        return estadoDao;
    }

    public Dao<Plataforma, Integer> getPlataformaDao() throws SQLException {

        if (plataformaDao == null) {
            plataformaDao = getDao(Plataforma.class);
        }

        return plataformaDao;
    }
}
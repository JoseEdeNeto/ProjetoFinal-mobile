package br.edu.utfpr.neto.projetofinal.modelo;

import android.provider.Settings;

import com.j256.ormlite.field.DatabaseField;

import br.edu.utfpr.neto.projetofinal.R;

public class Estado {

    public static final String ID = "id_estado";
    public static final String NOME = "nome_estado";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;

    @DatabaseField(canBeNull = false, unique = true, columnName = NOME)
    private String nome;

    public Estado(){}

    public Estado(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}

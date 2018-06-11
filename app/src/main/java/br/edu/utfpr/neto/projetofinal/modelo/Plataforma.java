package br.edu.utfpr.neto.projetofinal.modelo;

import com.j256.ormlite.field.DatabaseField;

public class Plataforma {

    public static final String ID = "id_plataforma";
    public static final String NOME = "nome_plataforma";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;

    @DatabaseField(canBeNull = false, unique = true, columnName = NOME)
    private String nome;

    public Plataforma(){}

    public Plataforma(String nome) {
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

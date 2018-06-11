package br.edu.utfpr.neto.projetofinal.modelo;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;

import br.edu.utfpr.neto.projetofinal.R;
import br.edu.utfpr.neto.projetofinal.persistencia.DatabaseHelper;

public class Jogo {

    public static final String ID = "id_jogo";
    public static final String NOME = "nome_jogo";
    public static final String PRECO = "preco";
    public static final String MIDIA = "midia";
    public static final String ESTADO_ID = "estado_id";
    public static final String PLATAFORMA_ID = "plataforma_id";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;

    @DatabaseField(canBeNull = false, columnName = NOME)
    private String nome;

    @DatabaseField(canBeNull = false, columnName = PRECO)
    private float preco;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Estado estado;

    // Veja as opções foreignAutoCreate e foreignAutoRefresh no ORMLite
    // O nome da chave estrangeira gerada automática pelo ORMLite também será TABELA_ESTRANGERIA_id
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Plataforma plataforma;

    @DatabaseField(canBeNull = false, columnName = MIDIA)
    private String midia;

    public Jogo(){
        // ORMLite necessita de um construtor sem parâmetros
    }

    public Jogo(String nome, float preco, Estado estado, Plataforma plataforma, String midia) {
        this.nome = nome;
        this.preco = preco;
        this.estado = estado;
        this.plataforma = plataforma;
        this.midia = midia;
    }

    public void setMidia(String midia) {
        this.midia = midia;
    }

    public String getMidia() {
        return midia;
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

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Plataforma getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    @Override
    public String toString() {
        return "'" + this.nome + "'" +
                ", " + "=" + this.preco + "\n" +
                this.estado.getNome() +
                ", " +this.plataforma.getNome() +
                ", " + this.midia;
    }
}

package pdasolucoes.com.br.homevacation.Model;

import java.io.Serializable;

/**
 * Created by PDA on 04/10/2017.
 */

public class Ambiente implements Serializable {

    private int id;
    private String descricao;
    private int ordem;
    private int idCasa;
    private int itens;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(int idCasa) {
        this.idCasa = idCasa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getItens() {
        return itens;
    }

    public void setItens(int itens) {
        this.itens = itens;
    }
}

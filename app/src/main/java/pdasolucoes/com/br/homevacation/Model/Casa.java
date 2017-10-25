package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 25/10/2017.
 */

public class Casa {

    private int id;
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

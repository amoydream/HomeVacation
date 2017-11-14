package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 08/11/2017.
 */

public class Categoria {

    private int idCategoria;
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

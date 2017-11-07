package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 11/10/2017.
 */

public class Questao {


    private int id;//ID_Item
    private int idAmbiente;//ID_Ambiente
    private String descricao;//questao
    private String evidencia;//Evidencia
    private int idUsuario;//ID_Usuario

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

    public int getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(int idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}

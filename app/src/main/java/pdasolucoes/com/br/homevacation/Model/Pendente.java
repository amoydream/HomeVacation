package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 21/12/2017.
 */

public class Pendente {

    private String descricao;
    private String subDescricao;
    private int idChecklist;
    private int idAmbiente;
    private int idAmbienteItem;
    private String tipo;


    //resposta pendencia
    private String descricaoRes;
    private String caminhoFoto;
    private int qtde;

    public String getDescricaoRes() {
        return descricaoRes;
    }

    public void setDescricaoRes(String descricaoRes) {
        this.descricaoRes = descricaoRes;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public int getQtde() {
        return qtde;
    }

    public void setQtde(int qtde) {
        this.qtde = qtde;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSubDescricao() {
        return subDescricao;
    }

    public void setSubDescricao(String subDescricao) {
        this.subDescricao = subDescricao;
    }

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    public int getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(int idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    public int getIdAmbienteItem() {
        return idAmbienteItem;
    }

    public void setIdAmbienteItem(int idAmbienteItem) {
        this.idAmbienteItem = idAmbienteItem;
    }
}

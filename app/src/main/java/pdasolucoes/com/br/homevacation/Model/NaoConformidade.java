package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 08/11/2017.
 */

public class NaoConformidade {

    private int idItemQuestao;
    private String descricao;
    private int valorColetado;
    private int valorEsperado;
    private String descAmbiente;
    private String acao;

    public int getIdItemQuestao() {
        return idItemQuestao;
    }

    public void setIdItemQuestao(int idItemQuestao) {
        this.idItemQuestao = idItemQuestao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getValorColetado() {
        return valorColetado;
    }

    public void setValorColetado(int valorColetado) {
        this.valorColetado = valorColetado;
    }

    public int getValorEsperado() {
        return valorEsperado;
    }

    public void setValorEsperado(int valorEsperado) {
        this.valorEsperado = valorEsperado;
    }

    public String getDescAmbiente() {
        return descAmbiente;
    }

    public void setDescAmbiente(String descAmbiente) {
        this.descAmbiente = descAmbiente;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }
}

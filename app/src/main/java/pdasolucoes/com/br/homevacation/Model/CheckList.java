package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 12/10/2017.
 */

public class CheckList {


    private int id;
    private String ambiente;
    private int ambienteOrdem;
    private String item;
    private int idAmbiente;
    private String rfid;
    private String epc;
    private String evidencia;
    private int estoque;
    private String dataAbertura;
    private String categoria;
    private int idCasaItem;

    public int getIdCasaItem() {
        return idCasaItem;
    }

    public void setIdCasaItem(int idCasaItem) {
        this.idCasaItem = idCasaItem;
    }

    public int getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(int idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public int getAmbienteOrdem() {
        return ambienteOrdem;
    }

    public void setAmbienteOrdem(int ambienteOrdem) {
        this.ambienteOrdem = ambienteOrdem;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}

package pdasolucoes.com.br.homevacation.Model;

/**
 * Created by PDA on 26/10/2017.
 */

public class EPC {

    private int codigo;
    private String epc;

    public EPC() {
    }

    public EPC(int codigo, String epc) {
        this.codigo = codigo;
        this.epc = epc;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }
}

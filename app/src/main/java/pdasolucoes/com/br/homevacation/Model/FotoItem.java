package pdasolucoes.com.br.homevacation.Model;

import java.io.Serializable;

/**
 * Created by PDA on 07/11/2017.
 */

public class FotoItem implements Serializable {

    private int idItem;
    private String caminhoFoto;
    private int idUsuario;
    private byte[] foto;

    public FotoItem() {
        idItem = 0;
        caminhoFoto = "";
        idUsuario = 0;
        foto = null;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}

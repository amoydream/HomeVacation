package pdasolucoes.com.br.homevacation.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by PDA on 15/10/2017.
 */

public class CheckListVolta implements KvmSerializable {

    private int idChecklist;
    private int idAmbienteItem;
    private String rfid;
    private int estoque;
    private int idUsuario;
    private byte[] foto;
    private String caminhoFoto;

    public CheckListVolta() {
        idChecklist = 0;
        idAmbienteItem = 0;
        rfid = "";
        estoque = 0;
        idUsuario = 0;
        foto = null;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    public int getIdAmbienteItem() {
        return idAmbienteItem;
    }

    public void setIdAmbienteItem(int idAmbienteItem) {
        this.idAmbienteItem = idAmbienteItem;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }


    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return idChecklist;
            case 1:
                return idAmbienteItem;
            case 2:
                return rfid;
            case 3:
                return estoque;
            case 4:
                return idUsuario;
            case 5:
                return foto;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 6;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i) {
            case 0:
                idChecklist = Integer.parseInt(o.toString());
                break;
            case 1:
                idAmbienteItem = Integer.parseInt(o.toString());
                break;
            case 2:
                rfid = o.toString();
                break;
            case 3:
                estoque = Integer.parseInt(o.toString());
                break;
            case 4:
                idUsuario = Integer.parseInt(o.toString());
                break;
            case 5:
                foto = new byte[]{Byte.parseByte(o.toString())};
        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_CheckList";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Ambiente_Item";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "RFID";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "Estoque";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Usuario";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Evidencia";
                break;
        }
    }
}

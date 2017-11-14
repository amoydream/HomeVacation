package pdasolucoes.com.br.homevacation.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by PDA on 07/11/2017.
 */

public class FotoItem implements Serializable, KvmSerializable {

    private int idItem;
    private String caminhoFoto;
    private int idUsuario;
    private int idAmbienteItem;
    private byte[] foto;

    public FotoItem() {
        idItem = 0;
        caminhoFoto = "";
        idUsuario = 0;
        idAmbienteItem = 0;
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

    public int getIdAmbienteItem() {
        return idAmbienteItem;
    }

    public void setIdAmbienteItem(int idAmbienteItem) {
        this.idAmbienteItem = idAmbienteItem;
    }

    @Override
    public Object getProperty(int i) {

        switch (i) {
            case 0:
                return idItem;
            case 1:
                return idAmbienteItem;
            case 2:
                return caminhoFoto;
            case 3:
                return foto;
            case 4:
                return idUsuario;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i) {
            case 0:
                idItem = Integer.parseInt(o.toString());
                break;
            case 1:
                idAmbienteItem = Integer.parseInt(o.toString());
                break;
            case 2:
                caminhoFoto = o.toString();
                break;
            case 3:
                foto = new byte[]{Byte.parseByte(o.toString())};
                break;
            case 4:
                idUsuario = Integer.parseInt(o.toString());
                break;
        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Ambiente_Item";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Foto_Path";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Foto_Stream";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Usuario";
                break;
        }
    }
}

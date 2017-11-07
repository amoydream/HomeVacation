package pdasolucoes.com.br.homevacation.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by PDA on 01/11/2017.
 */

public class FotoAmbiente implements Serializable, KvmSerializable {

    private int id;
    private int idAmbiente;
    private int idCasa;
    private String caminhoFoto;
    private int idUsuario;
    private String fotoRetornoString;
    private byte[] fotoStream;
    private Bitmap bitmap;

    public FotoAmbiente() {
        id = 0;
        idAmbiente = 0;
        caminhoFoto = "";
        idUsuario = 0;
        fotoStream = null;
    }

    public String getFotoRetornoString() {
        return fotoRetornoString;
    }

    public void setFotoRetornoString(String fotoRetornoString) {
        this.fotoRetornoString = fotoRetornoString;
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

    public int getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(int idCasa) {
        this.idCasa = idCasa;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public byte[] getFotoStream() {
        return fotoStream;
    }

    public void setFotoStream(byte[] fotoStream) {
        this.fotoStream = fotoStream;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Object getProperty(int i) {

        switch (i) {
            case 0:
                return idAmbiente;
            case 1:
                return id;
            case 2:
                return caminhoFoto;
            case 3:
                return fotoStream;
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
                idAmbiente = Integer.parseInt(o.toString());
                break;
            case 1:
                id = Integer.parseInt(o.toString());
                break;
            case 2:
                caminhoFoto = o.toString();
                break;
            case 3:
                fotoStream = new byte[]{Byte.parseByte(o.toString())};
                break;
            case 4:
                idUsuario = Integer.parseInt(o.toString());

        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {


        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Ambiente";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID";
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

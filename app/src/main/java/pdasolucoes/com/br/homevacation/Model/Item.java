package pdasolucoes.com.br.homevacation.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by PDA on 05/10/2017.
 */

public class Item implements KvmSerializable {

    private int idItem;//ID_Item
    private int idAmbiente;//ID_Ambiente
    private String descricao;//Item
    private String epc;//EPC
    private String rfid;//RFID
    private String evidencia;//Evidencia
    private int estoque;//Estoque
    private String categoria;//Categoria
    private int idUsuario;//ID_usuario
    private int idCasa;//ID_Casa
    private int idAmbienteItem;//ID_Ambiente_Item
    private int idCategoria;//ID_Categoria
    private String ambiente;//Ambiente


    public Item() {
        idItem = 0;
        idAmbiente = 0;
        descricao = "";
        epc = "";
        rfid = "";
        evidencia = "";
        estoque = 0;
        categoria = "";
        idUsuario = 0;
        idCasa = 0;
        idAmbienteItem = 0;
        idCategoria = 0;
        ambiente = "";
    }


    @Override
    public String toString() {
        return descricao;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
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

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
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

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(int idCasa) {
        this.idCasa = idCasa;
    }

    public int getIdAmbienteItem() {
        return idAmbienteItem;
    }

    public void setIdAmbienteItem(int idAmbienteItem) {
        this.idAmbienteItem = idAmbienteItem;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    @Override
    public Object getProperty(int i) {

        switch (i) {
            case 0:
                return idAmbienteItem;
            case 1:
                return idAmbiente;
            case 2:
                return idCategoria;
            case 3:
                return idItem;
            case 4:
                return idCasa;
            case 5:
                return rfid;
            case 6:
                return epc;
            case 7:
                return evidencia;
            case 8:
                return estoque;
            case 9:
                return idUsuario;
            case 10:
                return ambiente;
            case 11:
                return categoria;
            case 12:
                return descricao;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 13;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i) {
            case 0:
                idAmbienteItem = Integer.parseInt(o.toString());
                break;
            case 1:
                idAmbiente = Integer.parseInt(o.toString());
                break;
            case 2:
                idCategoria = Integer.parseInt(o.toString());
                break;
            case 3:
                idItem = Integer.parseInt(o.toString());
                break;
            case 4:
                idCasa = Integer.parseInt(o.toString());
                break;
            case 5:
                rfid = o.toString();
                break;
            case 6:
                epc = o.toString();
                break;
            case 7:
                evidencia = o.toString();
                break;
            case 8:
                estoque = Integer.parseInt(o.toString());
                break;
            case 9:
                idUsuario = Integer.parseInt(o.toString());
                break;
            case 10:
                ambiente = o.toString();
                break;
            case 11:
                categoria = o.toString();
                break;
            case 12:
                descricao = o.toString();
                break;

        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Ambiente_Item";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Ambiente";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Categoria";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Item";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Casa";
                break;

            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "RFID";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "EPC";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Evidencia";
                break;

            case 8:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "Estoque";
                break;

            case 9:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Usuario";
                break;

            case 10:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "Ambiente";
                break;

            case 11:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Categoria";
                break;

            case 12:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Item";
                break;
        }

    }
}

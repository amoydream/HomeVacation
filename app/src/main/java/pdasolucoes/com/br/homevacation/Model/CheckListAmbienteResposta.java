package pdasolucoes.com.br.homevacation.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by PDA on 08/11/2017.
 */

public class CheckListAmbienteResposta implements KvmSerializable {

    private int idChecklist;
    private int idAmbiente;
    private String resposta;
    private int idUsuario;

    public CheckListAmbienteResposta() {
        idChecklist = 0;
        idAmbiente = 0;
        resposta = "";
        idUsuario = 0;
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

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return idChecklist;
            case 1:
                return idAmbiente;
            case 2:
                return resposta;
            case 3:
                return idUsuario;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 4;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i) {
            case 0:
                idChecklist = Integer.parseInt(o.toString());
                break;
            case 1:
                idAmbiente = Integer.parseInt(o.toString());
                break;
            case 2:
                resposta = o.toString();
                break;
            case 3:
                idUsuario = Integer.parseInt(o.toString());
        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Checklist";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Ambiente";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Resposta";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Usuario";
                break;
        }
    }
}

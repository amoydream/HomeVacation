package pdasolucoes.com.br.homevacation.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.security.ProtectionDomain;
import java.util.Hashtable;

/**
 * Created by PDA on 17/10/2017.
 */

public class QuestaoCheckListVolta implements KvmSerializable {

    private int idChecklist;
    private int idQuestao;
    private String resposta;
    private byte[] foto;
    private String caminhoFoto;
    private int idUsuario;
    private boolean flagEvidencia;
    private String evidenciaPath;

    public QuestaoCheckListVolta() {
        idChecklist = 0;
        idQuestao = 0;
        resposta = "";
        foto = null;
        idUsuario = 0;
        evidenciaPath = "";
        flagEvidencia = false;
    }

    public boolean isFlagEvidencia() {
        return flagEvidencia;
    }

    public void setFlagEvidencia(boolean flagEvidencia) {
        this.flagEvidencia = flagEvidencia;
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

    public int getIdQuestao() {
        return idQuestao;
    }

    public void setIdQuestao(int idQuestao) {
        this.idQuestao = idQuestao;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
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

    @Override
    public Object getProperty(int i) {

        switch (i) {
            case 0:
                return idChecklist;
            case 1:
                return idQuestao;
            case 2:
                return resposta;
            case 3:
                return foto;
            case 4:
                return idUsuario;
            case 5:
                return evidenciaPath;
            case 6:
                return flagEvidencia;
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
                idChecklist = Integer.parseInt(o.toString());
                break;
            case 1:
                idQuestao = Integer.parseInt(o.toString());
                break;
            case 2:
                resposta = o.toString();
                break;
            case 3:
                foto = new byte[]{Byte.parseByte(o.toString())};
                break;
            case 4:
                idUsuario = Integer.parseInt(o.toString());
                break;
            case 5:
                evidenciaPath = o.toString();
                break;

            case 6:
                flagEvidencia = Boolean.parseBoolean(o.toString());
                break;
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
                propertyInfo.name = "ID_Questao";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Resposta";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Evidencia";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ID_Usuario";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "EvidenciaPath";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                propertyInfo.name = "FlagEvidencia";
                break;
        }
    }
}

package pdasolucoes.com.br.homevacation.Service;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Questao;

/**
 * Created by PDA on 12/10/2017.
 */

public class QuestaoService {


    public static String URL = "http://169.55.84.219/wshomevacation/wshomevacation.asmx";
    //private static final String URL = "http://169.55.84.219/wshomevacationdesenv/wshomevacation.asmx";
    private static String METHOD_NAME = "GetListaQuestao";
    private static String METHOD_NAME_SET = "SetQuestao";
    private static String METHOD_NAME_GET = "getQuestao";
    private static String NAMESPACE = "http://tempuri.org/";

    public static List<Questao> GetListaQuestao(int idAmbiente) {


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject response;
        List<Questao> lista = new ArrayList<>();
        try {

            PropertyInfo propertyIdAmbiente = new PropertyInfo();
            propertyIdAmbiente.setName("_idAmbiente");
            propertyIdAmbiente.setValue(idAmbiente);
            propertyIdAmbiente.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdAmbiente);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);
                Questao q = new Questao();

                q.setDescricao(item.getPropertyAsString("Descricao"));
                q.setIdAmbiente(Integer.parseInt(item.getPropertyAsString("ID_Ambiente")));
                q.setId(Integer.parseInt(item.getPropertyAsString("ID")));
                q.setEvidencia(item.getPropertyAsString("Evidencia"));
                q.setIdUsuario(Integer.parseInt(item.getPropertyAsString("ID_Usuario")));

                lista.add(q);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static Questao SetQuestao(Questao q) {


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET);
        SoapObject response;
        Questao qe = new Questao();
        try {

            PropertyInfo propertyIdAmbiente = new PropertyInfo();
            propertyIdAmbiente.setName("_idAmbiente");
            propertyIdAmbiente.setValue(q.getIdAmbiente());
            propertyIdAmbiente.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdAmbiente);

            PropertyInfo propertyDesc = new PropertyInfo();
            propertyDesc.setName("_descricao");
            propertyDesc.setValue(q.getDescricao());
            propertyDesc.setType(PropertyInfo.STRING_CLASS);

            request.addProperty(propertyDesc);

            PropertyInfo propertyEvidencia = new PropertyInfo();
            propertyEvidencia.setName("_evidencia");
            propertyEvidencia.setValue(q.getEvidencia());
            propertyEvidencia.setType(PropertyInfo.STRING_CLASS);

            request.addProperty(propertyEvidencia);

            PropertyInfo propertyIdUsuario = new PropertyInfo();
            propertyIdUsuario.setName("_idUsuario");
            propertyIdUsuario.setValue(q.getIdUsuario());
            propertyIdUsuario.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdUsuario);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME_SET, envelope);

            response = (SoapObject) envelope.getResponse();

            qe.setDescricao(response.getPropertyAsString("Descricao"));
            qe.setIdAmbiente(Integer.parseInt(response.getPropertyAsString("ID_Ambiente")));
            qe.setId(Integer.parseInt(response.getPropertyAsString("ID")));
            qe.setEvidencia(response.getPropertyAsString("Evidencia"));
            qe.setIdUsuario(Integer.parseInt(response.getPropertyAsString("ID_Usuario")));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return qe;
    }


    public static List<Questao> GetListaQuestao() {

        List<Questao> lista = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GET);
        SoapObject response;
        try {

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME_GET, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);
                Questao q = new Questao();

                q.setId(Integer.parseInt(item.getPropertyAsString("Codigo")));
                q.setDescricao(item.getPropertyAsString("Descricao"));

                lista.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}

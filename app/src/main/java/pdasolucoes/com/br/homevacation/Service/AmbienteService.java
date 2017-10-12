package pdasolucoes.com.br.homevacation.Service;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Ambiente;

/**
 * Created by PDA on 04/10/2017.
 */

public class AmbienteService {

    private static final String URL = "http://179.184.159.52/homevacation/wshomevacation.asmx";
    private static final String SOAP_ACTION = "http://tempuri.org/";
    private static final String METHOD_NAME = "GetListaAmbiente";
    private static final String METHOD_NAME_GENERIC = "GetListaAmbienteGenerico";
    private static final String METHOD_NAME_SET = "SetAmbiente";
    private static final String NAMESPACE = "http://tempuri.org/";

    public static List<Ambiente> getAmbiente(int idCasa) {
        List<Ambiente> lista = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject response;

        try {

            PropertyInfo propertyIdCasa = new PropertyInfo();
            propertyIdCasa.setName("_idCasa");
            propertyIdCasa.setValue(idCasa);
            propertyIdCasa.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdCasa);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);

                Ambiente a = new Ambiente();

                a.setDescricao(item.getPropertyAsString("Descricao"));
                a.setId(Integer.parseInt(item.getPropertyAsString("ID")));
                a.setIdCasa(Integer.parseInt(item.getPropertyAsString("ID_Casa")));
                a.setOrdem(Integer.parseInt(item.getPropertyAsString("Ordem")));
                a.setItens(Integer.parseInt(item.getPropertyAsString("Itens")));
                a.setQuestoes(Integer.parseInt(item.getPropertyAsString("Questoes")));

                lista.add(a);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Ambiente> getAmbienteGenerico() {
        List<Ambiente> lista = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GENERIC);
        SoapObject response;

        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME_GENERIC, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);

                Ambiente a = new Ambiente();

                a.setDescricao(item.getPropertyAsString("Descricao"));
                lista.add(a);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static int setAmbiente(Ambiente a) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET);
        SoapObject response;

        try {

            PropertyInfo propertyIdCasa = new PropertyInfo();
            propertyIdCasa.setName("_idCasa");
            propertyIdCasa.setValue(a.getIdCasa());
            propertyIdCasa.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdCasa);

            PropertyInfo propertyDesc = new PropertyInfo();
            propertyDesc.setName("_descricao");
            propertyDesc.setValue(a.getDescricao());
            propertyDesc.setType(PropertyInfo.STRING_CLASS);

            request.addProperty(propertyDesc);

            PropertyInfo propertyOrdem = new PropertyInfo();
            propertyOrdem.setName("_ordem");
            propertyOrdem.setValue(a.getOrdem());
            propertyOrdem.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyOrdem);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME_SET, envelope);

            response = (SoapObject) envelope.getResponse();

            Log.w("response", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return 10;

    }
}

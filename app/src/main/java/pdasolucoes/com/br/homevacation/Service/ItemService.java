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
import pdasolucoes.com.br.homevacation.Model.Item;

/**
 * Created by PDA on 04/10/2017.
 */

public class ItemService {

    private static final String URL = "http://179.184.159.52/homevacation/wshomevacation.asmx";
    private static final String SOAP_ACTION = "http://tempuri.org/";
    private static final String METHOD_NAME = "GetListaAmbienteItem";
    private static final String METHOD_NAME_GENERIC = "GetListaItem";
    private static final String METHOD_NAME_SET = "SetListaAmbienteItem";
    private static final String METHOD_NAME_SET2 = "SetItem";
    private static final String NAMESPACE = "http://tempuri.org/";

    public static List<Item> getItem(int idAmbiente) {
        List<Item> lista = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject response;

        try {

            PropertyInfo propertyAmbiente = new PropertyInfo();
            propertyAmbiente.setName("_idAmbiente");
            propertyAmbiente.setValue(idAmbiente);
            propertyAmbiente.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyAmbiente);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);

                Item it = new Item();

                it.setDescricao(item.getPropertyAsString("Item"));
                it.setIdItem(Integer.parseInt(item.getPropertyAsString("ID_Item")));
                it.setEvidencia(item.getPropertyAsString("Evidencia"));
                it.setRfid(item.getPropertyAsString("RFID"));
                it.setEstoque(Integer.parseInt(item.getPropertyAsString("Estoque")));
                it.setEpc(item.getPropertyAsString("EPC"));
                it.setCategoria(item.getPropertyAsString("Categoria"));
                it.setIdUsuario(Integer.parseInt(item.getPropertyAsString("ID_Usuario")));
                it.setIdAmbiente(Integer.parseInt(item.getPropertyAsString("ID_Ambiente")));

                lista.add(it);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Item> getItemGenerico() {
        List<Item> lista = new ArrayList<>();
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

                Item it = new Item();

                it.setDescricao(item.getPropertyAsString("Descricao"));
                it.setIdItem(Integer.parseInt(item.getPropertyAsString("ID")));
                it.setIdUsuario(Integer.parseInt(item.getPropertyAsString("ID_Usuario")));

                lista.add(it);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static boolean setListaAmbienteItem(List<Item> lista) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET);
        SoapObject response;

        try {

            SoapObject soapObject = new SoapObject(NAMESPACE, "_lstAmbienteItem");
            soapObject.addProperty("AmbienteItemEO", lista.get(0));

            request.addSoapObject(soapObject);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "AmbienteItemEO", new Item().getClass());

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME_SET, envelope);

            response = (SoapObject) envelope.getResponse();

            Log.w("response", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static int setItem(Item i) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET2);
        SoapObject response;

        try {

            PropertyInfo propertyDescricao = new PropertyInfo();
            propertyDescricao.setName("_descricao");
            propertyDescricao.setValue(i.getDescricao());
            propertyDescricao.setType(PropertyInfo.STRING_CLASS);

            request.addProperty(propertyDescricao);

            PropertyInfo propertyIdUsuario = new PropertyInfo();
            propertyIdUsuario.setName("_idUsuario");
            propertyIdUsuario.setValue(0);
            propertyIdUsuario.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdUsuario);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME_SET2, envelope);

            response = (SoapObject) envelope.getResponse();

            Log.w("response", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return 0;
        }

        return Integer.parseInt(response.getPropertyAsString("ID"));
    }
}

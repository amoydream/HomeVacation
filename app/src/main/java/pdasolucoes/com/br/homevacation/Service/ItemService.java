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

import pdasolucoes.com.br.homevacation.Model.Categoria;
import pdasolucoes.com.br.homevacation.Model.Item;

/**
 * Created by PDA on 04/10/2017.
 */

public class ItemService {

    private static final String URL = "http://169.55.84.219/wshomevacation/wshomevacation.asmx";
    //private static final String URL = "http://169.55.84.219/wshomevacationdesenv/wshomevacation.asmx";
    private static final String SOAP_ACTION = "http://tempuri.org/";
    private static final String METHOD_NAME = "GetListaAmbienteItem";
    private static final String METHOD_NAME_GENERIC = "GetListaItem";
    private static final String METHOD_NAME_GENERIC_CATEGORY = "GetListaAmbienteGenericoMobile";
    private static final String METHOD_NAME_SET = "SetListaAmbienteItem";
    private static final String METHOD_NAME_SET2 = "SetItem";
//    private static final String METHOD_NAME_SET_CATEGORY = "SetCategoria";
    private static final String METHOD_SET_EPC = "SetAmbienteItemECP";
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
                it.setIdAmbienteItem(Integer.parseInt(item.getPropertyAsString("ID_Ambiente_Item")));
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

    public static List<Item> getItemGenerico(String nomeAmbiente) {
        List<Item> lista = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GENERIC);
        SoapObject response;

        try {
            PropertyInfo propertyItem = new PropertyInfo();
            propertyItem.setName("ambiente");
            propertyItem.setValue(nomeAmbiente);
            propertyItem.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyItem);

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

    public static List<Categoria> getItemCategoria() {
        List<Categoria> lista = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GENERIC_CATEGORY);
        SoapObject response;

        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME_GENERIC_CATEGORY, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);

                Categoria c = new Categoria();

                c.setDescricao(item.getPropertyAsString("Categoria"));
                c.setIdCategoria(Integer.parseInt(item.getPropertyAsString("IdCategoria")));

                lista.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static int setListaAmbienteItem(Item i) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET);
        SoapObject response;
        int idAmbienteItem = 0, maior = 0, aux = 0;

        try {

            SoapObject soapObject = new SoapObject(NAMESPACE, "_lstAmbienteItem");
            soapObject.addProperty("AmbienteItemEO", i);

            request.addSoapObject(soapObject);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "AmbienteItemEO", new Item().getClass());

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_NAME_SET, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int j = 0; j < response.getPropertyCount(); j++) {
                SoapObject item = (SoapObject) response.getProperty(j);
                aux = Integer.parseInt(item.getPropertyAsString("ID_Ambiente_Item"));

                if (aux > maior) {
                    maior = aux;
                    idAmbienteItem = maior;
                }
            }


            Log.w("response", response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return 0;
        }

        return idAmbienteItem;
    }

//    public static int setCategoria(Categoria c) {
//        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET_CATEGORY);
//        SoapObject response;
//        int id;
//
//        try {
//
//            PropertyInfo propertyDescricao = new PropertyInfo();
//            propertyDescricao.setName("categoria");
//            propertyDescricao.setValue(c.getDescricao());
//            propertyDescricao.setType(PropertyInfo.STRING_CLASS);
//
//            request.addProperty(propertyDescricao);
//
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//            envelope.dotNet = true;
//            envelope.implicitTypes = true;
//            envelope.setOutputSoapObject(request);
//
//            HttpTransportSE transportSE = new HttpTransportSE(URL);
//            transportSE.call(SOAP_ACTION + METHOD_NAME_SET_CATEGORY, envelope);
//
//            response = (SoapObject) envelope.getResponse();
//
//            SoapObject item = (SoapObject) response.getProperty("CategoriaEO");
//            id = Integer.parseInt(item.getPropertyAsString("IdCategoria"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return 0;
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return 0;
//        }
//
//        return id;
//    }


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
            propertyIdUsuario.setValue(i.getIdUsuario());
            propertyIdUsuario.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdUsuario);

            PropertyInfo propertyCategoria = new PropertyInfo();
            propertyCategoria.setName("_idCategoria");
            propertyCategoria.setValue(i.getIdCategoria());
            propertyCategoria.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyCategoria);


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

    public static int SetItemEpc(String epc, int idAmbienteItem) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_SET_EPC);
        SoapObject response;
        int result = 0;

        try {

            PropertyInfo propertyEpc = new PropertyInfo();
            propertyEpc.setName("EPC");
            propertyEpc.setValue(epc);
            propertyEpc.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyEpc);

            PropertyInfo propertyIdAmbienteItem = new PropertyInfo();
            propertyIdAmbienteItem.setName("idAmbienteItem");
            propertyIdAmbienteItem.setValue(idAmbienteItem);
            propertyIdAmbienteItem.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdAmbienteItem);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(SOAP_ACTION + METHOD_SET_EPC, envelope);

            response = (SoapObject) envelope.bodyIn;
            result = Integer.parseInt(response.getPropertyAsString("SetAmbienteItemECPResult"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return result;

    }
}

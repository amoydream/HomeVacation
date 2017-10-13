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

import pdasolucoes.com.br.homevacation.Model.CheckList;

/**
 * Created by PDA on 12/10/2017.
 */

public class CheckListService {

    private static final String URL = "http://179.184.159.52/homevacation/wshomevacation.asmx";
    private static final String METHOD_NAME = "GetListaCheckListItens";
    private static final String METHOD_NAME_SET = "CriarCheckList";
    private static final String NAMESPACE = "http://tempuri.org/";

    public static List<CheckList> GetListaCheckListItens(int idCheckList) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject response;
        List<CheckList> lista = new ArrayList<>();
        try {

            PropertyInfo propertyCheck = new PropertyInfo();
            propertyCheck.setName("_idChecklist");
            propertyCheck.setValue(idCheckList);
            propertyCheck.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyCheck);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);
                CheckList c = new CheckList();

                c.setId(Integer.parseInt(item.getPropertyAsString("ID_CheckList")));
                c.setAmbiente(item.getPropertyAsString("Ambiente"));
                c.setAmbienteOrdem(Integer.parseInt(item.getPropertyAsString("AmbienteOrdem")));
                c.setCategoria(item.getPropertyAsString("Categoria"));
                c.setItem(item.getPropertyAsString("Item"));
                c.setRfid(item.getPropertyAsString("RFID"));
                c.setEpc(item.getPropertyAsString("EPC"));
                c.setEvidencia(item.getPropertyAsString("Evidencia"));
                c.setEstoque(Integer.parseInt(item.getPropertyAsString("Estoque")));
                lista.add(c);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static int CriarCheckList(int idCasa, int idUsuario) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET);
        SoapObject response;
        int idCheckList = 0;
        try {

            PropertyInfo propertyIdCasa = new PropertyInfo();
            propertyIdCasa.setName("_idCasa");
            propertyIdCasa.setValue(idCasa);
            propertyIdCasa.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyIdCasa);

            PropertyInfo propertyUsuario = new PropertyInfo();
            propertyUsuario.setName("_idUsuario");
            propertyUsuario.setValue(idUsuario);
            propertyUsuario.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyUsuario);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME_SET, envelope);

            response = (SoapObject) envelope.getResponse();

            idCheckList = Integer.parseInt(response.getPropertyAsString("ID"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return idCheckList;
    }
}

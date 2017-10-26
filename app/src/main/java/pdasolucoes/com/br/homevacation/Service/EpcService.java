package pdasolucoes.com.br.homevacation.Service;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Casa;
import pdasolucoes.com.br.homevacation.Model.EPC;

/**
 * Created by PDA on 25/10/2017.
 */

public class EpcService {

    private static final String URL = "http://179.184.159.52/homevacation/wshomevacation.asmx";
    private static final String METHOD_NAME = "getCasaEPC";
    private static final String NAMESPACE = "http://tempuri.org/";


    public static List<EPC> GetListaEPC(int idCasa) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject item, response;
        List<EPC> lista = new ArrayList<>();

        try {

            PropertyInfo propertyCasa = new PropertyInfo();
            propertyCasa.setName("codigoCasa");
            propertyCasa.setValue(idCasa);
            propertyCasa.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyCasa);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                item = (SoapObject) response.getProperty(i);
                EPC e = new EPC();

                e.setCodigo(Integer.parseInt(item.getPropertyAsString("Codigo")));
                e.setEpc(item.getPropertyAsString("Descricao"));

                lista.add(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {

            e.printStackTrace();
        }

        return lista;
    }
}

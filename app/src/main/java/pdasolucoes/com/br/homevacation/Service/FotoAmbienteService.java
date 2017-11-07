package pdasolucoes.com.br.homevacation.Service;

import android.util.Base64;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Casa;
import pdasolucoes.com.br.homevacation.Model.FotoAmbiente;
import pdasolucoes.com.br.homevacation.Util.TransformarImagem;

/**
 * Created by PDA on 01/11/2017.
 */

public class FotoAmbienteService {

    private static final String URL = "http://169.55.84.219/wshomevacation/wshomevacation.asmx";
    //private static final String URL = "http://169.55.84.219/wshomevacationdesenv/wshomevacation.asmx";
    private static final String METHOD_NAME = "SetListaAmbienteFoto";
    private static final String METHOD_GET_NAME = "GetListaAmbienteFoto";
    private static final String NAMESPACE = "http://tempuri.org/";


    public static List<FotoAmbiente> listar(int idAmbiente) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_NAME);
        SoapObject item, response;
        List<FotoAmbiente> lista = new ArrayList<>();

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
            transportSE.call(NAMESPACE + METHOD_GET_NAME, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                item = (SoapObject) response.getProperty(i);
                FotoAmbiente f = new FotoAmbiente();


                f.setIdAmbiente(Integer.parseInt(item.getPropertyAsString("ID_Ambiente")));
                f.setFotoRetornoString(item.getProperty("Foto_Path").toString());


                lista.add(f);
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


    public static boolean setListaAmbienteItem(List<FotoAmbiente> lista, int idAmbiente) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject response;

        try {

            SoapObject soapObject = new SoapObject(NAMESPACE, "_lstAmbiente");

            for (FotoAmbiente f : lista) {
                f.setIdAmbiente(idAmbiente);

                if (f.getCaminhoFoto() != null) {
                    f.setFotoStream(TransformarImagem.getBitmapAsByteArray(f.getCaminhoFoto()));
                }
                soapObject.addProperty("AmbienteFotoEO", f);
            }


            request.addSoapObject(soapObject);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "AmbienteFotoEO", new FotoAmbiente().getClass());

            MarshalBase64 md = new MarshalBase64();
            md.register(envelope);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME, envelope);

            response = (SoapObject) envelope.bodyIn;

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
}

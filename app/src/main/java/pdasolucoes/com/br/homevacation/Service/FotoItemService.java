package pdasolucoes.com.br.homevacation.Service;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.FotoAmbiente;
import pdasolucoes.com.br.homevacation.Model.FotoItem;
import pdasolucoes.com.br.homevacation.Util.TransformarImagem;

/**
 * Created by PDA on 08/11/2017.
 */

public class FotoItemService {

    private static final String URL = "http://169.55.84.219/wshomevacation/wshomevacation.asmx";
    //private static final String URL = "http://169.55.84.219/wshomevacationdesenv/wshomevacation.asmx";
    private static final String METHOD_NAME = "SetListaAmbienteItemFoto";
    private static final String NAMESPACE = "http://tempuri.org/";


    public static boolean setListaItemFoto(List<FotoItem> lista) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject response;

        try {

            SoapObject soapObject = new SoapObject(NAMESPACE, "_lstAmbiente");

            for (FotoItem f : lista) {
                if (f.getCaminhoFoto() != null) {
                    f.setFoto(TransformarImagem.getBitmapAsByteArray(f.getCaminhoFoto()));
                }
                soapObject.addProperty("AmbienteItemFotoEO", f);
            }


            request.addSoapObject(soapObject);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "AmbienteItemFotoEO", new FotoItem().getClass());

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

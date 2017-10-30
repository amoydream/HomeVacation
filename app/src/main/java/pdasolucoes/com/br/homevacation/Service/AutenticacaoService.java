package pdasolucoes.com.br.homevacation.Service;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import pdasolucoes.com.br.homevacation.Model.Usuario;

/**
 * Created by PDA on 25/10/2017.
 */

public class AutenticacaoService {

    private static final String URL = "http://169.55.84.219/wshomevacation/wshomevacation.asmx";
    private static final String METHOD_NAME = "GetAutenticacao";
    private static final String NAMESPACE = "http://tempuri.org/";


    public static Usuario AutencicaoUsuario(String login, String senha) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject item = null;
        Usuario u = new Usuario();

        try {

            PropertyInfo propertyUser = new PropertyInfo();
            propertyUser.setName("login");
            propertyUser.setValue(login);
            propertyUser.setType(PropertyInfo.STRING_CLASS);

            request.addProperty(propertyUser);

            PropertyInfo propertySenha = new PropertyInfo();
            propertySenha.setName("senha");
            propertySenha.setValue(senha);
            propertySenha.setType(PropertyInfo.STRING_CLASS);

            request.addProperty(propertySenha);

            PropertyInfo propertySistema = new PropertyInfo();
            propertySistema.setName("idSistema");
            propertySistema.setValue(3);
            propertySistema.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertySistema);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME, envelope);

            item = (SoapObject) envelope.getResponse();

            if (item.getPropertyAsString("ErrorMessage").equals("OK")) {

                u.setId(Integer.parseInt(item.getPropertyAsString("UserID")));
                u.setLogin(item.getPropertyAsString("UserLogin"));
                u.setSenha(item.getPropertyAsString("UserPassword"));
                u.setIdConta(Integer.parseInt(item.getPropertyAsString("AccountID")));
                u.setErrorAutenticao(item.getPropertyAsString("ErrorMessage"));
            } else {
                u.setErrorAutenticao(item.getPropertyAsString("ErrorMessage"));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {

            e.printStackTrace();
        }

        return u;
    }
}

package pdasolucoes.com.br.homevacation.Service;

import android.content.Intent;
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

import pdasolucoes.com.br.homevacation.Model.CheckList;
import pdasolucoes.com.br.homevacation.Model.CheckListVolta;
import pdasolucoes.com.br.homevacation.Model.Item;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckList;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckListVolta;
import pdasolucoes.com.br.homevacation.Util.TransformarImagem;

/**
 * Created by PDA on 12/10/2017.
 */

public class CheckListService {

    private static final String URL = "http://179.184.159.52/homevacation/wshomevacation.asmx";
    private static final String METHOD_NAME = "GetListaCheckListItens";
    private static final String METHOD_NAME_QUESTAO = "GetListaCheckListQuestao";
    private static final String METHOD_NAME_SET = "CriarCheckList";
    private static final String METHOD_NAME_SET_VOLTA = "SetListaCheckListItem";
    private static final String METHOD_NAME_SET_VOLTA_QUESTAO = "SetListaCheckListQuestao";
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
                c.setIdCasaItem(Integer.parseInt(item.getPropertyAsString("ID_Casa_Item")));
                c.setCategoria(item.getPropertyAsString("Categoria"));
                c.setItem(item.getPropertyAsString("Item"));
                c.setRfid(item.getPropertyAsString("RFID"));
                c.setEpc(item.getPropertyAsString("EPC"));
                c.setIdAmbiente(Integer.parseInt(item.getPropertyAsString("IdAmbiente")));
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


    public static List<QuestaoCheckList> GetCheckListQuestao(int idCheckList) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_QUESTAO);
        SoapObject response;
        List<QuestaoCheckList> lista = new ArrayList<>();
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
            transportSE.call(NAMESPACE + METHOD_NAME_QUESTAO, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);
                QuestaoCheckList q = new QuestaoCheckList();

                q.setIdCheckList(Integer.parseInt(item.getPropertyAsString("ID")));
                q.setIdAmbiente(Integer.parseInt(item.getPropertyAsString("ID_Ambiente")));
                q.setOrdem(Integer.parseInt(item.getPropertyAsString("Ordem")));
                q.setIdQuestao(Integer.parseInt(item.getPropertyAsString("ID_Questao")));
                q.setQuestao(item.getPropertyAsString("Questao"));
                q.setEvidencia(item.getPropertyAsString("Evidencia"));

                lista.add(q);

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


    public static int SetChecklistItem(List<CheckListVolta> lista) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET_VOLTA);
        SoapObject response;
        int result = 0;

        try {

            SoapObject soapObject = new SoapObject(NAMESPACE, "_lstChecklist");
            for (CheckListVolta c : lista) {
                if (c.getCaminhoFoto() != null) {
                    c.setFoto(TransformarImagem.getBitmapAsByteArray(c.getCaminhoFoto()));
                    c.setFlagEvidencia(true);
                } else {
                    c.setFlagEvidencia(false);

                }
                soapObject.addProperty("CheckListItemRespostaEO", c);
            }

            request.addSoapObject(soapObject);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);


            envelope.addMapping(NAMESPACE, "CheckListItemRespostaEO", new CheckListVolta().getClass());

            MarshalBase64 md = new MarshalBase64();
            md.register(envelope);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME_SET_VOLTA, envelope);

            response = (SoapObject) envelope.bodyIn;
            result = Integer.parseInt(response.getPropertyAsString("SetListaCheckListItemResult"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int SetChecklistQuestao(List<QuestaoCheckListVolta> lista) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SET_VOLTA_QUESTAO);
        SoapObject response;
        int result = 0;

        try {

            SoapObject soapObject = new SoapObject(NAMESPACE, "_lstChecklist");
            for (QuestaoCheckListVolta q : lista) {
                if (q.getCaminhoFoto() != null) {
                    q.setFoto(TransformarImagem.getBitmapAsByteArray(q.getCaminhoFoto()));
                    q.setFlagEvidencia(true);
                } else {
                    q.setFlagEvidencia(false);
                }
                soapObject.addProperty("CkeckListQuestaoRespostaEO", q);
            }

            request.addSoapObject(soapObject);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);


            envelope.addMapping(NAMESPACE, "CkeckListQuestaoRespostaEO", new QuestaoCheckListVolta().getClass());

            MarshalBase64 md = new MarshalBase64();
            md.register(envelope);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME_SET_VOLTA_QUESTAO, envelope);

            response = (SoapObject) envelope.bodyIn;

            result = Integer.parseInt(response.getPropertyAsString("SetListaCheckListQuestaoResult"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return result;
    }
}

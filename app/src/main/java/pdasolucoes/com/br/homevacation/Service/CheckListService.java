package pdasolucoes.com.br.homevacation.Service;

import android.content.Intent;
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

import pdasolucoes.com.br.homevacation.Model.Agenda;
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

    private static final String URL = "http://169.55.84.219/wshomevacation/wshomevacation.asmx";
    //private static final String URL = "http://169.55.84.219/wshomevacationdesenv/wshomevacation.asmx";
    private static final String METHOD_NAME = "GetListaCheckListItens";
    private static final String METHOD_NAME_QUESTAO = "GetListaCheckListQuestao";
    private static final String METHODO_NAME_INICIO = "SetInicioCkeckList";
    private static final String METHODO_NAME_FINALIZA = "SetFinalizaCkeckList";
    private static final String METHOD_NAME_AGENDA = "GetListaAgenda";
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
                c.setCheckListParametrizado(Integer.parseInt(item.getPropertyAsString("Estoque_Parametrizado")));
                c.setCheckListUltimoParametrizado(Integer.parseInt(item.getPropertyAsString("Estoque_Ultimo_Checklist")));
                c.setIdCasa(Integer.parseInt(item.getPropertyAsString("ID_Casa")));
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
                q.setIdCasa(Integer.parseInt(item.getPropertyAsString("ID_Casa")));
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

    public static List<Agenda> CriarCheckList(int idUsuario, String dataAgenda) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_AGENDA);
        SoapObject response;
        List<Agenda> lista = new ArrayList<>();
        try {

            PropertyInfo propertyUsuario = new PropertyInfo();
            propertyUsuario.setName("_usuario");
            propertyUsuario.setValue(idUsuario);
            propertyUsuario.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyUsuario);

            PropertyInfo propertyData = new PropertyInfo();
            propertyData.setName("_dtAgenda");
            propertyData.setValue(dataAgenda);
            propertyData.setType(PropertyInfo.STRING_CLASS);

            request.addProperty(propertyData);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHOD_NAME_AGENDA, envelope);

            response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) response.getProperty(i);
                Agenda a = new Agenda();
                a.setIdCheckList(Integer.parseInt(item.getPropertyAsString("ID_Checklist")));
                a.setDataAgenda(item.getPropertyAsString("DataAgengaString"));
                a.setIdCasa(Integer.parseInt(item.getPropertyAsString("ID_Casa")));
                a.setDescricaoCasa(item.getPropertyAsString("DescricaoCasa"));
                a.setComunidade(item.getPropertyAsString("ComunidadeCasa"));
                a.setImagem(item.getPropertyAsString("FotoCasa"));

                lista.add(a);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return lista;
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
        } catch (Exception e) {
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


    public static int IniciarCheckList(int idCheckList) {

        SoapObject request = new SoapObject(NAMESPACE, METHODO_NAME_INICIO);
        SoapObject response;
        int result = 0;
        try {

            PropertyInfo propertyCheck = new PropertyInfo();
            propertyCheck.setName("_idCheckList");
            propertyCheck.setValue(idCheckList);
            propertyCheck.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyCheck);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHODO_NAME_INICIO, envelope);

            response = (SoapObject) envelope.bodyIn;
            result = Integer.parseInt(response.getPropertyAsString("SetInicioCkeckListResult"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int FinalizaCheckList(int idCheckList) {

        SoapObject request = new SoapObject(NAMESPACE, METHODO_NAME_FINALIZA);
        SoapObject response;
        int result = 0;
        try {

            PropertyInfo propertyCheck = new PropertyInfo();
            propertyCheck.setName("_idCheckList");
            propertyCheck.setValue(idCheckList);
            propertyCheck.setType(PropertyInfo.INTEGER_CLASS);

            request.addProperty(propertyCheck);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(NAMESPACE + METHODO_NAME_FINALIZA, envelope);

            response = (SoapObject) envelope.bodyIn;

            result = Integer.parseInt(response.getPropertyAsString("SetFinalizaCkeckListResult"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return result;
    }
}

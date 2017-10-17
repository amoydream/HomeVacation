package pdasolucoes.com.br.homevacation.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Ambiente;

/**
 * Created by PDA on 12/10/2017.
 */

public class ListaAmbienteDao {

    public static String salvar(List<Ambiente> lista) {

        JSONArray array = new JSONArray();
        JSONObject obj;

        for (Ambiente a : lista) {
            obj = new JSONObject();

            try {
                if (a.getOrdem() == 0) {
                    obj.put("id", a.getId());
                    obj.put("ordem", a.getOrdem());
                    obj.put("respondido", true);
                    obj.put("descricao", a.getDescricao());
                    obj.put("id_casa", a.getIdCasa());
                    obj.put("itens", a.getItens());
                    obj.put("questions", a.getQuestoes());
                } else {
                    obj.put("id", a.getId());
                    obj.put("ordem", a.getOrdem());
                    obj.put("respondido", false);
                    obj.put("descricao", a.getDescricao());
                    obj.put("id_casa", a.getIdCasa());
                    obj.put("itens", a.getItens());
                    obj.put("questions", a.getQuestoes());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            array.put(obj);
        }

        return array.toString();
    }

    public static List<Ambiente> listar(String preferences) {
        JSONArray array;
        List<Ambiente> lista = new ArrayList<>();
        Ambiente ambiente;
        JSONObject obj;
        try {
            array = new JSONArray(preferences);
            for (int i = 0; i < array.length(); ++i) {
                obj = array.getJSONObject(i);

                ambiente = new Ambiente();

                ambiente.setOrdem(obj.getInt("ordem"));
                ambiente.setId(obj.getInt("id"));
                ambiente.setRespondido(obj.getBoolean("respondido"));
                ambiente.setDescricao(obj.getString("descricao"));
                ambiente.setIdCasa(obj.getInt("id_casa"));
                ambiente.setItens(obj.getInt("itens"));
                ambiente.setQuestoes(obj.getInt("questions"));
                lista.add(ambiente);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static String alterar(List<Ambiente> lista, Ambiente ambiente) {

        JSONArray array = new JSONArray();
        JSONObject obj;
        int valorAtual = ambiente.getOrdem() + 1;

        for (Ambiente a : lista) {
            obj = new JSONObject();
            try {
                if ((a.getOrdem() == (valorAtual)) || a.isRespondido()) {
                    obj.put("id", a.getId());
                    obj.put("ordem", a.getOrdem());
                    obj.put("respondido", true);
                    obj.put("descricao", a.getDescricao());
                    obj.put("id_casa", a.getIdCasa());
                    obj.put("itens", a.getItens());
                    obj.put("questions", a.getQuestoes());
                } else {
                    obj.put("id", a.getId());
                    obj.put("ordem", a.getOrdem());
                    obj.put("respondido", false);
                    obj.put("descricao", a.getDescricao());
                    obj.put("id_casa", a.getIdCasa());
                    obj.put("itens", a.getItens());
                    obj.put("questions", a.getQuestoes());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            array.put(obj);
        }

        return array.toString();
    }


}

package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Questao;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckList;

/**
 * Created by PDA on 17/10/2017.
 */

public class QuestaoDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public QuestaoDao(Context context) {
        helper = new DataBaseHelper(context);
    }


    public SQLiteDatabase getDatabase() {
        if (database == null) {
            database = helper.getWritableDatabase();
        }
        return database;
    }

    public void close() {
        helper.close();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public void incluir(List<QuestaoCheckList> lista) {

        try {
            deletar();
            for (QuestaoCheckList q : lista) {
                ContentValues values = new ContentValues();
                values.put("idChecklist", q.getIdCheckList());
                values.put("idAmbiente", q.getIdAmbiente());
                values.put("ordem", q.getOrdem());
                values.put("idQuestao", q.getIdQuestao());
                values.put("questao", q.getQuestao());
                values.put("evidencia", q.getEvidencia());

                getDatabase().insert("questaoChecklist", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<QuestaoCheckList> listar(int idAmbiente) {

        List<QuestaoCheckList> lista = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("select * from questaoChecklist where idQuestao not in(select v.idQuestao from questaoChecklist c, questaoVolta v" +
                " WHERE c.idChecklist = v.idChecklist and c.idQuestao=v.idQuestao and v.export = 1) and idAmbiente = ?", new String[]{idAmbiente + ""});

        try {
            while (cursor.moveToNext()) {
                QuestaoCheckList q = new QuestaoCheckList();
                q.setEvidencia(cursor.getString(cursor.getColumnIndex("evidencia")));
                q.setOrdem(cursor.getInt(cursor.getColumnIndex("ordem")));
                q.setQuestao(cursor.getString(cursor.getColumnIndex("questao")));
                q.setIdAmbiente(cursor.getInt(cursor.getColumnIndex("idAmbiente")));
                q.setIdQuestao(cursor.getInt(cursor.getColumnIndex("idQuestao")));
                lista.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return lista;
    }


    public int qtdeQuestao(int idAmbiente) {

        int qtde = 0;
        Cursor cursor = getDatabase().rawQuery("select COUNT(*) as qtdeQuestao from questaoChecklist where idQuestao not in(select v.idQuestao from questaoChecklist c, questaoVolta v" +
                " WHERE c.idChecklist = v.idChecklist and c.idQuestao=v.idQuestao and v.export = 1) and idAmbiente = ?", new String[]{idAmbiente + ""});

        try {
            while (cursor.moveToNext()) {
                qtde = cursor.getInt(cursor.getColumnIndex("qtdeQuestao"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return qtde;
    }


    public void deletar() {
        getDatabase().delete("questaoChecklist", null, null);
    }
}

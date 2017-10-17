package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import pdasolucoes.com.br.homevacation.Model.CheckListVolta;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckListVolta;

/**
 * Created by PDA on 17/10/2017.
 */

public class QuestaoVoltaDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public QuestaoVoltaDao(Context context) {
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

    public int incluir(QuestaoCheckListVolta q) {

        try {
            ContentValues values = new ContentValues();
            values.put("idChecklist", q.getIdChecklist());
            values.put("idQuestao", q.getIdQuestao());
            values.put("resposta", q.getResposta());
            values.put("foto", q.getFoto());
            values.put("idUsuario", q.getIdUsuario());
            values.put("export", 0);

            getDatabase().insert("questaoVolta", null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public void export(QuestaoCheckListVolta q) {
        ContentValues values = new ContentValues();
        values.put("export", 1);
        getDatabase().update("questaoVolta", values, "idchecklist = " + q.getIdChecklist() + " and idQuestao = " + q.getIdQuestao(), null);
    }

}

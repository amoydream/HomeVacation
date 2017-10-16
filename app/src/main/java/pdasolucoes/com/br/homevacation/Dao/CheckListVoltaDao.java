package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.ViewAnimationUtils;

import pdasolucoes.com.br.homevacation.Model.CheckListVolta;

/**
 * Created by PDA on 15/10/2017.
 */

public class CheckListVoltaDao {


    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public CheckListVoltaDao(Context context) {
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


    public void incluir(CheckListVolta c) {

        ContentValues values = new ContentValues();
        values.put("idChecklist", c.getIdChecklist());
        values.put("estoque", c.getEstoque());
        values.put("rfid", c.getRfid());
        values.put("foto", c.getFoto());
        values.put("idAmbienteItem", c.getIdAmbienteItem());
        values.put("idUsuario", c.getIdUsuario());
        values.put("export", 0);

        getDatabase().insert("checklistVolta", null, values);
    }

    public void export(CheckListVolta c) {
        ContentValues values = new ContentValues();
        values.put("export", 1);
        getDatabase().update("checklistVolta", values, "idChecklist = " + c.getIdChecklist() + " and idAmbienteItem = " + c.getIdAmbienteItem(), null);
    }
}

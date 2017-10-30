package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import pdasolucoes.com.br.homevacation.Model.CheckList;
import pdasolucoes.com.br.homevacation.Model.EPC;

/**
 * Created by PDA on 26/10/2017.
 */

public class EpcDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public EpcDao(Context context) {
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


    public void incluir(List<EPC> lista) {

        try {

            for (EPC e : lista) {
                ContentValues values = new ContentValues();
                values.put("codigo", e.getCodigo());
                values.put("epc", e.getEpc());

                getDatabase().insert("epc", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean existeEpc(String epc) {
        Cursor cursor = getDatabase().rawQuery("SELECT * FROM epc WHERE epc = ?", new String[]{epc});

        try {

            while (cursor.moveToNext()) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return false;
    }

    public void deletar() {
        getDatabase().delete("epc", null, null);
    }
}

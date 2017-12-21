package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.CheckList;
import pdasolucoes.com.br.homevacation.Model.CheckListVolta;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckListVolta;

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


    public int incluir(CheckListVolta c) {

        try {

            ContentValues values = new ContentValues();
            values.put("idChecklist", c.getIdChecklist());
            values.put("estoque", c.getEstoque());
            values.put("rfid", c.getRfid());
            values.put("caminhofoto", c.getCaminhoFoto());
            values.put("idAmbienteItem", c.getIdAmbienteItem());
            values.put("idUsuario", c.getIdUsuario());
            values.put("idCasa", c.getIdCasa());
            values.put("export", 0);
            values.put("respondido", 0);

            getDatabase().insert("checklistVolta", null, values);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    public void export(List<CheckListVolta> lista) {
        for (CheckListVolta c : lista) {
            ContentValues values = new ContentValues();
            values.put("export", 1);
            getDatabase().update("checklistVolta", values, "idChecklist = " + c.getIdChecklist() + " and idAmbienteItem = " + c.getIdAmbienteItem(), null);
        }
    }

    public void respondido(CheckListVolta c) {
        ContentValues values = new ContentValues();
        values.put("respondido", 1);
        getDatabase().update("checklistVolta", values, "idChecklist = " + c.getIdChecklist() + " and idAmbienteItem = " + c.getIdAmbienteItem(), null);

    }


    public List<CheckListVolta> listar(int idChecklist) {

        List<CheckListVolta> lista = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("SELECT idChecklist,idAmbienteItem,caminhofoto,rfid,estoque,idUsuario FROM checklistVolta WHERE idChecklist = ?", new String[]{idChecklist + ""});

        try {

            while (cursor.moveToNext()) {
                CheckListVolta c = new CheckListVolta();
                c.setIdChecklist(cursor.getInt(cursor.getColumnIndex("idChecklist")));
                c.setIdAmbienteItem(cursor.getInt(cursor.getColumnIndex("idAmbienteItem")));
                c.setCaminhoFoto(cursor.getString(cursor.getColumnIndex("caminhofoto")));
                c.setRfid(cursor.getString(cursor.getColumnIndex("rfid")));
                c.setEstoque(cursor.getInt(cursor.getColumnIndex("estoque")));
                c.setIdUsuario(cursor.getInt(cursor.getColumnIndex("idUsuario")));
                lista.add(c);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return lista;
    }

    public List<CheckListVolta> listarTodos() {

        List<CheckListVolta> lista = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("SELECT idChecklist,idAmbienteItem,caminhofoto,rfid,estoque,idUsuario FROM checklistVolta WHERE export = 0", null);

        try {

            while (cursor.moveToNext()) {
                CheckListVolta c = new CheckListVolta();
                c.setIdChecklist(cursor.getInt(cursor.getColumnIndex("idChecklist")));
                c.setIdAmbienteItem(cursor.getInt(cursor.getColumnIndex("idAmbienteItem")));
                c.setCaminhoFoto(cursor.getString(cursor.getColumnIndex("caminhofoto")));
                c.setRfid(cursor.getString(cursor.getColumnIndex("rfid")));
                c.setEstoque(cursor.getInt(cursor.getColumnIndex("estoque")));
                c.setIdUsuario(cursor.getInt(cursor.getColumnIndex("idUsuario")));
                lista.add(c);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return lista;
    }

    public int count() {

        int pendencias = 0;
        Cursor cursor = getDatabase().rawQuery("SELECT COUNT(*) pendentes FROM(" +
                " SELECT c.idchecklist FROM checklistVolta c WHERE c.export = 0 GROUP BY c.idCheckList" +
                " UNION" +
                " SELECT q.idchecklist FROM questaoVolta q WHERE q.export = 0 GROUP BY q.idCheckList)", null);

        try {

            while (cursor.moveToNext()) {

                pendencias = cursor.getInt(cursor.getColumnIndex("pendentes"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return pendencias;
    }

    public void deleter() {
        getDatabase().delete("checklistVolta", "export = 1", null);
    }


}

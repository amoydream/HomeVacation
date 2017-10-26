package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.CheckList;

/**
 * Created by PDA on 13/10/2017.
 */

public class ChecklistDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public ChecklistDao(Context context) {
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

    //metodos

    public void incluir(List<CheckList> lista) {

        try {
            deletar();

            for (CheckList c : lista) {
                ContentValues values = new ContentValues();
                values.put("idCheckList", c.getId());
                values.put("idAmbiente", c.getIdAmbiente());
                values.put("ambiente", c.getAmbiente());
                values.put("ambienteOrdem", c.getAmbienteOrdem());
                values.put("categoria", c.getCategoria());
                values.put("item", c.getItem());
                values.put("rfid", c.getRfid());
                values.put("epc", c.getEpc());
                values.put("idCasa",c.getId());
                values.put("idCasaItem", c.getIdCasaItem());
                values.put("evidencia", c.getEvidencia());
                values.put("estoque", c.getEstoque());
                values.put("achou",0);

                getDatabase().insert("checklist", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void achou(CheckList c){
        ContentValues values = new ContentValues();
        values.put("achou", 1);
        getDatabase().update("checklist", values, "idChecklist = " + c.getId() + " and idcasaitem = " + c.getIdCasaItem(), null);
    }

    public List<CheckList> listar(int idAmbiente) {

        List<CheckList> lista = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("select * from checklist where idcasaitem not in(select v.idambienteitem from checklist c, checklistVolta v" +
                " WHERE c.idChecklist = v.idChecklist and c.idcasaitem=v.idambienteitem and v.respondido = 1) and idAmbiente = ?", new String[]{idAmbiente + ""});

        try {

            while (cursor.moveToNext()) {
                CheckList c = new CheckList();
                c.setId(cursor.getInt(cursor.getColumnIndex("idChecklist")));
                c.setItem(cursor.getString(cursor.getColumnIndex("item")));
                c.setEstoque(cursor.getInt(cursor.getColumnIndex("estoque")));
                c.setEpc(cursor.getString(cursor.getColumnIndex("epc")));
                c.setEvidencia(cursor.getString(cursor.getColumnIndex("evidencia")));
                c.setRfid(cursor.getString(cursor.getColumnIndex("rfid")));
                c.setIdCasaItem(cursor.getInt(cursor.getColumnIndex("idCasaItem")));
                c.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                c.setAchou(cursor.getInt(cursor.getColumnIndex("achou")));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return lista;
    }

    public int qtdeItem(int idAmbiente) {

        int qtde = 0;
        Cursor cursor = getDatabase().rawQuery("select COUNT(*) qtdeItem from checklist where idcasaitem not in(select v.idambienteitem from checklist c, checklistVolta v" +
                " WHERE c.idChecklist = v.idChecklist and c.idcasaitem=v.idambienteitem and v.respondido = 1) and idAmbiente = ?", new String[]{idAmbiente + ""});

        try {

            while (cursor.moveToNext()) {
                qtde = cursor.getInt(cursor.getColumnIndex("qtdeItem"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return qtde;
    }

    public void deletar() {
        getDatabase().delete("checklist", null, null);
    }
}

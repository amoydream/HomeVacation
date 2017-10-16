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
                values.put("idCasaItem", c.getIdCasaItem());
                values.put("evidencia", c.getEvidencia());
                values.put("estoque", c.getEstoque());

                getDatabase().insert("checklist", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CheckList> listar(int idAmbiente) {

        List<CheckList> lista = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("SELECT * FROM checklist WHERE idAmbiente = ?", new String[]{idAmbiente + ""});

        try {

            while (cursor.moveToNext()) {
                CheckList c = new CheckList();
                c.setItem(cursor.getString(cursor.getColumnIndex("item")));
                c.setEstoque(cursor.getInt(cursor.getColumnIndex("estoque")));
                c.setEpc(cursor.getString(cursor.getColumnIndex("epc")));
                c.setEvidencia(cursor.getString(cursor.getColumnIndex("evidencia")));
                c.setRfid(cursor.getString(cursor.getColumnIndex("rfid")));
                c.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return lista;
    }

    public void deletar() {
        getDatabase().delete("checklist", null, null);
    }
}

package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.FotoAmbiente;

/**
 * Created by PDA on 01/11/2017.
 */

public class FotosAmbienteDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public FotosAmbienteDao(Context context) {
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

    public int incluir(List<FotoAmbiente> lista) {

        try {
            deletar();
            for (FotoAmbiente a : lista) {
                ContentValues values = new ContentValues();
                values.put("id", a.getId());
                values.put("idAmbiente", a.getIdAmbiente());
                values.put("idCasa", a.getIdCasa());
                values.put("caminhoFoto", a.getCaminhoFoto());

                getDatabase().insert("fotoAmbiente", null, values);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }


    public List<FotoAmbiente> listar() {

        List<FotoAmbiente> lista = new ArrayList<>();

        Cursor cursor = getDatabase().rawQuery("SELECT * FROM fotoAmbiente", null);

        try {

            while (cursor.moveToNext()) {
                FotoAmbiente f = new FotoAmbiente();
                f.setIdCasa(cursor.getInt(cursor.getColumnIndex("idCasa")));
                f.setId(cursor.getInt(cursor.getColumnIndex("id")));
                f.setCaminhoFoto(cursor.getString(cursor.getColumnIndex("caminhoFoto")));
                f.setIdAmbiente(cursor.getInt(cursor.getColumnIndex("idAmbiente")));

                lista.add(f);
            }

        } catch (Exception e) {

        } finally {
            cursor.close();
        }

        return lista;
    }

    private void deletar() {
        getDatabase().delete("fotoAmbiente", null, null);
    }
}

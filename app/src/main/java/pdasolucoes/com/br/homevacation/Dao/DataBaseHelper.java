package pdasolucoes.com.br.homevacation.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PDA on 13/10/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BD = "homevacation";
    private static final int VERSAO = 2;

    public DataBaseHelper(Context context) {
        super(context, BD, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE if not exists checklist(idChecklist INTEGER, idAmbiente INTEGER,ambiente TEXT, ambienteOrdem TEXT," +
                " categoria TEXT, item TEXT, rfid TEXT, epc TEXT, evidencia TEXT, estoque INTEGER, idCasaItem INTEGER, achou INTEGER, idCasa INTEGER," +
                " estoqueParametrizado INTEGER, estoqueUltimoChecklist INTEGER)");

        db.execSQL("CREATE TABLE if not exists checklistVolta(idChecklist INTEGER,idAmbienteItem, caminhofoto TEXT, estoque INTEGER, rfid TEXT, idUsuario INTEGER," +
                " export INTEGER, respondido INTEGER, idCasa INTEGER)");

        db.execSQL("CREATE TABLE if not exists questaoChecklist(idChecklist INTEGER, idAmbiente INTEGER, ordem INTEGER, idQuestao INTEGER, questao TEXT," +
                " evidencia TEXT, idCasa INTEGER)");

        db.execSQL("CREATE TABLE if not exists questaoVolta(idCheckList INTEGER, idQuestao INTEGER, resposta TEXT, caminhoFoto TEXT, idUsuario INTEGER, export INTEGER,respondido INTEGER,idCasa INTEGER)");

        db.execSQL("CREATE TABLE if not exists epc(codigo INTEGER, epc TEXT)");

        db.execSQL("CREATE TABLE if not exists fotoAmbiente(id INTEGER, idAmbiente,caminhoFoto TEXT, idCasa INTEGER)");

        db.execSQL("CREATE TABLE if not exists respostaPendencia(descricao TEXT, caminhoFoto TEXT, idAmbienteItem, idCheckList INTEGER, qtdeEstoque INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            deleteDataBases(db);
            onCreate(db);
        }
    }

    private void deleteDataBases(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        while (cursor.moveToNext()) {
            if (!cursor.getString(0).equals("sqlite_sequence")) {
                tables.add(cursor.getString(0));
            }
        }

        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }
    }
}

package pdasolucoes.com.br.homevacation.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PDA on 13/10/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BD = "homevacation";
    private static final int VERSAO = 1;

    public DataBaseHelper(Context context) {
        super(context, BD, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE if not exists checklist(idChecklist INTEGER, idAmbiente INTEGER,ambiente TEXT, ambienteOrdem TEXT," +
                " categoria TEXT, item TEXT, rfid TEXT, epc TEXT, evidencia TEXT, estoque INTEGER, idCasaItem INTEGER)");

        db.execSQL("CREATE TABLE if not exists checklistVolta(idChecklist INTEGER,idAmbienteItem, foto BLOB, estoque INTEGER, rfid TEXT, idUsuario INTEGER," +
                " export INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
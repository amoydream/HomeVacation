package pdasolucoes.com.br.homevacation.Dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by PDA on 13/10/2017.
 */

public class ChecklistDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;


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
}

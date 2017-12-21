package pdasolucoes.com.br.homevacation.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Pendente;

/**
 * Created by PDA on 21/12/2017.
 */

public class PendenteDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public PendenteDao(Context context) {
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


    public void incluirRespostaPendencia(Pendente p) {

        ContentValues values = new ContentValues();

        values.put("descricao", p.getDescricaoRes());
        values.put("caminhoFoto", p.getCaminhoFoto());
        values.put("idAmbienteItem", p.getIdAmbienteItem());
        values.put("idCheckList", p.getIdChecklist());
        values.put("qtdeEstoque", p.getQtde());

        getDatabase().insert("respostaPendencia", null, values);

    }

    public List<Pendente> listarPendente(int idCheckList) {
        List<Pendente> lista = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("SELECT p.DESCRICAO,p.SUBDESCRICAO,p.IDCHECKLIST, p.IDAMBIENTE, p.IDAMBIENTEITEM," +
                " CASE TIPO WHEN 'S' THEN 'RFID' WHEN 'N' THEN 'STOCK' ELSE TIPO END TIPO, rp.descricao, rp.qtdeEstoque,rp.caminhoFoto FROM(" +
                " SELECT c.item DESCRICAO, c.ambiente SUBDESCRICAO, c.idchecklist IDCHECKLIST, c.idAmbiente IDAMBIENTE, c.idcasaitem IDAMBIENTEITEM, c.rfid TIPO FROM checklist c" +
                " LEFT JOIN checklistVolta cv on cv.idChecklist = c.idchecklist and c.idcasaitem = cv.idambienteitem" +
                " WHERE ((c.rfid = 'S') AND (cv.rfid = 'N'))" +
                " OR ((c.rfid = 'N') AND (c.estoqueParametrizado <> cv.estoque))" +
                " AND C.IDCHECKLIST = ?" +
                " UNION" +
                " SELECT q.questao DESCRICAO, '-' SUBDESCRICAO, q.idchecklist IDCHECKLIST, q.idAmbiente IDAMBIENTE, q.idquestao IDAMBIENTEITEM, 'QUESTION' TIPO FROM questaoChecklist q" +
                " LEFT JOIN questaoVolta qv on q.idchecklist = qv.idchecklist and q.idcasa = qv.idcasa and q.idquestao = qv.idquestao" +
                " WHERE qv.resposta = 'N' AND q.IDCHECKLIST = ?)p" +
                " LEFT JOIN respostaPendencia rp ON rp.idCheckList = p.IDCHECKLIST and rp.idAmbienteItem = p.IDAMBIENTEITEM" +
                " WHERE rp.descricao is null", new String[]{idCheckList + "", idCheckList + ""});

        try {

            while (cursor.moveToNext()) {
                Pendente p = new Pendente();

                p.setDescricao(cursor.getString(cursor.getColumnIndex("DESCRICAO")));
                p.setSubDescricao(cursor.getString(cursor.getColumnIndex("SUBDESCRICAO")));
                p.setIdChecklist(cursor.getInt(cursor.getColumnIndex("IDCHECKLIST")));
                p.setIdAmbiente(cursor.getInt(cursor.getColumnIndex("IDAMBIENTE")));
                p.setIdAmbienteItem(cursor.getInt(cursor.getColumnIndex("IDAMBIENTEITEM")));
                p.setTipo(cursor.getString(cursor.getColumnIndex("TIPO")));

                lista.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return lista;
    }
}

package pdasolucoes.com.br.homevacation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pdasolucoes.com.br.homevacation.Adapter.ListaPendenteAdapter;
import pdasolucoes.com.br.homevacation.Dao.CheckListVoltaDao;
import pdasolucoes.com.br.homevacation.Dao.ChecklistDao;
import pdasolucoes.com.br.homevacation.Dao.PendenteDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.Pendente;

/**
 * Created by PDA on 21/12/2017.
 */

public class PendenteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListaPendenteAdapter adapter;
    private PendenteDao pendenteDao;
    private TextView tvTitulo;
    private AlertDialog dialog, dialogDescreva, dialogQtde;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        pendenteDao = new PendenteDao(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvTitulo = (TextView) findViewById(R.id.tvtTituloToolbar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        tvTitulo.setText(getString(R.string.pending));

        atualizarLista();
    }


//    private void popupConfirmAction(final Pendente p) {
//        View v = View.inflate(PendenteActivity.this, R.layout.popup_action_confirm, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(PendenteActivity.this);
//        builder.setView(v);
//
//        Button btOk, btNok, btCancel;
//
//        btNok = (Button) v.findViewById(R.id.btNok);
//        btOk = (Button) v.findViewById(R.id.btOk);
//        btCancel = (Button) v.findViewById(R.id.btCancel);
//
//        btNok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//            }
//        });
//
//        btOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                popupDescreva(p);
//                dialog.dismiss();
//            }
//        });
//
//        btCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog = builder.create();
//        dialog.show();
//    }

    private void popupDescreva(final Pendente p) {
        View v = View.inflate(PendenteActivity.this, R.layout.popup_descreva_acao, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PendenteActivity.this);
        builder.setView(v);

        Button btDone = (Button) v.findViewById(R.id.btDone);
        final TextInputEditText editDescricao = (TextInputEditText) v.findViewById(R.id.editDescrica);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editDescricao.getText().toString().equals("")) {
                    p.setDescricaoRes(editDescricao.getText().toString());
                    pendenteDao.incluirRespostaPendencia(p);
                    dialogDescreva.dismiss();

                    atualizarLista();
                } else {
                    Toast.makeText(PendenteActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogDescreva = builder.create();
        dialogDescreva.show();
    }

    private void popupInsereQtde(final Pendente p) {
        View v = View.inflate(PendenteActivity.this, R.layout.popup_insere_qtde_descricao, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PendenteActivity.this);
        builder.setView(v);
        final TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
        final TextInputEditText editeDesc = (TextInputEditText) v.findViewById(R.id.editDescrica);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        TextView tvParameter = (TextView) v.findViewById(R.id.parameterized);
        TextView lastCount = (TextView) v.findViewById(R.id.last_count);

        ChecklistDao checklistDao = new ChecklistDao(PendenteActivity.this);


        tvParameter.setText(getString(R.string.parameterized) + " " + checklistDao.qtdeParametrizado(p.getIdAmbienteItem()).getCheckListParametrizado());
        if (checklistDao.qtdeParametrizado(p.getIdAmbienteItem()).getCheckListUltimoParametrizado() == -1) {
            lastCount.setText(getString(R.string.last_count) + " " + 0);
        } else {
            lastCount.setText(getString(R.string.last_count) + " " + checklistDao.qtdeParametrizado(p.getIdAmbienteItem()).getCheckListUltimoParametrizado());
        }


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQtde.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editQtde.getText().toString().equals("") || !editeDesc.getText().toString().equals("")) {
                    p.setQtde(Integer.parseInt(editQtde.getText().toString()));
                    p.setDescricaoRes((editeDesc.getText().toString()));
                    pendenteDao.incluirRespostaPendencia(p);
                    dialogQtde.dismiss();
                    atualizarLista();
                } else {
                    Toast.makeText(PendenteActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialogQtde = builder.create();
        dialogQtde.show();
    }

    private void atualizarLista() {

        if (pendenteDao.listarPendente(getIntent().getIntExtra("ID_CHECKLIST", 0)).size() > 0) {

            adapter = new ListaPendenteAdapter(this, pendenteDao.listarPendente(getIntent().getIntExtra("ID_CHECKLIST", 0)));
            recyclerView.setAdapter(adapter);

            adapter.ItemClickListener(new ListaPendenteAdapter.ItemClick() {
                @Override
                public void onClick(int position) {
                    Pendente p = pendenteDao.listarPendente(getIntent().getIntExtra("ID_CHECKLIST", 0)).get(position);
                    if (p.getTipo().equals("RFID") || p.getTipo().equals("QUESTION")) {
                        popupDescreva(p);
                    } else {
                        popupInsereQtde(p);
                    }
                }
            });
        } else {
            Intent i = new Intent(PendenteActivity.this, CheckListAmbienteActivity.class);
            i.putExtra("PENDENTE_OK", "PENDENTE_OK");
            i.putExtra("ID_CHECKLIST", getIntent().getIntExtra("ID_CHECKLIST", 0));
            i.putExtra("ID_CASA",getIntent().getIntExtra("ID_CASA", 0));
            startActivity(i);
            finish();
        }


    }
}

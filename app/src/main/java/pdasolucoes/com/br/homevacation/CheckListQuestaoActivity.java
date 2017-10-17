package pdasolucoes.com.br.homevacation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistQuestaoAdapter;
import pdasolucoes.com.br.homevacation.Dao.QuestaoDao;
import pdasolucoes.com.br.homevacation.Dao.QuestaoVoltaDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckList;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckListVolta;
import pdasolucoes.com.br.homevacation.Service.CheckListService;
import pdasolucoes.com.br.homevacation.Util.ListaAmbienteDao;
import pdasolucoes.com.br.homevacation.Util.TransformarImagem;

/**
 * Created by PDA on 17/10/2017.
 */

public class CheckListQuestaoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTituloBar;
    private Ambiente ambiente;
    private List<QuestaoCheckList> listaQuestaoChecklist;
    private QuestaoDao questaoDao;
    private QuestaoVoltaDao questaoVoltaDao;
    private QuestaoCheckListVolta questaoCheckListVolta;
    private ListaChecklistQuestaoAdapter adapter;
    private ProgressDialog progressDialog, progressDialog2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        questaoDao = new QuestaoDao(this);
        questaoVoltaDao = new QuestaoVoltaDao(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvTituloBar = (TextView) findViewById(R.id.tvtTituloToolbar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        ambiente = (Ambiente) getIntent().getSerializableExtra("ambiente");

        tvTituloBar.setText(ambiente.getDescricao());

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        AsyncQuestao task = new AsyncQuestao();
        task.execute();
    }

    private class AsyncQuestao extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CheckListQuestaoActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.load));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            listaQuestaoChecklist = CheckListService.GetCheckListQuestao(getIntent().getIntExtra("ID_CHECKLIST", 0));

            questaoDao.incluir(listaQuestaoChecklist);

            return questaoDao.listar(ambiente.getId());
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                adapter = new ListaChecklistQuestaoAdapter((List<QuestaoCheckList>) o, CheckListQuestaoActivity.this);
                recyclerView.setAdapter(adapter);

                adapter.ItemClickListener(new ListaChecklistQuestaoAdapter.ItemClick() {
                    @Override
                    public void onClick(int position) {
                        popupRespoteQuestao(position);
                    }
                });
            }
        }
    }


    private void popupRespoteQuestao(int position) {

        View v = View.inflate(CheckListQuestaoActivity.this, R.layout.popup_responde_questao, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListQuestaoActivity.this);
        final AlertDialog dialog;
        builder.setView(v);

        ImageView imageCamera = (ImageView) v.findViewById(R.id.imageCamera);
        final TextInputEditText editResposta = (TextInputEditText) v.findViewById(R.id.editResposta);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        dialog = builder.create();


        QuestaoCheckList q = questaoDao.listar(ambiente.getId()).get(position);


        if (!q.getEvidencia().equals("S")) {
            imageCamera.setVisibility(View.GONE);
        } else {
            imageCamera.setVisibility(View.VISIBLE);
        }

        questaoCheckListVolta = new QuestaoCheckListVolta();
        questaoCheckListVolta.setIdChecklist(getIntent().getIntExtra("ID_CHECKLIST", 0));
        questaoCheckListVolta.setIdUsuario(1);
        questaoCheckListVolta.setIdQuestao(q.getIdQuestao());

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editResposta.getText().toString().equals("")) {
                    questaoCheckListVolta.setResposta(editResposta.getText().toString());
                    dialog.dismiss();

                    AsyncSetQuestao task = new AsyncSetQuestao();
                    task.execute(questaoCheckListVolta);

                } else {
                    Toast.makeText(CheckListQuestaoActivity.this, getString(R.string.preencha_campo), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                questaoCheckListVolta.setFoto(TransformarImagem.getBitmapAsByteArray(photo));
            }
        }
    }

    private class AsyncSetQuestao extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog2 = new ProgressDialog(CheckListQuestaoActivity.this);
            progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog2.setMessage(getString(R.string.load));
            progressDialog2.setCanceledOnTouchOutside(true);
            progressDialog2.setCancelable(false);
            progressDialog2.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            int result = questaoVoltaDao.incluir(questaoCheckListVolta);
//            int result = CheckListService.SetChecklistQuestao((QuestaoCheckListVolta) params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog2.isShowing()) {
                progressDialog2.dismiss();
                if (Integer.parseInt(o.toString()) == 1) {
                    questaoVoltaDao.export(questaoCheckListVolta);

                    adapter = new ListaChecklistQuestaoAdapter(questaoDao.listar(ambiente.getId()), CheckListQuestaoActivity.this);
                    recyclerView.setAdapter(adapter);

                    adapter.ItemClickListener(new ListaChecklistQuestaoAdapter.ItemClick() {
                        @Override
                        public void onClick(int position) {
                            popupRespoteQuestao(position);
                        }
                    });
                }

                if (questaoDao.listar(ambiente.getId()).size() == 0) {
                    popupFinish();
                }
            }
        }
    }

    public void popupFinish() {
        final SharedPreferences sharedPreferences = getSharedPreferences("listaAmbiente", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        View v = View.inflate(CheckListQuestaoActivity.this, R.layout.popup_prox_question, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListQuestaoActivity.this);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        TextView tvConteudo = (TextView) v.findViewById(R.id.conteudo);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);

        tvConteudo.setText(getString(R.string.finish_room));

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("lista", ListaAmbienteDao.alterar(ListaAmbienteDao.listar(sharedPreferences.getString("lista", "")), ambiente)).commit();
                CheckListAmbienteActivity.AmbienteActivity.finish();
                Intent i = new Intent(CheckListQuestaoActivity.this, CheckListAmbienteActivity.class);
                i.putExtra("FINISH_ROOM", "FINISH_ROOM");
                i.putExtra("ID_CHECKLIST", getIntent().getIntExtra("ID_CHECKLIST", 0));
                startActivity(i);
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}

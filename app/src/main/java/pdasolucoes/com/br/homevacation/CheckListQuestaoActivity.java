package pdasolucoes.com.br.homevacation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistQuestaoAdapter;
import pdasolucoes.com.br.homevacation.Dao.QuestaoDao;
import pdasolucoes.com.br.homevacation.Dao.QuestaoVoltaDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckList;
import pdasolucoes.com.br.homevacation.Model.QuestaoCheckListVolta;
import pdasolucoes.com.br.homevacation.Service.CheckListService;
import pdasolucoes.com.br.homevacation.Util.ListaAmbienteDao;
import pdasolucoes.com.br.homevacation.Util.SDCardUtils;
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
    private int flag = 0, POSITION = -1;
    private ProgressDialog progressDialog, progressDialog2;
    private File file;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        preferences = getSharedPreferences("Login", MODE_PRIVATE);

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

        if (savedInstanceState != null) {
            file = (File) savedInstanceState.getSerializable("file");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (POSITION > -1 && flag == 0) {
            popupRespoteQuestao(POSITION);
            flag = 1;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("file", file);
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
                        questaoCheckListVolta = new QuestaoCheckListVolta();
                        questaoCheckListVolta.setIdCasa(ambiente.getIdCasa());
                        popupRespoteQuestao(position);
                    }
                });

                if (questaoDao.listar(ambiente.getId()).size() == 0) {
                    popupFinish();
                }
            }
        }
    }


    private void popupRespoteQuestao(int position) {
        POSITION = position;
        View v = View.inflate(CheckListQuestaoActivity.this, R.layout.popup_responde_questao, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListQuestaoActivity.this);
        final AlertDialog dialog;
        builder.setView(v);

        ImageView imageCamera = (ImageView) v.findViewById(R.id.imageCamera);
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupAnswer);
        RadioButton radioYes = (RadioButton) v.findViewById(R.id.radioYes);
        RadioButton radioNo = (RadioButton) v.findViewById(R.id.radioNo);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        dialog = builder.create();


        final QuestaoCheckList q = questaoDao.listar(ambiente.getId()).get(position);


        if (!q.getEvidencia().equals("S")) {
            imageCamera.setVisibility(View.GONE);
        } else {
            imageCamera.setVisibility(View.VISIBLE);
        }

        questaoCheckListVolta.setIdChecklist(getIntent().getIntExtra("ID_CHECKLIST", 0));
        questaoCheckListVolta.setIdUsuario(preferences.getInt("idUsuario", 0));
        questaoCheckListVolta.setIdQuestao(q.getIdQuestao());

        if (questaoCheckListVolta.getCaminhoFoto() != null) {
            imageCamera.setImageResource(R.drawable.ic_camera_alt_green_24dp);
        }

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeImagem = System.currentTimeMillis() + ".jpg";
                file = SDCardUtils.getPrivateFile(getBaseContext(), nomeImagem, Environment.DIRECTORY_PICTURES);
                // Chama a intent informando o arquivo para salvar a foto
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Context context = getBaseContext();
                Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, 0);
                dialog.dismiss();

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag = 0;
                dialog.dismiss();
            }
        });

        if (!questaoCheckListVolta.getResposta().equals("")) {
            if (questaoCheckListVolta.getResposta().equals("No")) {
                radioNo.setChecked(true);
            } else {
                radioYes.setChecked(true);
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton r = (RadioButton) group.findViewById(checkedId);
                String resposta = "";

                if (r.getText().toString().equals("No")) {
                    resposta = "N";
                } else {
                    resposta = "S";
                }

                questaoCheckListVolta.setResposta(resposta);
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (q.getEvidencia().equals("S") && (questaoCheckListVolta.getCaminhoFoto() == null)) {
                    Toast.makeText(CheckListQuestaoActivity.this, getString(R.string.take_picture), Toast.LENGTH_SHORT).show();
                } else {

                    if (!questaoCheckListVolta.getResposta().equals("")) {
                        dialog.dismiss();
                        AsyncSetQuestao task = new AsyncSetQuestao();
                        task.execute(questaoCheckListVolta);

                    } else {
                        Toast.makeText(CheckListQuestaoActivity.this, getString(R.string.preencha_campo), Toast.LENGTH_SHORT).show();
                    }
                    flag = 0;
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
                if (file != null && file.exists()) {
                    Log.d("foto", file.getAbsolutePath());

                    Uri imageUri = Uri.fromFile(file);
                    questaoCheckListVolta.setCaminhoFoto(file.getPath());

                    Intent i = new Intent(CheckListQuestaoActivity.this, PopupImage.class);
                    i.putExtra("imageUri", imageUri);
                    startActivity(i);
                }
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
                            questaoCheckListVolta = new QuestaoCheckListVolta();
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

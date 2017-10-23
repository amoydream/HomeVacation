package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistItemAdapter;
import pdasolucoes.com.br.homevacation.Dao.CheckListVoltaDao;
import pdasolucoes.com.br.homevacation.Dao.ChecklistDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.CheckList;
import pdasolucoes.com.br.homevacation.Model.CheckListVolta;
import pdasolucoes.com.br.homevacation.Service.CheckListService;
import pdasolucoes.com.br.homevacation.Util.ImageResizeUtils;
import pdasolucoes.com.br.homevacation.Util.SDCardUtils;
import pdasolucoes.com.br.homevacation.Util.TransformarImagem;

/**
 * Created by PDA on 13/10/2017.
 */

public class CheckListItemActivity extends AppCompatActivity {

    private TextView tvTitulo;
    List<CheckList> listaCheckList;
    private ListaChecklistItemAdapter adapter;
    private RecyclerView recyclerView;
    private Ambiente ambiente;
    private ChecklistDao checklistDao;
    private CheckListVoltaDao checkListVoltaDao;
    private CheckListVolta checkListVolta;
    private ProgressDialog progressDialog, progressDialog2;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        listaCheckList = new ArrayList<>();
        checklistDao = new ChecklistDao(this);
        checkListVoltaDao = new CheckListVoltaDao(this);
        tvTitulo = (TextView) findViewById(R.id.tvtTituloToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        ambiente = (Ambiente) getIntent().getSerializableExtra("ambiente");
        tvTitulo.setText(ambiente.getDescricao());

        AsyncChecklistItem task = new AsyncChecklistItem();
        task.execute();

        if (savedInstanceState != null) {
            file = (File) savedInstanceState.getSerializable("file");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("file", file);
    }

    public class AsyncChecklistItem extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CheckListItemActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.load));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

//            listaCheckList = CheckListService.GetListaCheckListItens(getIntent().getIntExtra("ID_CHECKLIST", 0));
//
//            checklistDao.incluir(listaCheckList);

            return checklistDao.listar(ambiente.getId());
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                adapter = new ListaChecklistItemAdapter((List<CheckList>) o, CheckListItemActivity.this);
                recyclerView.setAdapter(adapter);

                adapter.ItemClickListener(new ListaChecklistItemAdapter.ItemClick() {
                    @Override
                    public void onClick(int position) {
                        popupAction(position);
                    }
                });
            }
        }
    }

    public void popupAction(int position) {
        View v = View.inflate(CheckListItemActivity.this, R.layout.popup_action, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemActivity.this);
        ImageView imageRfid, imageCamera, imageEstoque;
        Button btDone, btCancel;
        imageRfid = (ImageView) v.findViewById(R.id.imageRfid);
        imageCamera = (ImageView) v.findViewById(R.id.imageCamera);
        imageEstoque = (ImageView) v.findViewById(R.id.imageEstoque);
        btDone = (Button) v.findViewById(R.id.btDone);
        btCancel = (Button) v.findViewById(R.id.btCancel);
        final AlertDialog dialog;
        builder.setView(v);


        checkListVolta = new CheckListVolta();
        checkListVolta.setIdChecklist(getIntent().getIntExtra("ID_CHECKLIST", 0));
        checkListVolta.setIdUsuario(1);
        checkListVolta.setIdAmbienteItem(checklistDao.listar(ambiente.getId()).get(position).getIdCasaItem());

        dialog = builder.create();
        dialog.show();

        final CheckList c = checklistDao.listar(ambiente.getId()).get(position);
        if (c.getRfid().equals("S")) {
            imageRfid.setVisibility(View.VISIBLE);
        } else {
            imageRfid.setVisibility(View.GONE);
        }

        if (c.getEvidencia().equals("S")) {
            imageCamera.setVisibility(View.VISIBLE);
        } else {
            imageCamera.setVisibility(View.GONE);
        }

        if (c.getEstoque() > 1) {
            imageEstoque.setVisibility(View.VISIBLE);
        } else {
            imageEstoque.setVisibility(View.GONE);
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
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, 0);

//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    try {
//                        File diretorio = Environment
//                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                        String nomeImagem = diretorio.getPath() + "/"
//                                + System.currentTimeMillis() + ".png";
//
//                        file = new File(nomeImagem);
//                        uriImagem = FileProvider.getUriForFile(CheckListItemActivity.this,
//                                BuildConfig.APPLICATION_ID + ".provider", file);
//
//                        mCurrentPhotoPath = "file:" + file.getAbsolutePath();
//
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagem);
//                        startActivityForResult(intent, 0);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });

        imageEstoque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupQuantidade();
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

                if (c.getEvidencia().equals("S") && checkListVolta.getCaminhoFoto() == null) {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.take_picture), Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    AsynSetCheckList task = new AsynSetCheckList();
                    task.execute(checkListVolta);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                if (file != null && file.exists()) {
                    Log.d("foto", file.getAbsolutePath());

                    Uri imageUri = Uri.fromFile(file);
                    checkListVolta.setCaminhoFoto(file.getPath());

                    Intent i = new Intent(CheckListItemActivity.this, PopupImage.class);
                    i.putExtra("imageUri", imageUri);
                    startActivity(i);
                }


            }
        }


    }

    public class AsynSetCheckList extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog2 = new ProgressDialog(CheckListItemActivity.this);
            progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog2.setMessage(getString(R.string.load));
            progressDialog2.setCanceledOnTouchOutside(true);
            progressDialog2.setCancelable(false);
            progressDialog2.show();

        }

        @Override
        protected Object doInBackground(Object[] params) {

            int result = checkListVoltaDao.incluir(checkListVolta);
            //int result = CheckListService.SetChecklistItem((CheckListVolta) params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog2.isShowing()) {
                progressDialog2.dismiss();
                if (Integer.parseInt(o.toString()) == 1) {
                    checkListVoltaDao.export(checkListVolta);


                    //atualizando a lista
                    adapter = new ListaChecklistItemAdapter(checklistDao.listar(ambiente.getId()), CheckListItemActivity.this);
                    recyclerView.setAdapter(adapter);

                    adapter.ItemClickListener(new ListaChecklistItemAdapter.ItemClick() {
                        @Override
                        public void onClick(int position) {
                            popupAction(position);
                        }
                    });
                }

                if (checklistDao.listar(ambiente.getId()).size() == 0) {
                    popupQuestion();
                }
            }
        }
    }

    public void popupQuantidade() {
        View v = View.inflate(CheckListItemActivity.this, R.layout.popup_insere_qtde, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemActivity.this);
        final TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editQtde.getText().toString().equals("")) {
                    checkListVolta.setEstoque(Integer.parseInt(editQtde.getText().toString()));
                    dialog.dismiss();
                } else {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.preencha_campo), Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139) {
            Toast.makeText(CheckListItemActivity.this, "Olha o RFID ai hahaha", Toast.LENGTH_SHORT).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void popupQuestion() {
        View v = View.inflate(CheckListItemActivity.this, R.layout.popup_prox_question, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemActivity.this);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        TextView tvConteudo = (TextView) v.findViewById(R.id.conteudo);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);

        tvConteudo.setText(getString(R.string.now));

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CheckListItemActivity.this, CheckListQuestaoActivity.class);
                i.putExtra("ambiente", ambiente);
                i.putExtra("ID_CHECKLIST", getIntent().getIntExtra("ID_CHECKLIST", 0));
                startActivity(i);
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

}

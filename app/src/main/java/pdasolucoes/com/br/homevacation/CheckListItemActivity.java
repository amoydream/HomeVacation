package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.annotation.Check;
import com.rscja.deviceapi.RFIDWithUHF;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ImagesAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistItemAdapter;
import pdasolucoes.com.br.homevacation.Dao.CheckListVoltaDao;
import pdasolucoes.com.br.homevacation.Dao.ChecklistDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.CheckList;
import pdasolucoes.com.br.homevacation.Model.CheckListAmbienteResposta;
import pdasolucoes.com.br.homevacation.Model.CheckListVolta;
import pdasolucoes.com.br.homevacation.Model.FotoAmbiente;
import pdasolucoes.com.br.homevacation.Service.AmbienteService;
import pdasolucoes.com.br.homevacation.Service.CheckListService;
import pdasolucoes.com.br.homevacation.Util.ImageResizeUtils;
import pdasolucoes.com.br.homevacation.Util.ListaAmbienteDao;
import pdasolucoes.com.br.homevacation.Util.SDCardUtils;
import pdasolucoes.com.br.homevacation.Util.TransformarImagem;

/**
 * Created by PDA on 13/10/2017.
 */

public class CheckListItemActivity extends AppCompatActivity {

    private TextView tvTitulo;
    private List<CheckList> listaCheckList;
    private ListaChecklistItemAdapter adapter;
    private RecyclerView recyclerView;
    private Ambiente ambiente;
    private ChecklistDao checklistDao;
    private CheckListVoltaDao checkListVoltaDao;
    private CheckListVolta checkListVolta;
    private ProgressDialog progressDialog, progressDialog2, dialog;
    private AlertDialog dialog2, dialog1;
    private File file;
    private Handler handler;
    private int POSITION = -1, flag = 0;
    private boolean loopFlag = false;
    private RFIDWithUHF mReader;
    private List<String> listaEpcs;
    private SharedPreferences preferences;
    private List<CheckListAmbienteResposta> listAmbienteRespostas;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        preferences = getSharedPreferences("Login", MODE_PRIVATE);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        listaEpcs = new ArrayList<>();
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

        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {

            Toast.makeText(this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (mReader != null) {
            InitTask initTask = new InitTask();
            initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


        List<FotoAmbiente> fotoAmbientes = (List<FotoAmbiente>) getIntent().getSerializableExtra("fotoAmbiente");

        if (fotoAmbientes.size() != 0) {
            new AsyncFotos().execute(fotoAmbientes);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);


                String result = msg.obj + "";
                if (!listaEpcs.contains(result)) {
                    listaEpcs.add(result);
                }
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (POSITION > -1 && flag == 0) {
            popupAction(POSITION);
            flag = 1;
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
                        //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
                        checkListVolta = new CheckListVolta();
                        checkListVolta.setIdCasa(ambiente.getIdCasa());
                        //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
                        //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
                        checkListVolta.setEstoque(-1);
                        popupAction(position);
                    }
                });

                if (checklistDao.listar(ambiente.getId()).size() == 0) {
                    popupQuestion();
                }
            }
        }
    }

    public void popupAction(final int position) {
        POSITION = position;
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

        checkListVolta.setIdChecklist(getIntent().getIntExtra("ID_CHECKLIST", 0));
        checkListVolta.setIdUsuario(preferences.getInt("idUsuario", 0));
        checkListVolta.setIdAmbienteItem(checklistDao.listar(ambiente.getId()).get(position).getIdCasaItem());

        dialog = builder.create();
        dialog.show();

        final CheckList c = checklistDao.listar(ambiente.getId()).get(position);

        if (c.getRfid().equals("S") && !(c.getAchou() == 1)) {
            imageRfid.setVisibility(View.VISIBLE);
        } else {
            imageRfid.setImageResource(R.drawable.ic_rfid_chip_green);
            imageRfid.setVisibility(View.GONE);
        }

        if (c.getEvidencia().equals("S")) {
            imageCamera.setVisibility(View.VISIBLE);
        } else {
            imageCamera.setVisibility(View.GONE);
        }

        if (checkListVolta.getCaminhoFoto() != null) {
            imageCamera.setImageResource(R.drawable.ic_camera_alt_green_24dp);
        }

        if (c.getEstoque() > 1) {
            imageEstoque.setVisibility(View.VISIBLE);
        } else {
            imageEstoque.setVisibility(View.GONE);
        }

        if (checkListVolta.getEstoque() > -1) {
            imageEstoque.setImageResource(R.drawable.ic_warehouse_green);
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
                flag = 0;
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, 0);
                dialog.dismiss();

            }
        });

        imageEstoque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                popupQuantidade(position);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                dialog.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (c.getEvidencia().equals("S") && checkListVolta.getCaminhoFoto() == null) {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.take_picture), Toast.LENGTH_SHORT).show();
                } else if (c.getEstoque() > 1 && checkListVolta.getEstoque() == -1) {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.preencha_campo), Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    if (c.getRfid().equals("S")) {
                        if (c.getAchou() == 1) {
                            checkListVolta.setRfid("S");
                        } else {
                            //rever, do jeito q está, mesmo sim como não ele envia a volta
                            checkListVolta.setRfid("N");
                        }
                    }
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

            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog2.isShowing()) {
                progressDialog2.dismiss();

                if (Integer.parseInt(o.toString()) == 1) {
                    checkListVoltaDao.respondido(checkListVolta);

                    //atualizando a lista
                    adapter = new ListaChecklistItemAdapter(checklistDao.listar(ambiente.getId()), CheckListItemActivity.this);
                    recyclerView.setAdapter(adapter);

                    adapter.ItemClickListener(new ListaChecklistItemAdapter.ItemClick() {
                        @Override
                        public void onClick(int position) {
                            //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
                            checkListVolta = new CheckListVolta();
                            checkListVolta.setIdCasa(ambiente.getIdCasa());
                            //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
                            //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
                            checkListVolta.setEstoque(-1);
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

    public void popupQuantidade(final int position) {
        View v = View.inflate(CheckListItemActivity.this, R.layout.popup_insere_qtde, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemActivity.this);
        final TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        TextView tvParameter = (TextView) v.findViewById(R.id.parameterized);
        TextView lastCount = (TextView) v.findViewById(R.id.last_count);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();

        tvParameter.setText(getString(R.string.parameterized) + " " + checklistDao.listar(ambiente.getId()).get(position).getCheckListParametrizado());
        if (checklistDao.listar(ambiente.getId()).get(position).getCheckListUltimoParametrizado() == -1) {
            lastCount.setText(getString(R.string.last_count) + " " + 0);
        } else {
            lastCount.setText(getString(R.string.last_count) + " " + checklistDao.listar(ambiente.getId()).get(position).getCheckListUltimoParametrizado());
        }


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
                    popupAction(position);
                } else {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.preencha_campo), Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
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


    class TagThread extends Thread {

        private int mBetween = 80;
        HashMap<String, String> map;

        public TagThread(int iBetween) {
            mBetween = iBetween;
        }

        public void run() {

            String[] res;

            while (loopFlag) {

                res = mReader.readTagFormBuffer();

                if (res != null) {

                    Message msg = handler.obtainMessage();
                    msg.obj = res[1];
                    handler.sendMessage(msg);
                }

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {


                        if (keyCode == 139) {
                            dialog.dismiss();
                            stopInventory();

                            List<CheckList> listacoletados = existe(checklistDao.listar(ambiente.getId()), listaEpcs);

                            for (CheckList c : listacoletados) {
                                checklistDao.achou(c);

                                if (!c.getRfid().equals("") && c.getEvidencia().equals("N") && c.getEstoque() <= 1) {
                                    //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
                                    checkListVolta = new CheckListVolta();
                                    checkListVolta.setIdCasa(ambiente.getIdCasa());
                                    //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
                                    //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
                                    checkListVolta.setEstoque(-1);
                                    checkListVolta.setRfid("S");
                                    checkListVolta.setIdChecklist(getIntent().getIntExtra("ID_CHECKLIST", 0));
                                    checkListVolta.setIdUsuario(preferences.getInt("idUsuario", 0));
                                    checkListVolta.setIdAmbienteItem(c.getIdCasaItem());

                                    checkListVoltaDao.incluir(checkListVolta);
                                    checkListVoltaDao.respondido(checkListVolta);
                                }

                                adapter = new ListaChecklistItemAdapter(checklistDao.listar(ambiente.getId()), CheckListItemActivity.this);
                                recyclerView.setAdapter(adapter);

                                adapter.ItemClickListener(new ListaChecklistItemAdapter.ItemClick() {
                                    @Override
                                    public void onClick(int position) {
                                        //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
                                        checkListVolta = new CheckListVolta();
                                        checkListVolta.setIdCasa(ambiente.getIdCasa());
                                        //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
                                        //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
                                        checkListVolta.setEstoque(-1);
                                        popupAction(position);
                                    }
                                });

                                if (checklistDao.listar(ambiente.getId()).size() == 0) {
                                    popupQuestion();
                                }
                            }


                            return true;
                        }
                        return false;
                    }
                });

                try {
                    sleep(mBetween);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {

        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139) {

            readTag();
        }

        return super.onKeyDown(keyCode, event);
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;
        boolean test;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(CheckListItemActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mypDialog.cancel();

            if (!result) {
                Toast.makeText(CheckListItemActivity.this, "Init fail",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(CheckListItemActivity.this, "Init OK",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void readTag() {
        dialog = new ProgressDialog(CheckListItemActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.load));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.show();
        if (mReader.startInventoryTag((byte) 0, (byte) 0)) {
            loopFlag = true;
            new TagThread(10).start();
        } else {
            mReader.stopInventory();
            Toast.makeText(CheckListItemActivity.this, "Open Failure", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopInventory() {

        if (loopFlag) {

            loopFlag = false;

            if (!mReader.stopInventory()) {
                Toast.makeText(CheckListItemActivity.this, "Falha", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public List<CheckList> existe(List<CheckList> listaCheck, List<String> listaString) {

        List<CheckList> listVoltas = new ArrayList<>();
        for (CheckList c : listaCheck) {
            //existe sem o inicio 3000
            if (listaString.contains(c.getEpc())) {
                listVoltas.add(new CheckList(c.getId(), c.getRfid(), c.getIdCasaItem(), c.getEvidencia(), c.getEstoque()));
                //existe com o inicio 3000
            } else if (listaString.contains("3000" + c.getEpc())) {
                listVoltas.add(new CheckList(c.getId(), c.getRfid(), c.getIdCasaItem(), c.getEvidencia(), c.getEstoque()));
            }
        }
        return listVoltas;
    }


    private void popupPicture(List<FotoAmbiente> fotoAmbientes) {
        View v = View.inflate(this, R.layout.popup_picture_list, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        RecyclerView recyclerView2 = (RecyclerView) v.findViewById(R.id.recyclerView);
        ImagesAmbienteAdapter adapter2;
        builder.setView(v);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        listAmbienteRespostas = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView2.setLayoutManager(llm);

        adapter2 = new ImagesAmbienteAdapter(fotoAmbientes, CheckListItemActivity.this);
        recyclerView2.setAdapter(adapter2);

        dialog1 = builder.create();

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup descricao
                popupDescrive();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });


        dialog1.show();
    }


    private class AsyncFotos extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            URL url;
            Bitmap bmp = null;
            List<FotoAmbiente> lista = (List<FotoAmbiente>) params[0];
            for (int i = 0; i < lista.size(); i++) {
                try {
                    url = new URL(lista.get(i).getFotoRetornoString());
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FotoAmbiente f = lista.get(i);
                f.setBitmap(bmp);

                lista.set(i, f);
            }
            return lista;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            popupPicture((List<FotoAmbiente>) o);

        }
    }


    private void popupDescrive() {
        View v = View.inflate(CheckListItemActivity.this, R.layout.popup_edittext, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemActivity.this);
        final TextInputEditText textInputEditText = (TextInputEditText) v.findViewById(R.id.editDescrica);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        builder.setView(v);
        dialog2 = builder.create();
        dialog2.show();

        final CheckListAmbienteResposta car = new CheckListAmbienteResposta();

        car.setIdChecklist(getIntent().getIntExtra("ID_CHECKLIST", 0));
        car.setIdUsuario(preferences.getInt("idUsuario", 0));
        car.setIdAmbiente(ambiente.getId());

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textInputEditText.getText().toString().equals("")) {
                    //pego o texto e envio para o serviço
                    car.setResposta(textInputEditText.getText().toString());

                    listAmbienteRespostas.add(car);

                    AsyncRespostaAmbiente task = new AsyncRespostaAmbiente();
                    task.execute(listAmbienteRespostas);
                    //service

                } else {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class AsyncRespostaAmbiente extends AsyncTask {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CheckListItemActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            return AmbienteService.setListaResposta((List<CheckListAmbienteResposta>) params[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();

                if (Integer.parseInt(o.toString()) == 0) {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.error_insert), Toast.LENGTH_SHORT).show();
                } else {
                    dialog2.dismiss();
                    dialog1.dismiss();
                }
            }

        }
    }
}


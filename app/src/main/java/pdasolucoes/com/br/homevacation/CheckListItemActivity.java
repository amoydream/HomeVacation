package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.BATCH_MODE;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.TAG_FIELD;
import com.zebra.rfid.api3.TagData;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pdasolucoes.com.br.homevacation.application.Application;
import pdasolucoes.com.br.homevacation.application.Constants;
import pdasolucoes.com.br.homevacation.application.CustomProgressDialog;
import pdasolucoes.com.br.homevacation.application.DataExportTask;
import pdasolucoes.com.br.homevacation.application.Inventorytimer;
import pdasolucoes.com.br.homevacation.application.ResponseHandlerInterfaces;
import pdasolucoes.com.br.homevacation.Adapter.ImagesAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistItemAdapter;
import pdasolucoes.com.br.homevacation.Dao.CheckListVoltaDao;
import pdasolucoes.com.br.homevacation.Dao.ChecklistDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.CheckList;
import pdasolucoes.com.br.homevacation.Model.CheckListAmbienteResposta;
import pdasolucoes.com.br.homevacation.Model.CheckListVolta;
import pdasolucoes.com.br.homevacation.Model.FotoAmbiente;
import pdasolucoes.com.br.homevacation.Model.InventoryListItem;
import pdasolucoes.com.br.homevacation.Service.AmbienteService;
import pdasolucoes.com.br.homevacation.Util.SDCardUtils;

/**
 * Created by PDA on 13/10/2017.
 */

public class CheckListItemActivity extends AppCompatActivity implements ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.ResponseTagHandler, Readers.RFIDReaderEventHandler {

    //Messages for progress bar
    public static Timer t;
    private static ArrayList<ResponseHandlerInterfaces.BluetoothDeviceFoundHandler> bluetoothDeviceFoundHandlers = new ArrayList<>();
    protected boolean isInventoryAborted;
    protected boolean isLocationingAborted;
    protected int accessTagCount;
    //To indicate indeterminate progress
    protected CustomProgressDialog progressDialogZebra;
    private Boolean isTriggerRepeat;
    private boolean pc = false;
    private boolean rssi = false;
    private boolean phase = false;
    private boolean channelIndex = false;
    private boolean tagSeenCount = false;
    private boolean isDeviceDisconnected = false;
    private AsyncTask<Void, Void, Boolean> DisconnectTask;
    public static ArrayList<ReaderDevice> readersList = new ArrayList<>();
    private DeviceConnectTask deviceConnectTask;

    private TextView tvTitulo;
    private List<CheckList> listaCheckList;
    private ListaChecklistItemAdapter adapter;
    private RecyclerView recyclerView;
    private Ambiente ambiente;
    private ChecklistDao checklistDao;
    private CheckListVoltaDao checkListVoltaDao;
    private CheckListVolta checkListVolta;
    private ProgressDialog progressDialog, progressDialog2;
    private AlertDialog dialog2, dialog1;
    private File file;
    private Handler handler;
    private int POSITION = -1, flag = 0;
    private boolean loopFlag = false;
    private RFIDWithUHF mReader;
    private List<String> listaEpcs;
    private SharedPreferences preferences;
    private TextView tvScan;
    private List<CheckListAmbienteResposta> listAmbienteRespostas;
    private int flagRFIDImage = 0;


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
        tvScan = (TextView) findViewById(R.id.scan);

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

//        try {
//            mReader = RFIDWithUHF.getInstance();
//        } catch (Exception ex) {
//
//            Toast.makeText(this, ex.getMessage(),
//                    Toast.LENGTH_SHORT).show();
//
//            return;
//        }
//
//        if (mReader != null) {
//            InitTask initTask = new InitTask();
//            initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }

        Application.eventHandler = new EventHandler();
        Inventorytimer.getInstance().setActivity(this);
        initializeConnectionSettings();

        if (Application.readers == null) {
            Application.readers = new Readers();
        }
        Application.readers.attach(this);
        if (!isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
        }

        loadPairedDevices();

        List<FotoAmbiente> fotoAmbientes = (List<FotoAmbiente>) getIntent().getSerializableExtra("fotoAmbiente");

        if (fotoAmbientes.size() != 0) {
            new AsyncFotos().execute(fotoAmbientes);
        }

    }

    private void initializeConnectionSettings() {
        SharedPreferences settings = getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        Application.AUTO_DETECT_READERS = settings.getBoolean(Constants.AUTO_DETECT_READERS, true);
        Application.AUTO_RECONNECT_READERS = settings.getBoolean(Constants.AUTO_RECONNECT_READERS, false);
        Application.NOTIFY_READER_AVAILABLE = settings.getBoolean(Constants.NOTIFY_READER_AVAILABLE, false);
        Application.NOTIFY_READER_CONNECTION = settings.getBoolean(Constants.NOTIFY_READER_CONNECTION, false);
        Application.NOTIFY_BATTERY_STATUS = settings.getBoolean(Constants.NOTIFY_BATTERY_STATUS, true);
        Application.EXPORT_DATA = settings.getBoolean(Constants.EXPORT_DATA, false);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//
//
//                String result = msg.obj + "";
//                if (!listaEpcs.contains(result)) {
//                    listaEpcs.add(result);
//                    TiraLista();
//                }
//            }
//        };
//
//
//    }

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

    @Override
    public void RFIDReaderAppeared(ReaderDevice readerDevice) {

    }

    @Override
    public void RFIDReaderDisappeared(ReaderDevice readerDevice) {

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
                    public void onClick(View v, int position) {
                        //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item

                        checkListVolta = new CheckListVolta();
                        checkListVolta.setIdCasa(ambiente.getIdCasa());
                        //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
                        //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
                        checkListVolta.setEstoque(0);
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
        Button btDone, btCancel, btNok;
        imageRfid = (ImageView) v.findViewById(R.id.imageRfid);
        imageCamera = (ImageView) v.findViewById(R.id.imageCamera);
        imageEstoque = (ImageView) v.findViewById(R.id.imageEstoque);
        btDone = (Button) v.findViewById(R.id.btDone);
        btCancel = (Button) v.findViewById(R.id.btCancel);
        btNok = (Button) v.findViewById(R.id.btNok);
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

        if (checkListVolta.getEstoque() > 1) {
            imageEstoque.setImageResource(R.drawable.ic_warehouse_green);
        }

        if (c.getEstoque() > 1 && c.getEvidencia().equals("N")) {
            dialog.dismiss();
            popupQuantidade(position, c.getEvidencia());
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
                popupQuantidade(position, c.getEvidencia());
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                dialog.dismiss();
            }
        });

        btNok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (c.getRfid().equals("S")) {
                    checkListVolta.setRfid("N");
                }
                AsynSetCheckList task = new AsynSetCheckList();
                task.execute(checkListVolta);
            }
        });


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (c.getEvidencia().equals("S") && checkListVolta.getCaminhoFoto() == null) {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.take_picture), Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    if (c.getRfid().equals("S")) {
                        checkListVolta.setEstoque(1);
                        if (c.getAchou() == 1) {
                            checkListVolta.setRfid("S");
                        } else {
                            if (flagRFIDImage == 0) {
                                //tirar foto caso não encontre rfid, mas o item está la
                                //rever, do jeito q está, mesmo sim como não ele envia a volta
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

                                checkListVolta.setRfid("N");
                            }
                        }
                    } else {
                        checkListVolta.setEstoque(1);
                        flagRFIDImage = 1;
                    }
                    if (flagRFIDImage != 0) {
                        AsynSetCheckList task = new AsynSetCheckList();
                        task.execute(checkListVolta);
                        flagRFIDImage = 0;
                    }
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

                    flagRFIDImage = 1;

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
                        public void onClick(View v, int position) {
                            //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
                            checkListVolta = new CheckListVolta();
                            checkListVolta.setIdCasa(ambiente.getIdCasa());
                            //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
                            //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
                            checkListVolta.setEstoque(0);
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

    public void popupQuantidade(final int position, final String evidencia) {
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

                    if (evidencia.equals("N")) {
                        AsynSetCheckList task = new AsynSetCheckList();
                        task.execute(checkListVolta);
                    } else {
                        popupAction(position);
                    }

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


//    class TagThread extends Thread {
//
//        private int mBetween = 80;
//        HashMap<String, String> map;
//
//        public TagThread(int iBetween) {
//            mBetween = iBetween;
//        }
//
//        public void run() {
//
//            String[] res;
//
//            while (loopFlag) {
//
//                res = mReader.readTagFormBuffer();
//
//                if (res != null) {
//
//                    Message msg = handler.obtainMessage();
//                    msg.obj = res[1];
//                    handler.sendMessage(msg);
//                }
//
////                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
////                    @Override
////                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
////
////
////                        if (keyCode == 139) {
////                            dialog.dismiss();
////                            stopInventory();
////
//////                            List<CheckList> listacoletados = existe(checklistDao.listar(ambiente.getId()), listaEpcs);
//////
//////                            for (CheckList c : listacoletados) {
//////                                checklistDao.achou(c);
//////
//////                                if (!c.getRfid().equals("") && c.getEvidencia().equals("N") && c.getEstoque() <= 1) {
//////                                    //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
//////                                    checkListVolta = new CheckListVolta();
//////                                    checkListVolta.setIdCasa(ambiente.getIdCasa());
//////                                    //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
//////                                    //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
//////                                    checkListVolta.setEstoque(-1);
//////                                    checkListVolta.setRfid("S");
//////                                    checkListVolta.setIdChecklist(getIntent().getIntExtra("ID_CHECKLIST", 0));
//////                                    checkListVolta.setIdUsuario(preferences.getInt("idUsuario", 0));
//////                                    checkListVolta.setIdAmbienteItem(c.getIdCasaItem());
//////
//////                                    checkListVoltaDao.incluir(checkListVolta);
//////                                    checkListVoltaDao.respondido(checkListVolta);
//////                                }
//////
//////                                adapter = new ListaChecklistItemAdapter(checklistDao.listar(ambiente.getId()), CheckListItemActivity.this);
//////                                recyclerView.setAdapter(adapter);
//////
//////                                adapter.ItemClickListener(new ListaChecklistItemAdapter.ItemClick() {
//////                                    @Override
//////                                    public void onClick(int position) {
//////                                        //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
//////                                        checkListVolta = new CheckListVolta();
//////                                        checkListVolta.setIdCasa(ambiente.getIdCasa());
//////                                        //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
//////                                        //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
//////                                        checkListVolta.setEstoque(-1);
//////                                        popupAction(position);
//////                                    }
//////                                });
//////
//////                                if (checklistDao.listar(ambiente.getId()).size() == 0) {
//////                                    popupQuestion();
//////                                }
//////                            }
////
////
////                            return true;
////                        }
////                        return false;
////                    }
////                });
//
//                try {
//                    sleep(mBetween);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }

    @Override
    protected void onDestroy() {

//        if (mReader != null) {
//            mReader.free();
//        }

        if (DisconnectTask != null)
            DisconnectTask.cancel(true);
        //disconnect from reader
        try {
            if (Application.mConnectedReader != null) {
                Application.mConnectedReader.Events.removeEventsListener(Application.eventHandler);
                Application.mConnectedReader.disconnect();
            }
        } catch (InvalidUsageException e) {
            e.printStackTrace();
        } catch (OperationFailureException e) {
            e.printStackTrace();
        }
        Application.mConnectedReader = null;
        //stop Timer
        Inventorytimer.getInstance().stopTimer();
        stopTimer();
        //update dpo icon in settings list
        //SettingsContent.ITEMS.get(8).icon = R.drawable.title_dpo_disabled;
        clearSettings();
        Application.mConnectedDevice = null;
//        Application.mConnectedReader = null;
        //ReadersListFragment.readersList.clear();
        Application.readers.deattach(this);
        Application.reset();

        super.onDestroy();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == 139) {
//
//            if (event.getRepeatCount() == 0) {
//                readTag();
//            }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

//    public class InitTask extends AsyncTask<String, Integer, Boolean> {
//        ProgressDialog mypDialog;
//        boolean test;
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            super.onPreExecute();
//
//            mypDialog = new ProgressDialog(CheckListItemActivity.this);
//            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mypDialog.setMessage("Init...");
//            mypDialog.setCanceledOnTouchOutside(false);
//            mypDialog.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            // TODO Auto-generated method stub
//            return mReader.init();
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//
//            mypDialog.cancel();
//
//            if (!result) {
//                Toast.makeText(CheckListItemActivity.this, "Init fail",
//                        Toast.LENGTH_SHORT).show();
//
//            } else {
//                Toast.makeText(CheckListItemActivity.this, "Init OK",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

//    public void readTag() {
//        dialog = new ProgressDialog(CheckListItemActivity.this);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setMessage(getString(R.string.load));
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setCancelable(false);
//        dialog.show();
//
//        if (tvScan.getVisibility() != View.VISIBLE) {
//            if (mReader.startInventoryTag((byte) 0, (byte) 0)) {
//                tvScan.setVisibility(View.VISIBLE);
//                loopFlag = true;
//                new TagThread(10).start();
//            } else {
//                mReader.stopInventory();
//                Toast.makeText(CheckListItemActivity.this, "Open Failure", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            stopInventory();
//        }
//    }

//    private void stopInventory() {
//
//        if (loopFlag) {
//            recyclerView.setEnabled(true);
//            tvScan.setVisibility(View.GONE);
//            loopFlag = false;
//
//            if (!mReader.stopInventory()) {
//                Toast.makeText(CheckListItemActivity.this, "RFID FAIL STOP", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }

    public List<CheckList> existe(List<CheckList> listaCheck, List<String> listaString) {

        List<CheckList> listVoltas = new ArrayList<>();
        for (CheckList c : listaCheck) {
            //existe sem o inicio 3000
            if (listaString.contains(c.getEpc())) {
                listVoltas.add(new CheckList(c.getId(), c.getRfid(), c.getIdAmbiente(), c.getIdCasaItem(), c.getEvidencia(), c.getEstoque()));
                //existe com o inicio 3000
            } else if (listaString.contains("3000" + c.getEpc())) {
                listVoltas.add(new CheckList(c.getId(), c.getRfid(), c.getIdAmbiente(), c.getIdCasaItem(), c.getEvidencia(), c.getEstoque()));
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

    private void TiraLista() {
        List<CheckList> listacoletados = existe(checklistDao.listar(ambiente.getId()), listaEpcs);

        for (CheckList c : listacoletados) {
            checklistDao.achou(c);

            if (checklistDao.itensEncontrados(c) == 0) {
                //dialog.dismiss();
                //stopInventory();
            }

            if (!c.getRfid().equals("") && c.getEvidencia().equals("N") && c.getEstoque() <= 1) {
                //inicio aqui pq toda vez q eu chamo o popuaction ele criaria um novo checklistvolta, e eu só qro criar quando eu clicar no item
                checkListVolta = new CheckListVolta();
                checkListVolta.setIdCasa(ambiente.getIdCasa());
                //inicio como -1 para conseguir fazer o teste se ja foi preenchido, e inicio ela aqui pq toda vez q eu volto de outro
                //popup ele estava setando -1 e eu não conseguia saber se foi preenchido ou não
                checkListVolta.setEstoque(1);
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
                public void onClick(View v, int position) {
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


    private boolean getRepeatTriggers() {
        if ((Application.settings_startTrigger != null && (Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC))
                || (isTriggerRepeat != null && isTriggerRepeat))
            return true;
        else
            return false;
    }

    public class EventHandler implements RfidEventsListener {

        @Override
        public void eventReadNotify(RfidReadEvents e) {
            final TagData[] myTags = Application.mConnectedReader.Actions.getReadTags(100);
            if (myTags != null) {
                //Log.d("RFID_EVENT","l: "+myTags.length);
                for (int index = 0; index < myTags.length; index++) {
                    if (myTags[index].getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                            myTags[index].getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
//                        if (myTags[index].getMemoryBankData().length() > 0) {
//                            System.out.println(" Mem Bank Data " + myTags[index].getMemoryBankData());
//                        }
                    }
                    if (myTags[index].isContainsLocationInfo()) {
                        final int tag = index;
                        Application.TagProximityPercent = myTags[tag].LocationInfo.getRelativeDistance();
/*                        if (fragment instanceof LocationingFragment)
                            ((LocationingFragment) fragment).handleLocateTagResponse();*/
                    }
                    if (Application.isAccessCriteriaRead && !Application.mIsInventoryRunning) {
                        accessTagCount++;
                    } else {
                        if (myTags[index] != null && (myTags[index].getOpStatus() == null || myTags[index].getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS)) {
                            final int tag = index;
                            new ResponseHandlerTask(myTags[tag]).execute();
                        }
                    }
                }
            }
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            System.out.println("Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
            notificationFromGenericReader(rfidStatusEvents);
        }
    }

    /**
     * Async Task, which will handle tag data response from reader. This task is used to check whether tag is in inventory list or not.
     * If tag is not in the list then it will add the tag data to inventory list. If tag is there in inventory list then it will update the tag details in inventory list.
     */
    public class ResponseHandlerTask extends AsyncTask<Void, Void, Boolean> {
        private TagData tagData;
        private InventoryListItem inventoryItem;
        private InventoryListItem oldObject;
        private String memoryBank;
        private String memoryBankData;

        ResponseHandlerTask(TagData tagData) {
            this.tagData = tagData;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean added = false;
            try {
                if (Application.inventoryList.containsKey(tagData.getTagID())) {
                    inventoryItem = new InventoryListItem(tagData.getTagID(), 1, null, null, null, null, null, null);
                    int index = Application.inventoryList.get(tagData.getTagID());
                    if (index >= 0) {
                        Application.TOTAL_TAGS++;
                        //Tag is already present. Update the fields and increment the count
                        if (tagData.getOpCode() != null)
                            if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
                                memoryBank = tagData.getMemoryBank().toString();
                                memoryBankData = tagData.getMemoryBankData().toString();
                            }
                        oldObject = Application.tagsReadInventory.get(index);
                        oldObject.incrementCount();
                        if (oldObject.getMemoryBankData() != null && !oldObject.getMemoryBankData().equalsIgnoreCase(memoryBankData))
                            oldObject.setMemoryBankData(memoryBankData);
                        if (pc)
                            oldObject.setPC(Integer.toString(tagData.getPC()));
                        if (phase)
                            oldObject.setPhase(Integer.toString(tagData.getPhase()));
                        if (channelIndex)
                            oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                        if (rssi)
                            oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                    }
                } else {
                    //Tag is encountered for the first time. Add it.
                    if (Application.inventoryMode == 0 || (Application.inventoryMode == 1 && Application.UNIQUE_TAGS <= Constants.UNIQUE_TAG_LIMIT)) {
                        int tagSeenCount = 0;
                        if (Integer.toString(tagData.getTagSeenCount()) != null)
                            tagSeenCount = tagData.getTagSeenCount();
                        if (tagSeenCount != 0) {
                            Application.TOTAL_TAGS += tagSeenCount;
                            inventoryItem = new InventoryListItem(tagData.getTagID(), tagSeenCount, null, null, null, null, null, null);
                        } else {
                            Application.TOTAL_TAGS++;
                            inventoryItem = new InventoryListItem(tagData.getTagID(), 1, null, null, null, null, null, null);
                        }
                        added = Application.tagsReadInventory.add(inventoryItem);
                        if (added) {
                            Application.inventoryList.put(tagData.getTagID(), Application.UNIQUE_TAGS);
                            if (tagData.getOpCode() != null)

                                if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
                                    memoryBank = tagData.getMemoryBank().toString();
                                    memoryBankData = tagData.getMemoryBankData().toString();

                                }
                            oldObject = Application.tagsReadInventory.get(Application.UNIQUE_TAGS);
                            oldObject.setMemoryBankData(memoryBankData);
                            oldObject.setMemoryBank(memoryBank);
                            if (pc)
                                oldObject.setPC(Integer.toString(tagData.getPC()));
                            if (phase)
                                oldObject.setPhase(Integer.toString(tagData.getPhase()));
                            if (channelIndex)
                                oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                            if (rssi)
                                oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                            Application.UNIQUE_TAGS++;
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                //logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                oldObject = null;
                added = false;
            } catch (Exception e) {
                // logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                oldObject = null;
                added = false;
            }
            inventoryItem = null;
            memoryBank = null;
            memoryBankData = null;
            return added;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            cancel(true);
            if (oldObject != null)
                CheckListItemActivity.this.handleTagResponse(oldObject, result);
            oldObject = null;
        }
    }

    protected class UpdateDisconnectedStatusTask extends AsyncTask<Void, Void, Boolean> {
        private final String device;
        // store current reader state
        private final ReaderDevice readerDevice;
        long disconnectedTime;

        public UpdateDisconnectedStatusTask(String device) {
            this.device = device;
            disconnectedTime = System.currentTimeMillis();
            // store current reader state
            readerDevice = Application.mConnectedDevice;
            //
            Application.mReaderDisappeared = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (readerDevice != null && readerDevice.getName().equalsIgnoreCase(device))
                        readerDisconnected(readerDevice);
                    else
                        readerDisconnected(new ReaderDevice(device, null));
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //Check if the connected device is one we had comm with
            if (!Application.is_disconnection_requested && Application.AUTO_RECONNECT_READERS && readerDevice != null && device != null && device.equalsIgnoreCase(readerDevice.getName())) {
                if (isBluetoothEnabled()) {
                    boolean bConnected = false;
                    int retryCount = 0;
                    while (!bConnected && retryCount < 10) {
                        if (isCancelled() || isDeviceDisconnected)
                            break;
                        try {
                            Thread.sleep(1000);
                            retryCount++;
                            // check manual connection is initiated
                            if (Application.is_connection_requested || isCancelled())
                                break;
                            readerDevice.getRFIDReader().reconnect();
                            bConnected = true;
                            // break temporary pairing connection if reader is unpaired
                            if (Application.mReaderDisappeared != null && Application.mReaderDisappeared.getName().equalsIgnoreCase(readerDevice.getName())) {
                                readerDevice.getRFIDReader().disconnect();
                                bConnected = false;
                                break;
                            }
                        } catch (InvalidUsageException e) {
                        } catch (OperationFailureException e) {
                            if (e.getResults() == RFIDResults.RFID_BATCHMODE_IN_PROGRESS) {
                                Application.isBatchModeInventoryRunning = true;
                                bConnected = true;
                            }
                            if (e.getResults() == RFIDResults.RFID_READER_REGION_NOT_CONFIGURED) {
                                try {
                                    readerDevice.getRFIDReader().disconnect();
                                    bConnected = false;
                                    break;
                                } catch (InvalidUsageException e1) {
                                    e1.printStackTrace();
                                } catch (OperationFailureException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return bConnected;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!isCancelled()) {
                if (result)
                    readerReconnected(readerDevice);
                else if (!Application.is_connection_requested) {
                    sendNotification(Constants.ACTION_READER_CONN_FAILED, "Connection Failed!! was received");
                    try {
                        readerDevice.getRFIDReader().disconnect();
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Method which will called once notification received from reader.
     * update the operation status in the application based on notification type
     *
     * @param rfidStatusEvents - notification received from reader
     */

    private void notificationFromGenericReader(RfidStatusEvents rfidStatusEvents) {

        if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.DISCONNECTION_EVENT) {
            if (Application.mConnectedReader != null)
                DisconnectTask = new UpdateDisconnectedStatusTask(Application.mConnectedReader.getHostName()).execute();
//            Application.mConnectedReader = null;
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.INVENTORY_START_EVENT) {
            if (!Application.isAccessCriteriaRead && !Application.isLocatingTag) {
                //if (!getRepeatTriggers() && Inventorytimer.getInstance().isTimerRunning()) {
                Application.mIsInventoryRunning = true;
                Inventorytimer.getInstance().startTimer();
                //}
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.INVENTORY_STOP_EVENT) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            accessTagCount = 0;
            Application.isAccessCriteriaRead = false;

            if (Application.mIsInventoryRunning) {
                Inventorytimer.getInstance().stopTimer();
            } else if (Application.isGettingTags) {
                Application.isGettingTags = false;
                Application.mConnectedReader.Actions.purgeTags();
                if (Application.EXPORT_DATA) {
                    if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new DataExportTask(getApplicationContext(), Application.tagsReadInventory, Application.mConnectedDevice.getName(), Application.TOTAL_TAGS, Application.UNIQUE_TAGS, Application.mRRStartedTime).execute();
                            }
                        });
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*if (fragment instanceof ReadersListFragment) {
                            //((ReadersListFragment) fragment).cancelProgressDialog();
                            if (Application.mConnectedReader != null && Application.mConnectedReader.ReaderCapabilities.getModelName() != null) {
                                ((ReadersListFragment) fragment).capabilitiesRecievedforDevice();
                            }
                        }*/
                    }
                });
            }

            if (!getRepeatTriggers()) {
                if (Application.mIsInventoryRunning)
                    isInventoryAborted = true;
                else if (Application.isLocatingTag)
                    isLocationingAborted = true;
                operationHasAborted();
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.OPERATION_END_SUMMARY_EVENT) {
            /*if (fragment instanceof RapidReadFragment)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((RapidReadFragment) fragment).updateInventoryDetails();
                    }
                });*/
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
            Boolean triggerPressed = false;
            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED)
                triggerPressed = true;
            //if (fragment instanceof ResponseHandlerInterfaces.TriggerEventHandler) {
            if (triggerPressed && (Application.settings_startTrigger.getTriggerType().toString().equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE.toString()) || (isTriggerRepeat != null && !isTriggerRepeat)))
                CheckListItemActivity.this.triggerPressEventRecieved();
            else if (!triggerPressed && (Application.settings_stopTrigger.getTriggerType().toString().equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE.toString()) || (isTriggerRepeat != null && !isTriggerRepeat)))
                CheckListItemActivity.this.triggerReleaseEventRecieved();
            //}
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            /*final Events.BatteryData batteryData = rfidStatusEvents.StatusEventData.BatteryData;
            Application.BatteryData = batteryData;
            setActionBarBatteryStatus(batteryData.getLevel());

            if (batteryNotificationHandlers != null && batteryNotificationHandlers.size() > 0) {
                for (BatteryNotificationHandler batteryNotificationHandler : batteryNotificationHandlers)
                    batteryNotificationHandler.deviceStatusReceived(batteryData.getLevel(), batteryData.getCharging(), batteryData.getCause());
            }
            if (Application.NOTIFY_BATTERY_STATUS && batteryData.getCause() != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (batteryData.getCause().trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_CRITICAL))
                            sendNotification(com.zebra.rfidreader.demo.common.Constants.ACTION_READER_BATTERY_CRITICAL, getString(R.string.battery_status__critical_message));
                        else if (batteryData.getCause().trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_LOW))
                            sendNotification(com.zebra.rfidreader.demo.common.Constants.ACTION_READER_BATTERY_CRITICAL, getString(R.string.battery_status_low_message));
                    }
                });
            }*/

        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.BATCH_MODE_EVENT) {
            Application.isBatchModeInventoryRunning = true;
            startTimer();
            Application.mIsInventoryRunning = true;
            Application.memoryBankId = 0;
            isTriggerRepeat = rfidStatusEvents.StatusEventData.BatchModeEventData.get_RepeatTrigger();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (fragment instanceof ResponseHandlerInterfaces.BatchModeEventHandler)
//                        ((ResponseHandlerInterfaces.BatchModeEventHandler) fragment).batchModeEventReceived();
                    /*if (fragment instanceof ReadersListFragment) {
                        //((ReadersListFragment) fragment).cancelProgressDialog();
                        if (Application.mConnectedReader != null && Application.mConnectedReader.ReaderCapabilities.getModelName() == null) {
                            ((ReadersListFragment) fragment).capabilitiesRecievedforDevice();
                        }
                    }*/
                }
            });
        }
    }

    private void operationHasAborted() {
        //retrieve get tags if inventory in batch mode got aborted
        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            if (isInventoryAborted) {
                Application.isBatchModeInventoryRunning = false;
                isInventoryAborted = true;
                Application.isGettingTags = true;
                if (Application.settings_startTrigger == null) {
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            try {
                                if (Application.mConnectedReader.isCapabilitiesReceived())
                                    UpdateReaderConnection(false);
                                else
                                    UpdateReaderConnection(true);
                                // update fields before getting tags
                                getTagReportingfields();
                                //
                                Application.mConnectedReader.Actions.getBatchedTags();
                            } catch (InvalidUsageException e) {
                                e.printStackTrace();
                            } catch (OperationFailureException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                } else
                    Application.mConnectedReader.Actions.getBatchedTags();
            }
        }

        if (Application.mIsInventoryRunning) {
            if (isInventoryAborted) {
                Application.mIsInventoryRunning = false;
                isInventoryAborted = false;
                isTriggerRepeat = null;
                if (Inventorytimer.getInstance().isTimerRunning())
                    Inventorytimer.getInstance().stopTimer();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //export Data to the file
                        if (Application.EXPORT_DATA)
                            if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty()) {
                                new DataExportTask(getApplicationContext(), Application.tagsReadInventory, Application.mConnectedReader.getHostName(), Application.TOTAL_TAGS, Application.UNIQUE_TAGS, Application.mRRStartedTime).execute();
                            }
                    }
                });
            }
        } else if (Application.isLocatingTag) {
            if (isLocationingAborted) {
                Application.isLocatingTag = false;
                isLocationingAborted = false;
            }
        }
    }

    public static void UpdateReaderConnection(Boolean fullUpdate) throws InvalidUsageException, OperationFailureException {
        Application.mConnectedReader.Events.setBatchModeEvent(true);
        Application.mConnectedReader.Events.setReaderDisconnectEvent(true);
        Application.mConnectedReader.Events.setInventoryStartEvent(true);
        Application.mConnectedReader.Events.setInventoryStopEvent(true);
        Application.mConnectedReader.Events.setTagReadEvent(true);
        Application.mConnectedReader.Events.setHandheldEvent(true);
        Application.mConnectedReader.Events.setBatteryEvent(true);
        Application.mConnectedReader.Events.setPowerEvent(true);
        Application.mConnectedReader.Events.setOperationEndSummaryEvent(true);

        if (fullUpdate)
            Application.mConnectedReader.PostConnectReaderUpdate();

        Application.regulatory = Application.mConnectedReader.Config.getRegulatoryConfig();
        Application.regionNotSet = false;
        Application.rfModeTable = Application.mConnectedReader.ReaderCapabilities.RFModes.getRFModeTableInfo(0);
        Application.antennaRfConfig = Application.mConnectedReader.Config.Antennas.getAntennaRfConfig(1);
        Application.singulationControl = Application.mConnectedReader.Config.Antennas.getSingulationControl(1);
        Application.settings_startTrigger = Application.mConnectedReader.Config.getStartTrigger();
        Application.settings_stopTrigger = Application.mConnectedReader.Config.getStopTrigger();
        Application.tagStorageSettings = Application.mConnectedReader.Config.getTagStorageSettings();
        Application.dynamicPowerSettings = Application.mConnectedReader.Config.getDPOState();
        Application.beeperVolume = Application.mConnectedReader.Config.getBeeperVolume();
        Application.batchMode = Application.mConnectedReader.Config.getBatchModeConfig().getValue();
        Application.reportUniquetags = Application.mConnectedReader.Config.getUniqueTagReport();
        Application.mConnectedReader.Config.getDeviceVersionInfo(Application.versionInfo);
        //Log.d("RFIDDEMO","SCANNERNAME: " + Application.mConnectedReader.ReaderCapabilities.getScannerName());
        startTimer();
    }

    public static void startTimer() {
        if (t == null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (Application.mConnectedReader != null)
                            Application.mConnectedReader.Config.getDeviceStatus(true, false, false);
                        else
                            stopTimer();
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                }
            };
            t = new Timer();
            t.scheduleAtFixedRate(task, 0, 60000);
        }
    }

    /**
     * method to stop timer
     */
    public static void stopTimer() {
        if (t != null) {
            t.cancel();
            t.purge();
        }
        t = null;
    }

    private void getTagReportingfields() {
        pc = false;
        phase = false;
        channelIndex = false;
        rssi = false;
        if (Application.tagStorageSettings != null) {
            TAG_FIELD[] tag_field = Application.tagStorageSettings.getTagFields();
            for (int idx = 0; idx < tag_field.length; idx++) {
                if (tag_field[idx] == TAG_FIELD.PEAK_RSSI)
                    rssi = true;
                if (tag_field[idx] == TAG_FIELD.PHASE_INFO)
                    phase = true;
                if (tag_field[idx] == TAG_FIELD.PC)
                    pc = true;
                if (tag_field[idx] == TAG_FIELD.CHANNEL_INDEX)
                    channelIndex = true;
                if (tag_field[idx] == TAG_FIELD.TAG_SEEN_COUNT)
                    tagSeenCount = true;
            }
        }
    }

    /**
     * Method to notify device disconnection
     *
     * @param readerDevice
     */
    private void readerDisconnected(ReaderDevice readerDevice) {
        stopTimer();
        //updateConnectedDeviceDetails(readerDevice, false);
        if (Application.NOTIFY_READER_CONNECTION)
            sendNotification(Constants.ACTION_READER_DISCONNECTED, "Disconnected from " + readerDevice.getName());
        clearSettings();
        //setActionBarBatteryStatus(0);
        bluetoothDeviceDisConnected(readerDevice);
        Application.mConnectedDevice = null;
        Application.mConnectedReader = null;
        Application.is_disconnection_requested = false;
    }

    /**
     * method to clear reader's settings on disconnection
     */
    public static void clearSettings() {
        Application.antennaPowerLevel = null;
        Application.antennaRfConfig = null;
        Application.singulationControl = null;
        Application.rfModeTable = null;
        Application.regulatory = null;
        Application.batchMode = -1;
        Application.tagStorageSettings = null;
        Application.reportUniquetags = null;
        Application.dynamicPowerSettings = null;
        Application.settings_startTrigger = null;
        Application.settings_stopTrigger = null;
        Application.beeperVolume = null;
        Application.preFilters = null;
        if (Application.versionInfo != null)
            Application.versionInfo.clear();
        Application.regionNotSet = false;
        Application.isBatchModeInventoryRunning = null;
        Application.BatteryData = null;
        Application.is_disconnection_requested = false;
        Application.mConnectedDevice = null;
//        Application.mConnectedReader = null;
    }

    /**
     * Method to send the notification
     *
     * @param action - intent action
     * @param data   - notification message
     */
    public void sendNotification(String action, String data) {
        /*if (Application.isActivityVisible()) {
            if (action.equalsIgnoreCase(Constants.ACTION_READER_BATTERY_CRITICAL) || action.equalsIgnoreCase(Constants.ACTION_READER_BATTERY_LOW)) {
                new Toast(MainActivity.this, R.layout.toast_layout, data).show();
            } else {
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent i = new Intent(MainActivity.this, NotificationsService.class);
            i.putExtra(Constants.INTENT_ACTION, action);
            i.putExtra(Constants.INTENT_DATA, data);
            startService(i);
        }*/
    }

    public void bluetoothDeviceConnected(ReaderDevice device) {
        if (bluetoothDeviceFoundHandlers != null && bluetoothDeviceFoundHandlers.size() > 0) {
            for (ResponseHandlerInterfaces.BluetoothDeviceFoundHandler bluetoothDeviceFoundHandler : bluetoothDeviceFoundHandlers)
                bluetoothDeviceFoundHandler.bluetoothDeviceConnected(device);
        }
        if (Application.NOTIFY_READER_CONNECTION)
            sendNotification(Constants.ACTION_READER_CONNECTED, "Connected to " + device.getName());
    }

    public void bluetoothDeviceDisConnected(ReaderDevice device) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (Application.mIsInventoryRunning) {
            inventoryAborted();
            //export Data to the file if inventory is running in batch mode
            if (Application.isBatchModeInventoryRunning != null && !Application.isBatchModeInventoryRunning)
                if (Application.EXPORT_DATA)
                    if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty())
                        new DataExportTask(getApplicationContext(), Application.tagsReadInventory, device.getName(), Application.TOTAL_TAGS, Application.UNIQUE_TAGS, Application.mRRStartedTime).execute();
            Application.isBatchModeInventoryRunning = false;
        }
        if (Application.isLocatingTag) {
            Application.isLocatingTag = false;
        }

        Application.isAccessCriteriaRead = false;
        accessTagCount = 0;

        if (bluetoothDeviceFoundHandlers != null && bluetoothDeviceFoundHandlers.size() > 0) {
            for (ResponseHandlerInterfaces.BluetoothDeviceFoundHandler bluetoothDeviceFoundHandler : bluetoothDeviceFoundHandlers)
                bluetoothDeviceFoundHandler.bluetoothDeviceDisConnected(device);
        }

        if (Application.mConnectedReader != null && !Application.AUTO_RECONNECT_READERS) {
            try {
                Application.mConnectedReader.disconnect();
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            }
            Application.mConnectedReader = null;
        }
    }

    public void inventoryAborted() {
        Inventorytimer.getInstance().stopTimer();
        Application.mIsInventoryRunning = false;
    }

    /**
     * method to know whether bluetooth is enabled or not
     *
     * @return - true if bluetooth enabled
     * - false if bluetooth disabled
     */
    public static boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    private void readerReconnected(ReaderDevice readerDevice) {
        // store app reader
        Application.mConnectedDevice = readerDevice;
        Application.mConnectedReader = readerDevice.getRFIDReader();
        //
        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            /*clearInventoryData();*/
            Application.mIsInventoryRunning = true;
            Application.memoryBankId = 0;
            startTimer();
//            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
//            if (fragment instanceof ResponseHandlerInterfaces.BatchModeEventHandler)
//                ((ResponseHandlerInterfaces.BatchModeEventHandler) fragment).batchModeEventReceived();
        } else
            try {
                UpdateReaderConnection(false);
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            }
        // connect call
        bluetoothDeviceConnected(readerDevice);
    }

    //@Override
    public void triggerPressEventRecieved() {
        if (!Application.mIsInventoryRunning)
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inventoryStartOrStop();
                }
            });
    }

    //@Override
    public void triggerReleaseEventRecieved() {
        if (Application.mIsInventoryRunning)
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inventoryStartOrStop();
                }
            });
    }

    public void inventoryStartOrStop() {
        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
//                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                if (!Application.mIsInventoryRunning) {
                    //Here we send the inventory command to start reading the tags

                    //set flag value
                    tvScan.setVisibility(View.VISIBLE);
                    isInventoryAborted = false;
                    Application.mIsInventoryRunning = true;
                    getTagReportingfields();

                    //Perform inventory

                    try {
                        Application.mConnectedReader.Actions.Inventory.perform(); //Leitura RFID
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (final OperationFailureException e) {
                        e.printStackTrace();
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
//                                    if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
//                                        ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(e.getResults());
//                                    sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getVendorMessage());
                                }
                            });
                        }
                    }
                    if (Application.batchMode != -1) {
                        if (Application.batchMode == BATCH_MODE.ENABLE.getValue())
                            Application.isBatchModeInventoryRunning = true;
                    }

                } else if (Application.mIsInventoryRunning) {
                    isInventoryAborted = true;
                    tvScan.setVisibility(View.GONE);
                    //Here we send the abort command to stop the inventory
                    try {
                        Application.mConnectedReader.Actions.Inventory.stop();
                        if (((Application.settings_startTrigger != null && (Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC)))
                                || (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning))
                            operationHasAborted();
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }

                }
            } else
                Toast.makeText(getApplicationContext(), "RFID desconectado", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "Bluetooth desconectado", Toast.LENGTH_SHORT).show();
    }

    private String getReaderPassword(String address) {
        SharedPreferences sharedPreferences = CheckListItemActivity.this.getSharedPreferences(Constants.READER_PASSWORDS, 0);
        return sharedPreferences.getString(address, null);
    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
/*        if (listView.getAdapter() == null) {
            listView.setAdapter(adapter);
            batchModeInventoryList.setVisibility(View.GONE);
        }

        totalNoOfTags.setText(String.valueOf(Application.TOTAL_TAGS));
        if (uniqueTags != null)
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
        if (isAddedToList) {
            adapter.add(inventoryListItem);
        }
        adapter.notifyDataSetChanged();*/
        if (isAddedToList) {
            if (!listaEpcs.contains(inventoryListItem.getTagID())) {
                listaEpcs.add(inventoryListItem.getTagID());
                TiraLista();
            }
        }
    }

    private void loadPairedDevices() {

        if (isBluetoothEnabled()) {

            //readersList = new ArrayList<>();
            //Application.readers = new Readers();

            readersList.addAll(Application.readers.GetAvailableRFIDReaderList());

            ReaderDevice readerDevice = readersList.get(0);

            if (Application.mConnectedReader == null) {
                if (deviceConnectTask == null || deviceConnectTask.isCancelled()) {
                    Application.is_connection_requested = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                        deviceConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                        deviceConnectTask.execute();
                    }
                }
            } else {
                {
                    if (Application.mConnectedReader.isConnected()) {

                        try {
                            Application.mConnectedReader.Config.setBeeperVolume(BEEPER_VOLUME.HIGH_BEEP);
                        } catch (InvalidUsageException e) {
                            e.printStackTrace();
                        } catch (OperationFailureException e) {
                            e.printStackTrace();
                        }

                        Application.is_disconnection_requested = true;
                        try {
                            Application.mConnectedReader.disconnect();
                        } catch (InvalidUsageException e) {
                            e.printStackTrace();
                        } catch (OperationFailureException e) {
                            e.printStackTrace();
                        }
                        //
                        bluetoothDeviceDisConnected(Application.mConnectedDevice);
                        if (Application.NOTIFY_READER_CONNECTION)
                            sendNotification(Constants.ACTION_READER_DISCONNECTED, "Disconnected from " + Application.mConnectedReader.getHostName());
                        //
                        clearSettings();
                    }
                    if (!Application.mConnectedReader.getHostName().equalsIgnoreCase(readerDevice.getName())) {
                        Application.mConnectedReader = null;
                        if (deviceConnectTask == null || deviceConnectTask.isCancelled()) {
                            Application.is_connection_requested = true;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                                deviceConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                                deviceConnectTask.execute();
                            }
                        }
                    } else {
                        Application.mConnectedReader = null;
                    }
                }
            }


        } else
            Toast.makeText(this, "Please enable the bluetooth", Toast.LENGTH_SHORT).show();


    }

    public void bluetoothDeviceConnFailed(ReaderDevice device) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (deviceConnectTask != null)
            deviceConnectTask.cancel(true);
        Constants.logAsMessage(Constants.TYPE_ERROR, "ReadersListFragment", "deviceName is null or empty");

        sendNotification(Constants.ACTION_READER_CONN_FAILED, "Connection Failed!! was received");

        Application.mConnectedReader = null;
        Application.mConnectedDevice = null;
    }


    private class DeviceConnectTask extends AsyncTask<Void, String, Boolean> {
        private final ReaderDevice connectingDevice;
        private String prgressMsg;
        private OperationFailureException ex;
        private String password;

        DeviceConnectTask(ReaderDevice connectingDevice, String prgressMsg, String Password) {
            this.connectingDevice = connectingDevice;
            this.prgressMsg = prgressMsg;
            password = Password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogZebra = new CustomProgressDialog(CheckListItemActivity.this, prgressMsg);
            progressDialogZebra.setCanceledOnTouchOutside(true);
            progressDialogZebra.show();
        }

        @Override
        protected Boolean doInBackground(Void... a) {
            try {
                if (password != null)
                    connectingDevice.getRFIDReader().setPassword(password);
                connectingDevice.getRFIDReader().connect();
                if (password != null) {
                    SharedPreferences.Editor editor = CheckListItemActivity.this.getSharedPreferences(Constants.READER_PASSWORDS, 0).edit();
                    editor.putString(connectingDevice.getName(), password);
                    editor.commit();
                }
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
                ex = e;
            }
            if (connectingDevice.getRFIDReader().isConnected()) {
                Application.mConnectedReader = connectingDevice.getRFIDReader();
                try {
                    Application.mConnectedReader.Events.addEventsListener(Application.eventHandler);
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
                connectingDevice.getRFIDReader().Events.setBatchModeEvent(true);
                connectingDevice.getRFIDReader().Events.setReaderDisconnectEvent(true);
                connectingDevice.getRFIDReader().Events.setBatteryEvent(true);
                connectingDevice.getRFIDReader().Events.setInventoryStopEvent(true);
                connectingDevice.getRFIDReader().Events.setInventoryStartEvent(true);
                // if no exception in connect
                if (ex == null) {
                    try {
                        UpdateReaderConnection(false);
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                } else {
                    clearSettings();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (progressDialogZebra!=null && progressDialogZebra.isShowing()) {
                progressDialogZebra.dismiss();
                if (ex != null) {
                    if (ex.getResults() == RFIDResults.RFID_CONNECTION_PASSWORD_ERROR) {
                        bluetoothDeviceConnected(connectingDevice);
                    } else if (ex.getResults() == RFIDResults.RFID_BATCHMODE_IN_PROGRESS) {
                        Application.isBatchModeInventoryRunning = true;
                        Application.mIsInventoryRunning = true;
                        bluetoothDeviceConnected(connectingDevice);
                        if (Application.NOTIFY_READER_CONNECTION)
                            sendNotification(Constants.ACTION_READER_CONNECTED, "Connected to " + connectingDevice.getName());
                        //Events.StatusEventData data = Application.mConnectedReader.Events.GetStatusEventData(RFID_EVENT_TYPE.BATCH_MODE_EVENT);
//                    Intent detailsIntent = new Intent(getActivity(), MainActivity.class);
//                    detailsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    detailsIntent.putExtra(RFID_EVENT_TYPE.BATCH_MODE_EVENT.toString(), 0/*data.BatchModeEventData.get_RepeatTrigger()*/);
//                    startActivity(detailsIntent);
                    } else if (ex.getResults() == RFIDResults.RFID_READER_REGION_NOT_CONFIGURED) {
//                    bluetoothDeviceConnected(connectingDevice);
//                    Application.regionNotSet = true;
//                    sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, "Please set the region");
//                    Intent detailsIntent = new Intent(CheckListItemActivity.this, SettingsDetailActivity.class);
//                    detailsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    detailsIntent.putExtra(Constants.SETTING_ITEM_ID, 7);
//                    startActivity(detailsIntent);
                    } else
                        bluetoothDeviceConnFailed(connectingDevice);
                } else {
                    if (result) {
                        if (Application.NOTIFY_READER_CONNECTION)
                            sendNotification(Constants.ACTION_READER_CONNECTED, "Connected to " + connectingDevice.getName());
                        bluetoothDeviceConnected(connectingDevice);
                    } else {
                        bluetoothDeviceConnFailed(connectingDevice);
                    }
                }
                deviceConnectTask = null;
            }
        }

        @Override
        protected void onCancelled() {
            deviceConnectTask = null;
            super.onCancelled();
        }

        public ReaderDevice getConnectingDevice() {
            return connectingDevice;
        }
    }
}


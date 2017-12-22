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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pdasolucoes.com.br.homevacation.Adapter.ListaItemAdapter;
import pdasolucoes.com.br.homevacation.Dao.EpcDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.Categoria;
import pdasolucoes.com.br.homevacation.Model.EPC;
import pdasolucoes.com.br.homevacation.Model.FotoItem;
import pdasolucoes.com.br.homevacation.Model.InventoryListItem;
import pdasolucoes.com.br.homevacation.Model.Item;
import pdasolucoes.com.br.homevacation.Service.FotoItemService;
import pdasolucoes.com.br.homevacation.Service.ItemService;
import pdasolucoes.com.br.homevacation.Util.DialogKeyListener;
import pdasolucoes.com.br.homevacation.Util.ImageResizeUtils;
import pdasolucoes.com.br.homevacation.Util.ItemEPC;
import pdasolucoes.com.br.homevacation.Util.SDCardUtils;
import pdasolucoes.com.br.homevacation.application.Application;
import pdasolucoes.com.br.homevacation.application.Constants;
import pdasolucoes.com.br.homevacation.application.CustomProgressDialog;
import pdasolucoes.com.br.homevacation.application.DataExportTask;
import pdasolucoes.com.br.homevacation.application.Inventorytimer;
import pdasolucoes.com.br.homevacation.application.ResponseHandlerInterfaces;

/**
 * Created by PDA on 05/10/2017.
 */

public class CadastroItemActivity extends AppCompatActivity implements ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.ResponseTagHandler, Readers.RFIDReaderEventHandler {

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

    private RecyclerView recyclerView;
    private ListaItemAdapter adapter;
    private TextView tvTituloItem;
    private List<Item> lista;
    private Ambiente ambiente;
    private Item item;
    private FloatingActionButton fab;
    private List<Item> listaItemGeneric;
    private ProgressDialog progressDialog;
    private Categoria categoria;
    private List<Categoria> listaCategoria;
    private ArrayAdapter<Item> arrayAdapter;
    private Spinner spinner;
    private EpcDao epcDao;
    public static RFIDWithUHF mReader;
    private SharedPreferences preferences;
    private LinearLayout pictures, newPictures;
    private File file;
    private int flag = 0;
    private List<FotoItem> listaFotoItem;
    private FotoItem fotoItem;
    private AlertDialog dialog, dialog2;
    private int idAmbienteItem;
    private String epc;
    private TextInputEditText editEpc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        preferences = getSharedPreferences("Login", MODE_PRIVATE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvTituloItem = (TextView) findViewById(R.id.tvtTituloToolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);


        epcDao = new EpcDao(this);
        ambiente = (Ambiente) getIntent().getSerializableExtra("ambiente");

        tvTituloItem.setText(ambiente.getDescricao());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaFotoItem = new ArrayList<>();
                AsyncCategoriaGeneric taskGeneric = new AsyncCategoriaGeneric();
                taskGeneric.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

        if (savedInstanceState != null) {
            file = (File) savedInstanceState.getSerializable("file");
        }

        AsyncItem task = new AsyncItem();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ambiente.getId());

//        try {
//            mReader = RFIDWithUHF.getInstance();
//        } catch (Exception ex) {
//            Toast.makeText(CadastroItemActivity.this, ex.getMessage(),
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (mReader != null) {
//            new InitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }

        Application.eventHandler2 = new EventHandler();
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("file", file);
    }

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
                Application.mConnectedReader.Events.removeEventsListener(Application.eventHandler2);
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

    @Override
    protected void onResume() {
        super.onResume();

        if ((dialog != null && dialog.isShowing()) || (dialog2 != null && dialog2.isShowing())) {

            if (editEpc != null && epc != null) {
                if (editEpc.isShown()) {
                    if (!epc.equals("")) {
                        if (epc.substring(0, 4).equals("3000")) {
                            epc = epc.substring(4);
                        }

                        if (epc.length() == 24 && epc.substring(0, 2).equals("15")) {
                            if (!epcDao.existeEpc(epc)) {
                                editEpc.setText(epc);
                                item.setEpc(epc);
                            } else {
                                Toast.makeText(this, getString(R.string.epc_register) + " : " + epc, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, getString(R.string.incorret_barcode) + epc, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            if (fotoItem != null) {
                if (listaFotoItem.size() >= 0) {
                    newPictures.setVisibility(View.VISIBLE);
                    ImageView imageView = new ImageView(this);

                    if (!fotoItem.getCaminhoFoto().equals("") && flag == 0) {

                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(128, 256);
                        llp.setMargins(0, 0, 10, 0);
                        imageView.setLayoutParams(llp);

                        Uri uri = Uri.parse(fotoItem.getCaminhoFoto());
                        int w = imageView.getWidth();
                        int h = imageView.getHeight();
                        Bitmap bitmap = ImageResizeUtils.getResizedImage(uri, w, h, false);
                        imageView.setImageBitmap(bitmap);
                        pictures.addView(imageView);
                        flag = 1;
                    }
                }
            }
        }
    }

    @Override
    public void RFIDReaderAppeared(ReaderDevice readerDevice) {

    }

    @Override
    public void RFIDReaderDisappeared(ReaderDevice readerDevice) {

    }

    private class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

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
                Toast.makeText(CadastroItemActivity.this, "Init RFID failed",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(CadastroItemActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage(getString(R.string.load));
            mypDialog.setCanceledOnTouchOutside(true);
            mypDialog.show();
        }
    }


    public class AsyncItem extends AsyncTask<Integer, Void, List<Item>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CadastroItemActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.load));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<Item> doInBackground(Integer... params) {

            lista = ItemService.getItem(params[0]);

            return lista;
        }

        @Override
        protected void onPostExecute(final List<Item> items) {
            super.onPostExecute(items);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            adapter = new ListaItemAdapter(items, CadastroItemActivity.this);
            recyclerView.setAdapter(adapter);

            adapter.ItemClickListener(new ListaItemAdapter.ItemClick() {
                @Override
                public void onClick(int position) {
                    //popupInformacoes do item
                    listaFotoItem = new ArrayList<>();
                    idAmbienteItem = items.get(position).getIdAmbienteItem();
                    item = items.get(position);
                    popupAlteraItem();
                }
            });
        }
    }

    public class AsyncCategoriaGeneric extends AsyncTask<Void, Void, Object> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CadastroItemActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

        }

        @Override
        protected Object doInBackground(Void... params) {


            listaCategoria = ItemService.getItemCategoria();
            return null;
        }

        @Override
        protected void onPostExecute(Object items) {
            super.onPostExecute(items);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                item = new Item();
                popupInsereItem();
            }

        }

        public class AsyncItemGeneric extends AsyncTask<Object, Void, Object> {

            @Override
            protected Object doInBackground(Object... params) {

                listaItemGeneric = ItemService.getItemGenerico(params[0].toString());
                Item i = new Item();
                i.setIdItem(-1);
                i.setDescricao(getResources().getString(R.string.other));
                i.setIdUsuario(preferences.getInt("idUsuario", 0));
                listaItemGeneric.add(i);
                return listaItemGeneric;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                arrayAdapter =
                        new ArrayAdapter<>(CadastroItemActivity.this, android.R.layout.simple_list_item_1, listaItemGeneric);

                spinner.setAdapter(arrayAdapter);
            }
        }

        public void popupInsereItem() {
            View v = View.inflate(CadastroItemActivity.this, R.layout.popup_insere_novo_item, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroItemActivity.this);
            //builder.setOnKeyListener(dkl);
            final TextInputEditText editItem = (TextInputEditText) v.findViewById(R.id.editRoom);
            editEpc = (TextInputEditText) v.findViewById(R.id.editEPC);
            final TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
//            final TextInputEditText editCategoria = (TextInputEditText) v.findViewById(R.id.editCategoria);
            Button btDone = (Button) v.findViewById(R.id.btDone);
            Button btCancel = (Button) v.findViewById(R.id.btCancel);
            RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
            RadioGroup radioGroupEvidence = (RadioGroup) v.findViewById(R.id.radioGroupEvidence);
            spinner = (Spinner) v.findViewById(R.id.spinnerItem);
            Spinner spinnerCategoria = (Spinner) v.findViewById(R.id.spinnerCategoria);
            final TextInputLayout textInputStock = (TextInputLayout) v.findViewById(R.id.textInputLStock);
            final TextInputLayout textInputItem = (TextInputLayout) v.findViewById(R.id.textInputItem);
            final TextInputLayout textInputEpc = (TextInputLayout) v.findViewById(R.id.textInputEpc);
//            final TextInputLayout textInputCategoria = (TextInputLayout) v.findViewById(R.id.textInputCategoria);
            newPictures = (LinearLayout) v.findViewById(R.id.newPictures);
            pictures = (LinearLayout) v.findViewById(R.id.picture);
            ImageView addImage = (ImageView) v.findViewById(R.id.addImage);
            final ImageView addRFID = (ImageView) v.findViewById(R.id.addRfid);

            addImage.setOnClickListener(new View.OnClickListener() {
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
                }
            });

            addRFID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentIntegrator integrator = new IntentIntegrator(CadastroItemActivity.this);
                    integrator.setOrientationLocked(false);
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                }
            });

            builder.setView(v);
            dialog = builder.create();
            dialog.show();

            categoria = new Categoria();

            item.setIdUsuario(preferences.getInt("idUsuario", 0));
            ArrayAdapter<Categoria> arrayCategoria =
                    new ArrayAdapter<>(CadastroItemActivity.this, android.R.layout.simple_list_item_1, listaCategoria);
            spinnerCategoria.setAdapter(arrayCategoria);

            spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Categoria c = (Categoria) parent.getItemAtPosition(position);

                    spinner.setEnabled(true);
                    textInputItem.setVisibility(View.GONE);
//                        textInputCategoria.setVisibility(View.GONE);

                    categoria.setIdCategoria(c.getIdCategoria());
                    categoria.setDescricao(c.getDescricao());

                    AsyncItemGeneric asyncItemGeneric = new AsyncItemGeneric();
                    asyncItemGeneric.executeOnExecutor(THREAD_POOL_EXECUTOR, c.getDescricao());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Item i = (Item) parent.getItemAtPosition(position);

                    if (i.getIdItem() == -1) {
                        textInputItem.setVisibility(View.VISIBLE);
                    } else {
                        textInputItem.setVisibility(View.GONE);
                    }

                    item.setIdItem(i.getIdItem());
                    item.setDescricao(i.getDescricao());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    RadioButton r = (RadioButton) group.findViewById(checkedId);
                    String rfid = "";

                    if (r.getText().toString().equals("No")) {
                        textInputStock.setVisibility(View.VISIBLE);
                        textInputEpc.setVisibility(View.GONE);
                        addRFID.setVisibility(View.GONE);
                        rfid = "N";
                    } else {
                        textInputStock.setVisibility(View.GONE);
                        textInputEpc.setVisibility(View.VISIBLE);
                        addRFID.setVisibility(View.VISIBLE);
                        rfid = "S";

                    }

                    item.setRfid(rfid);
                }
            });

            radioGroupEvidence.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    RadioButton r = (RadioButton) group.findViewById(checkedId);
                    String evidence = "";

                    if (r.getText().toString().equals("No")) {
                        evidence = "N";
                    } else {
                        evidence = "S";

                    }
                    item.setEvidencia(evidence);
                }
            });

            editEpc.setEnabled(false);
//            editCategoria.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (editCategoria.isShown()) {
//                        if (!editCategoria.getText().toString().equals("")) {
//                            categoria.setDescricao(editCategoria.getText().toString());
//                        }
//                    }
//                }
//            });

            editItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (editItem.isShown()) {
                        if (!editItem.getText().toString().equals("")) {
                            item.setDescricao(editItem.getText().toString());
                        }
                    }
                }
            });

            editQtde.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (editQtde.isShown()) {
                        if (!editQtde.getText().toString().equals("")) {
                            item.setEstoque(Integer.parseInt(editQtde.getText().toString()));
                        }
                    }
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

                    //parametros q devem ser passados se o item e cataegoria ja cadastrados ou não
                    item.setIdAmbiente(ambiente.getId());
                    item.setIdUsuario(preferences.getInt("idUsuario", 0));
                    item.setIdCasa(ambiente.getIdCasa());
                    item.setAmbiente(ambiente.getDescricao());

                    List<EPC> epcs = new ArrayList<>();
                    epcs.add(new EPC(1, editEpc.getText().toString()));
                    epcDao.incluir(epcs);


                    if (item.getRfid().equals("S")) {
                        //if (!editEpc.getText().toString().equals("")) {
                        if (editItem.isShown()) {
                            item.setIdCategoria(categoria.getIdCategoria());
                            AsyncSetItem task = new AsyncSetItem();
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);

                        } else {
                            //chamar o async para cadastrar o item
                            //caso insira um item ja cadastrado
                            item.setIdCategoria(categoria.getIdCategoria());
                            AsyncCadastroItem task = new AsyncCadastroItem();
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
                        }

                        dialog.dismiss();
//                        } else {
//                            Toast.makeText(CadastroItemActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
//                        }
                    } else {

                        if (!editQtde.getText().toString().equals("")) {
                            if (editItem.isShown()) {
                                item.setIdCategoria(categoria.getIdCategoria());
                                AsyncSetItem task = new AsyncSetItem();
                                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);

                            } else {
                                //chamar o async para cadastrar o item
                                //caso insira um item ja cadastrado
                                item.setIdCategoria(categoria.getIdCategoria());
                                AsyncCadastroItem task = new AsyncCadastroItem();
                                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
                            }

                            dialog.dismiss();
                        } else {
                            Toast.makeText(CadastroItemActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
                        }
                    }

                    //inerindo fotos do item caso exista
                    if (listaFotoItem.size() != 0) {
                        AsynFotoAmbienteItem task = new AsynFotoAmbienteItem();
                        task.execute(listaFotoItem, idAmbienteItem);
                    }


                }
            });
        }

        public class AsyncCadastroItem extends AsyncTask<Object, Void, Integer> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CadastroItemActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getString(R.string.load));
                progressDialog.show();

            }

            @Override
            protected Integer doInBackground(Object... params) {
                return ItemService.setListaAmbienteItem((Item) params[0]);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (integer == 0) {
                    Toast.makeText(CadastroItemActivity.this, getString(R.string.error_insert), Toast.LENGTH_SHORT).show();
                }

                idAmbienteItem = integer;
                AsyncItem task = new AsyncItem();
                task.executeOnExecutor(THREAD_POOL_EXECUTOR, ambiente.getId());
            }
        }

        private class AsyncSetItem extends AsyncTask<Object, Void, Object> {


            @Override
            protected Object doInBackground(Object[] params) {
                item.setIdItem(ItemService.setItem((Item) params[0]));

                return item;
            }

            @Override
            protected void onPostExecute(Object objects) {
                super.onPostExecute(objects);

                AsyncCadastroItem task = new AsyncCadastroItem();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Item) objects);

            }
        }

//        private class AsyncSetCategoria extends AsyncTask<Object, Void, Integer> {
//
//
//            @Override
//            protected Integer doInBackground(Object[] params) {
//                categoria.setIdCategoria(ItemService.setCategoria(((Categoria) params[0])));
//
//                return categoria.getIdCategoria();
//            }
//
//            @Override
//            protected void onPostExecute(Integer id) {
//                super.onPostExecute(id);
//
//                //passando o novo idCategoria para o item
//                item.setIdCategoria(id);
//                AsyncSetItem task = new AsyncSetItem();
//                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
//            }
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                if (file != null && file.exists()) {
                    Log.d("foto", file.getAbsolutePath());

                    Uri imageUri = Uri.fromFile(file);
                    fotoItem = new FotoItem();
                    fotoItem.setIdAmbienteItem(idAmbienteItem);
                    fotoItem.setCaminhoFoto(file.getPath());
                    fotoItem.setIdUsuario(preferences.getInt("idUsuario", 0));

                    listaFotoItem.add(fotoItem);

                    Intent i = new Intent(CadastroItemActivity.this, PopupImage.class);
                    i.putExtra("imageUri", imageUri);
                    startActivity(i);
                    flag = 0;
                }
            } else if (scanResult != null) {
                //pegando o codigo de barras lido
                epc = scanResult.getContents();
            }
        }
    }

    private void popupAlteraItem() {
        View v = View.inflate(CadastroItemActivity.this, R.layout.popup_altera_novo_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroItemActivity.this);
        //codigo para ler epc por UHC
        //DialogKeyListener dkl = new DialogKeyListener();

        editEpc = (TextInputEditText) v.findViewById(R.id.editEPC);
        TextView tvNomeItem = (TextView) v.findViewById(R.id.nomeItem);
        TextView tvRfid = (TextView) v.findViewById(R.id.tvRfid);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);

        newPictures = (LinearLayout) v.findViewById(R.id.newPictures);
        pictures = (LinearLayout) v.findViewById(R.id.picture);
        ImageView addImage = (ImageView) v.findViewById(R.id.addImage);
        ImageView addRFID = (ImageView) v.findViewById(R.id.addRfid);

        if (!item.getRfid().equals("S")) {
            editEpc.setVisibility(View.VISIBLE);
            tvRfid.setVisibility(View.VISIBLE);
        }

        tvNomeItem.setText(item.getDescricao());
        editEpc.setEnabled(false);

        addImage.setOnClickListener(new View.OnClickListener() {
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
            }
        });


        addRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(CadastroItemActivity.this);
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        builder.setView(v);
        dialog2 = builder.create();
        dialog2.show();


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!editEpc.getText().toString().equals("")) {
                //sync item update
                //inerindo fotos do item caso exista
                if (listaFotoItem.size() != 0) {
                    AsynFotoAmbienteItem task = new AsynFotoAmbienteItem();
                    task.execute(listaFotoItem, idAmbienteItem);
                }

                List<EPC> epcs = new ArrayList<>();
                epcs.add(new EPC(1, editEpc.getText().toString()));
                epcDao.incluir(epcs);

                AsyncUpdateEpc task = new AsyncUpdateEpc();
                task.execute(editEpc.getText().toString(), item.getIdAmbienteItem());
                dialog2.dismiss();
//                } else {
//                    Toast.makeText(CadastroItemActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }

    private class AsyncUpdateEpc extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            int result = ItemService.SetItemEpc(params[0].toString(), Integer.parseInt(params[1].toString()));

            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (Integer.parseInt(o.toString()) != 0) {
                AsyncItem task = new AsyncItem();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ambiente.getId());
            }
        }
    }

    private class AsynFotoAmbienteItem extends AsyncTask {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CadastroItemActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            return FotoItemService.setListaItemFoto((List<FotoItem>) params[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();

                if (!(boolean) o) {
                    Toast.makeText(CadastroItemActivity.this, getString(R.string.error_insert), Toast.LENGTH_SHORT).show();
                }
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
                CadastroItemActivity.this.handleTagResponse(oldObject, result);
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
                CadastroItemActivity.this.triggerPressEventRecieved();
            else if (!triggerPressed && (Application.settings_stopTrigger.getTriggerType().toString().equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE.toString()) || (isTriggerRepeat != null && !isTriggerRepeat)))
                CadastroItemActivity.this.triggerReleaseEventRecieved();
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
        SharedPreferences sharedPreferences = CadastroItemActivity.this.getSharedPreferences(Constants.READER_PASSWORDS, 0);
        return sharedPreferences.getString(address, null);
    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {

        epc = inventoryListItem.getTagID();

        if (editEpc != null) {
            if (editEpc.isShown()) {
                if (!epc.equals("")) {
                    if (epc.substring(0, 4).equals("3000")) {
                        epc = epc.substring(4);
                    }

                    if (epc.length() == 24 && epc.substring(0, 2).equals("15")) {
                        if (!epcDao.existeEpc(epc)) {
                            editEpc.setText(epc);
                            item.setEpc(epc);
                        }
                    } else {
                        Toast.makeText(CadastroItemActivity.this, getString(R.string.incorret_barcode) + epc, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void loadPairedDevices() {

        if (isBluetoothEnabled()) {

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
            progressDialogZebra = new CustomProgressDialog(CadastroItemActivity.this, prgressMsg);
            progressDialogZebra.show();
        }

        @Override
        protected Boolean doInBackground(Void... a) {
            try {
                if (password != null)
                    connectingDevice.getRFIDReader().setPassword(password);
                connectingDevice.getRFIDReader().connect();
                if (password != null) {
                    SharedPreferences.Editor editor = CadastroItemActivity.this.getSharedPreferences(Constants.READER_PASSWORDS, 0).edit();
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
                    Application.mConnectedReader.Events.addEventsListener(Application.eventHandler2);
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
                        CheckListItemActivity.UpdateReaderConnection(false);
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                } else {
                    CheckListItemActivity.clearSettings();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialogZebra.cancel();
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

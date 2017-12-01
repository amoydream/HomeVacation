package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaItemAdapter;
import pdasolucoes.com.br.homevacation.Dao.EpcDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.Categoria;
import pdasolucoes.com.br.homevacation.Model.EPC;
import pdasolucoes.com.br.homevacation.Model.FotoItem;
import pdasolucoes.com.br.homevacation.Model.Item;
import pdasolucoes.com.br.homevacation.Service.FotoItemService;
import pdasolucoes.com.br.homevacation.Service.ItemService;
import pdasolucoes.com.br.homevacation.Util.DialogKeyListener;
import pdasolucoes.com.br.homevacation.Util.ImageResizeUtils;
import pdasolucoes.com.br.homevacation.Util.SDCardUtils;

/**
 * Created by PDA on 05/10/2017.
 */

public class CadastroItemActivity extends AppCompatActivity {

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
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ((dialog != null && dialog.isShowing()) || (dialog2 != null && dialog2.isShowing())) {

            if (editEpc != null && epc!=null) {
                if (editEpc.isShown()) {
                    if (!epc.equals("")) {
                        if (!epcDao.existeEpc(epc)) {
                            editEpc.setText(epc);
                            item.setEpc(epc);
                        } else {
                            Toast.makeText(this, getString(R.string.epc_register) + " : " + epc, Toast.LENGTH_LONG).show();
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

//    private class InitTask extends AsyncTask<String, Integer, Boolean> {
//        ProgressDialog mypDialog;
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
//                Toast.makeText(CadastroItemActivity.this, "Init RFID failed",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            super.onPreExecute();
//
//            mypDialog = new ProgressDialog(CadastroItemActivity.this);
//            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mypDialog.setMessage(getString(R.string.load));
//            mypDialog.setCanceledOnTouchOutside(true);
//            mypDialog.show();
//        }
//    }


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
//            DialogKeyListener dkl = new DialogKeyListener();
//            builder.setOnKeyListener(dkl);
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
//            dkl.ItemEpcListener(new DialogKeyListener.ItemEPC() {
//                @Override
//                public void onClickEpc(String epc) {
//                    if (editEpc.isShown()) {
//                        if (!epc.equals("")) {
//                            if (!epcDao.existeEpc(epc)) {
//                                editEpc.setText(epc);
//                                item.setEpc(epc);
//                            }
//                        }
//                    }
//
//                }
//            });

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

                    List<EPC> epcs = new ArrayList<>();
                    epcs.add(new EPC(1, editEpc.getText().toString()));
                    epcDao.incluir(epcs);


                    if (item.getRfid().equals("S")) {
                        if (!editEpc.getText().toString().equals("")) {
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
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

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
//        DialogKeyListener dkl = new DialogKeyListener();
//        builder.setOnKeyListener(dkl);
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

        //codigo para ler epc por UHC
//        dkl.ItemEpcListener(new DialogKeyListener.ItemEPC() {
//            @Override
//            public void onClickEpc(String epc) {
//                if (editEpc.isShown()) {
//                    if (!epc.equals("")) {
//                        if (!epcDao.existeEpc(epc)) {
//                            editEpc.setText(epc);
//                            i.setEpc(epc);
//                        }
//                    }
//                }
//
//            }
//        });

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
}

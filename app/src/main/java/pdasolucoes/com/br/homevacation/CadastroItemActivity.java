package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaItemAdapter;
import pdasolucoes.com.br.homevacation.Dao.EpcDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.Categoria;
import pdasolucoes.com.br.homevacation.Model.EPC;
import pdasolucoes.com.br.homevacation.Model.Item;
import pdasolucoes.com.br.homevacation.Service.ItemService;
import pdasolucoes.com.br.homevacation.Util.DialogKeyListener;

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
    List<Item> listaItemGeneric;
    private ProgressDialog progressDialog;
    private Categoria categoria;
    private List<Categoria> listaCategoria;
    ArrayAdapter<Item> arrayAdapter;
    Spinner spinner;
    private EpcDao epcDao;
    public static RFIDWithUHF mReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

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
                AsyncCategoriaGeneric taskGeneric = new AsyncCategoriaGeneric();
                taskGeneric.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

        AsyncItem task = new AsyncItem();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ambiente.getId());

        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {
            Toast.makeText(CadastroItemActivity.this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mReader != null) {
            new InitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected void onDestroy() {
        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
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
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            adapter = new ListaItemAdapter(items, CadastroItemActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }

    public class AsyncCategoriaGeneric extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {


            listaCategoria = ItemService.getItemCategoria();
            Categoria c = new Categoria();
            c.setIdCategoria(-1);
            c.setDescricao(getResources().getString(R.string.other));
            listaCategoria.add(c);

            return null;
        }

        @Override
        protected void onPostExecute(Object items) {
            super.onPostExecute(items);
            popupInsereItem();
        }

        public class AsyncItemGeneric extends AsyncTask<Integer, Void, Object> {

            @Override
            protected Object doInBackground(Integer... params) {

                listaItemGeneric = ItemService.getItemGenerico(params[0]);
                Item i = new Item();
                i.setIdItem(-1);
                i.setDescricao(getResources().getString(R.string.other));
                i.setIdUsuario(0);
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
            final AlertDialog dialog;
            DialogKeyListener dkl = new DialogKeyListener();
            builder.setOnKeyListener(dkl);
            final TextInputEditText editItem = (TextInputEditText) v.findViewById(R.id.editRoom);
            final TextInputEditText editEpc = (TextInputEditText) v.findViewById(R.id.editEPC);
            final TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
            final TextInputEditText editCategoria = (TextInputEditText) v.findViewById(R.id.editCategoria);
            Button btDone = (Button) v.findViewById(R.id.btDone);
            Button btCancel = (Button) v.findViewById(R.id.btCancel);
            RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
            RadioGroup radioGroupEvidence = (RadioGroup) v.findViewById(R.id.radioGroupEvidence);
            spinner = (Spinner) v.findViewById(R.id.spinnerItem);
            Spinner spinnerCategoria = (Spinner) v.findViewById(R.id.spinnerCategoria);
            final TextInputLayout textInputStock = (TextInputLayout) v.findViewById(R.id.textInputLStock);
            final TextInputLayout textInputItem = (TextInputLayout) v.findViewById(R.id.textInputItem);
            final TextInputLayout textInputEpc = (TextInputLayout) v.findViewById(R.id.textInputEpc);
            final TextInputLayout textInputCategoria = (TextInputLayout) v.findViewById(R.id.textInputCategoria);

            builder.setView(v);
            dialog = builder.create();
            dialog.show();


            item = new Item();
            categoria = new Categoria();

            ArrayAdapter<Categoria> arrayCategoria =
                    new ArrayAdapter<>(CadastroItemActivity.this, android.R.layout.simple_list_item_1, listaCategoria);
            spinnerCategoria.setAdapter(arrayCategoria);

            spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Categoria c = (Categoria) parent.getItemAtPosition(position);

                    if (c.getIdCategoria() == -1) {
                        textInputCategoria.setVisibility(View.VISIBLE);
                        textInputItem.setVisibility(View.VISIBLE);
                        spinner.setEnabled(false);
                        //atualiazinado spinner
                        listaItemGeneric = new ArrayList<>();
                        Item i = new Item();
                        i.setIdItem(-1);
                        i.setDescricao(getString(R.string.other));
                        listaItemGeneric.add(i);
                        arrayAdapter =
                                new ArrayAdapter<>(CadastroItemActivity.this, android.R.layout.simple_list_item_1, listaItemGeneric);

                        spinner.setAdapter(arrayAdapter);
                    } else {
                        spinner.setEnabled(true);
                        textInputItem.setVisibility(View.GONE);
                        textInputCategoria.setVisibility(View.GONE);

                        categoria.setIdCategoria(c.getIdCategoria());
                        categoria.setDescricao(c.getDescricao());

                        AsyncItemGeneric asyncItemGeneric = new AsyncItemGeneric();
                        asyncItemGeneric.executeOnExecutor(THREAD_POOL_EXECUTOR, c.getIdCategoria());


                    }
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
                        rfid = "N";
                    } else {
                        textInputStock.setVisibility(View.GONE);
                        textInputEpc.setVisibility(View.VISIBLE);
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
            dkl.ItemEpcListener(new DialogKeyListener.ItemEPC() {
                @Override
                public void onClickEpc(String epc) {
                    if (editEpc.isShown()) {
                        if (!epc.equals("")) {
                            if (!epcDao.existeEpc(epc)) {
                                editEpc.setText(epc);
                                item.setEpc(epc);
                            }
                        }
                    }

                }
            });

            editCategoria.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (editCategoria.isShown()) {
                        if (!editCategoria.getText().toString().equals("")) {
                            categoria.setDescricao(editCategoria.getText().toString());
                        }
                    }
                }
            });

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
                    item.setIdUsuario(1);
                    item.setIdCasa(ambiente.getIdCasa());

                    List<EPC> epcs = new ArrayList<>();
                    epcs.add(new EPC(1, editEpc.getText().toString()));
                    epcDao.incluir(epcs);

                    if (editCategoria.isShown()) {
                        AsyncSetCategoria asyncSetCategoria = new AsyncSetCategoria();
                        asyncSetCategoria.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, categoria);

                    } else if (editItem.isShown()) {
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


                }
            });
        }

        public class AsyncCadastroItem extends AsyncTask {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CadastroItemActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getString(R.string.load));
                progressDialog.show();

            }

            @Override
            protected Object doInBackground(Object[] params) {

                ItemService.setListaAmbienteItem((Item) params[0]);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                AsyncItem task = new AsyncItem();
                task.executeOnExecutor(THREAD_POOL_EXECUTOR, ambiente.getId());
            }
        }

        public class AsyncSetItem extends AsyncTask<Object, Void, Object> {


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

        public class AsyncSetCategoria extends AsyncTask<Object, Void, Integer> {


            @Override
            protected Integer doInBackground(Object[] params) {
                categoria.setIdCategoria(ItemService.setCategoria(((Categoria) params[0])));

                return categoria.getIdCategoria();
            }

            @Override
            protected void onPostExecute(Integer id) {
                super.onPostExecute(id);

                //passando o novo idCategoria para o item
                item.setIdCategoria(id);
                AsyncSetItem task = new AsyncSetItem();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
            }
        }
    }
}

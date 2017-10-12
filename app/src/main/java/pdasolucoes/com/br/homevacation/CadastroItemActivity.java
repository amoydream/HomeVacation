package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaItemAdapter;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.Item;
import pdasolucoes.com.br.homevacation.Service.ItemService;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvTituloItem = (TextView) findViewById(R.id.tvtTituloToolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

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
                AsyncItemGeneric taskGeneric = new AsyncItemGeneric();
                taskGeneric.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncItem task = new AsyncItem();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ambiente.getId());

    }

    public class AsyncItem extends AsyncTask<Integer, Void, List<Item>> {


        @Override
        protected List<Item> doInBackground(Integer... params) {

            lista = ItemService.getItem(params[0]);
            return lista;
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);

            adapter = new ListaItemAdapter(items, CadastroItemActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }

    public class AsyncItemGeneric extends AsyncTask<Void, Void, List<Item>> {

        @Override
        protected List<Item> doInBackground(Void... params) {

            listaItemGeneric = ItemService.getItemGenerico();
            Item i = new Item();
            i.setIdItem(-1);
            i.setDescricao(getResources().getString(R.string.other));
            i.setIdUsuario(0);
            listaItemGeneric.add(i);

            return listaItemGeneric;
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
            popupInsereItem();
        }
    }

    public void popupInsereItem() {
        View v = View.inflate(CadastroItemActivity.this, R.layout.popup_insere_novo_item, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroItemActivity.this);
        final AlertDialog dialog;
        final TextInputEditText editItem = (TextInputEditText) v.findViewById(R.id.editRoom);
        final TextInputEditText editEpc = (TextInputEditText) v.findViewById(R.id.editEPC);
        final TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        RadioGroup radioGroupEvidence = (RadioGroup) v.findViewById(R.id.radioGroupEvidence);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerItem);
        final TextInputLayout textInputStock = (TextInputLayout) v.findViewById(R.id.textInputLStock);
        final TextInputLayout textInputItem = (TextInputLayout) v.findViewById(R.id.textInputItem);
        final TextInputLayout textInputEpc = (TextInputLayout) v.findViewById(R.id.textInputEpc);

        builder.setView(v);
        dialog = builder.create();
        dialog.show();

        item = new Item();

        ArrayAdapter<Item> arrayAdapter =
                new ArrayAdapter<>(CadastroItemActivity.this, android.R.layout.simple_list_item_1, listaItemGeneric);
        spinner.setAdapter(arrayAdapter);


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

        editEpc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editEpc.isShown()) {
                    if (!editEpc.getText().toString().equals("")) {
                        item.setEpc(editEpc.getText().toString());
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

                List<Item> list = new ArrayList<Item>();
                item.setIdCategoria(1);
                item.setIdAmbiente(ambiente.getId());
                item.setIdUsuario(1);
                item.setIdCasa(ambiente.getIdCasa());
                list.add(item);

                if (editItem.isShown()) {
                    AsyncSetItem asyncSetItem = new AsyncSetItem();
                    asyncSetItem.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list);
                } else {
                    //chamar o async para cadastrar o item
                    AsyncCadastroItem task = new AsyncCadastroItem();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list);
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
            progressDialog.setMessage("carregando...");
            progressDialog.show();

        }

        @Override
        protected Object doInBackground(Object[] params) {

            ItemService.setListaAmbienteItem((List<Item>) params[0]);

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

    public class AsyncSetItem extends AsyncTask<Object, Void, Object[]> {


        @Override
        protected Object[] doInBackground(Object[] params) {
            item.setIdItem(ItemService.setItem(((List<Item>) params[0]).get(0)));

            return params;
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);

            AsyncCadastroItem task = new AsyncCadastroItem();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (List<Item>) objects[0]);

        }
    }
}

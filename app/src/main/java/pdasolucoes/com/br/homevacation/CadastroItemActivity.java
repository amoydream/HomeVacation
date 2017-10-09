package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_item);

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
        TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
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

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chamar o async para cadastrar o item
            }
        });


    }
}

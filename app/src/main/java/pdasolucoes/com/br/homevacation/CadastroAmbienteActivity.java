package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;

import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Service.AmbienteService;

public class CadastroAmbienteActivity extends AppCompatActivity {


    private List<Ambiente> listaAmbiente;
    private ListaAmbienteAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private String descricao = "";
    private TextView tvTituloBar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tvTituloBar = (TextView) findViewById(R.id.tvtTituloToolbar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupInsereAmbiente();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncAmbiente task = new AsyncAmbiente();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class AsyncAmbiente extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CadastroAmbienteActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.load));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
    }

        @Override
        protected Object doInBackground(Object[] params) {

            listaAmbiente = AmbienteService.getAmbiente(OpcaoEntradaActivity.CASA);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            tvTituloBar.setText(listaAmbiente.get(0).getDescricaoCasa());

            adapter = new ListaAmbienteAdapter(listaAmbiente, getApplicationContext());
            recyclerView.setAdapter(adapter);

        }
    }

    public class AsyncInsertRoom extends AsyncTask<Ambiente, Void, Integer> {

        @Override
        protected Integer doInBackground(Ambiente... params) {

            int id = AmbienteService.setAmbiente(params[0]);

            return id;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            //atualizar lista
            onResume();
        }
    }

    public void popupInsereAmbiente() {
        View v = View.inflate(CadastroAmbienteActivity.this, R.layout.popup_insere_novo_ambiente, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroAmbienteActivity.this);
        final AlertDialog dialog;
        final TextInputEditText editText = (TextInputEditText) v.findViewById(R.id.editRoom);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);

        builder.setView(v);
        dialog = builder.create();
        dialog.show();

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descricao = editText.getText().toString();

                Ambiente a = new Ambiente();
                a.setDescricao(descricao);
                a.setOrdem(0);
                a.setIdCasa(OpcaoEntradaActivity.CASA);

                AsyncInsertRoom task = new AsyncInsertRoom();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, a);

                dialog.dismiss();
            }
        });
    }
}

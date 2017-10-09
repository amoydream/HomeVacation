package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import pdasolucoes.com.br.homevacation.Adapter.ListaAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Service.AmbienteService;
import pdasolucoes.com.br.homevacation.Util.PopupsDialog;

public class CadastroAmbienteActivity extends AppCompatActivity {


    private List<Ambiente> listaAmbiente;
    private ListaAmbienteAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private String descricao = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_ambiente);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);

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
        protected Object doInBackground(Object[] params) {

            listaAmbiente = AmbienteService.getAmbiente(1);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            adapter = new ListaAmbienteAdapter(listaAmbiente, getApplicationContext());
            recyclerView.setAdapter(adapter);

            adapter.ItemAmbienteListener(new ListaAmbienteAdapter.ItemAmbiente() {
                @Override
                public void onItemClick(Ambiente a) {


//                    Intent i = new Intent(CadastroAmbienteActivity.this, CadastroItemActivity.class);
//                    i.putExtra("ambiente", a);
//                    startActivity(i);
                }
            });
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

        builder.setView(v);
        dialog = builder.create();
        dialog.show();


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descricao = editText.getText().toString();

                Ambiente a = new Ambiente();
                a.setDescricao(descricao);
                a.setOrdem(0);
                a.setIdCasa(1);

                AsyncInsertRoom task = new AsyncInsertRoom();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, a);

                dialog.dismiss();
            }
        });
    }
}

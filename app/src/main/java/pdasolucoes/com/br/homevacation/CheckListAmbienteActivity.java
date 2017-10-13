package pdasolucoes.com.br.homevacation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Service.AmbienteService;
import pdasolucoes.com.br.homevacation.Util.ListaAmbienteDao;

/**
 * Created by PDA on 12/10/2017.
 */

public class CheckListAmbienteActivity extends AppCompatActivity {

    private TextView tvTitulo;
    List<Ambiente> listaAmbiente;
    private ListaChecklistAmbienteAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        tvTitulo = (TextView) findViewById(R.id.tvtTituloToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);


        AsyncAmbiente task = new AsyncAmbiente();
        task.execute();

    }

    public class AsyncAmbiente extends AsyncTask {
        SharedPreferences sharedPreferences = getSharedPreferences("listaAmbiente", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        @Override
        protected Object doInBackground(Object[] params) {

            listaAmbiente = AmbienteService.getAmbiente(CadastroAmbienteActivity.CASA);


            editor.putString("lista", ListaAmbienteDao.salvar(listaAmbiente)).commit();


            return ListaAmbienteDao.listar(sharedPreferences.getString("lista", ""));
        }

        @Override
        protected void onPostExecute(final Object o) {
            super.onPostExecute(o);


            adapter = new ListaChecklistAmbienteAdapter((List<Ambiente>) o, getApplicationContext());
            recyclerView.setAdapter(adapter);

            adapter.ItemClickListener(new ListaChecklistAmbienteAdapter.ItemClick() {
                @Override
                public void onClick(int position) {
                    Intent i = new Intent(CheckListAmbienteActivity.this, CheckListItemActivity.class);
                    i.putExtra("ambiente", (((List<Ambiente>) o).get(position)));
                    i.putExtra("ID_CHECKLIST", getIntent().getIntExtra("ID_CHECKLIST", 0));
                    startActivity(i);

//                    editor.putString("lista", ListaAmbienteDao.alterar((List<Ambiente>) o, ((List<Ambiente>) o).get(position))).commit();
//
//                    adapter = new ListaChecklistAmbienteAdapter(ListaAmbienteDao.listar(sharedPreferences.getString("lista", "")), CheckListAmbienteActivity.this);
//                    recyclerView.setAdapter(adapter);

                }
            });
        }
    }
}

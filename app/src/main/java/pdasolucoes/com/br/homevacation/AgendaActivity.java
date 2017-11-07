package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import pdasolucoes.com.br.homevacation.Adapter.ListaAgendaAdapter;
import pdasolucoes.com.br.homevacation.Model.Agenda;
import pdasolucoes.com.br.homevacation.Service.CheckListService;

/**
 * Created by PDA on 02/11/2017.
 */

public class AgendaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTitulo;
    private ListaAgendaAdapter adapter;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;
    private Intent i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        preferences = getSharedPreferences("Login", MODE_PRIVATE);

        tvTitulo = (TextView) findViewById(R.id.tvtTituloToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        tvTitulo.setText(R.string.agenda);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        AsyncCriaCheckList task = new AsyncCriaCheckList();
        task.execute(preferences.getInt("idUsuario", 0), sdf.format(new Date()));

    }


    private class AsyncCriaCheckList extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AgendaActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.load));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            List<Agenda> lista = CheckListService.CriarCheckList(Integer.parseInt(params[0].toString()), params[1].toString());


            return lista;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


                new AsyncFotos().execute(o);


        }
    }

    private void popupMsg(final Agenda a) {
        View v = View.inflate(AgendaActivity.this, R.layout.popup_msg, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AgendaActivity.this);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        TextView tvConteudo = (TextView) v.findViewById(R.id.conteudo);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);

        tvConteudo.setText(getString(R.string.start_checklist));

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncInicioCheck task = new AsyncInicioCheck();
                task.execute(a.getIdCheckList());
                i = new Intent(AgendaActivity.this, CheckListAmbienteActivity.class);
                i.putExtra("ID_CHECKLIST", a.getIdCheckList());
                i.putExtra("ID_CASA", a.getIdCasa());
                dialog.dismiss();

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private class AsyncInicioCheck extends AsyncTask {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AgendaActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            int result = CheckListService.IniciarCheckList(Integer.parseInt(params[0].toString()));

            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                if (Integer.parseInt(o.toString()) != 0) {
                    startActivity(i);
                    finish();
                }
            }
        }
    }


    private class AsyncFotos extends AsyncTask {
        Bitmap bmp;

        @Override
        protected Object doInBackground(Object[] params) {
            URL url;
            List<Agenda> lista = (List<Agenda>) params[0];
            for (int i = 0; i < lista.size(); i++) {
                try {
                    url = new URL(lista.get(i).getImagem());
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Agenda a = lista.get(i);
                a.setImagemBitMap(bmp);
                lista.set(i, a);
            }


            return lista;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();

                adapter = new ListaAgendaAdapter((List<Agenda>) o, AgendaActivity.this);
                recyclerView.setAdapter(adapter);

                adapter.ItemAgendaListener(new ListaAgendaAdapter.ItemAgenda() {
                    @Override
                    public void onItemClick(Agenda a) {
                        popupMsg(a);
                    }
                });
            }

        }
    }
}

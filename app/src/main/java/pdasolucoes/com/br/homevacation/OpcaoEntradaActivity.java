package pdasolucoes.com.br.homevacation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.rscja.deviceapi.RFIDWithUHF;

import java.util.List;

import pdasolucoes.com.br.homevacation.Dao.CheckListVoltaDao;
import pdasolucoes.com.br.homevacation.Dao.EpcDao;
import pdasolucoes.com.br.homevacation.Dao.QuestaoVoltaDao;
import pdasolucoes.com.br.homevacation.Model.Casa;
import pdasolucoes.com.br.homevacation.Model.EPC;
import pdasolucoes.com.br.homevacation.Service.CasaService;
import pdasolucoes.com.br.homevacation.Service.CheckListService;
import pdasolucoes.com.br.homevacation.Service.EpcService;

/**
 * Created by PDA on 11/10/2017.
 */

public class OpcaoEntradaActivity extends AbsRuntimePermission {

    private ImageView imageCadastro, imageCheckList;
    public static final int REQUEST_PERMISSION = 10;
    private ProgressDialog progressDialog, progressDialog2;
    private CounterFab fab, fab1, fab2, fab3;
    private Boolean isFabOpen = false;
    private CheckListVoltaDao checkListVoltaDao;
    private QuestaoVoltaDao questaoVoltaDao;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    //private TextView tvSync, tvSignOut, tvChange;
    private Casa c;
    private SharedPreferences preferences;
    public static int CASA = 0;
    private EpcDao epcDao;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcao_entrada);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        preferences = getSharedPreferences("Login", MODE_PRIVATE);

        checkListVoltaDao = new CheckListVoltaDao(this);
        questaoVoltaDao = new QuestaoVoltaDao(this);
        epcDao = new EpcDao(this);
        imageCadastro = (ImageView) findViewById(R.id.imageCadastro);
        imageCheckList = (ImageView) findViewById(R.id.imageCheckList);

        imageCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OpcaoEntradaActivity.this, CadastroAmbienteActivity.class);
                startActivity(i);
            }
        });

        imageCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMsg();
            }
        });

        requestAppPermissions(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string.msg, REQUEST_PERMISSION);

        //Floating Action Buttons
        fab = (CounterFab) findViewById(R.id.fab);
        fab1 = (CounterFab) findViewById(R.id.fab_1);
        fab2 = (CounterFab) findViewById(R.id.fab_2);
        fab3 = (CounterFab) findViewById(R.id.fab_3);

//        tvSignOut = (TextView) findViewById(R.id.tvSignOut);
//        tvSync = (TextView) findViewById(R.id.tvSync);
//        tvChange = (TextView) findViewById(R.id.tvChangeHouse);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(OpcaoEntradaActivity.this, R.layout.popup_msg, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(OpcaoEntradaActivity.this);
                final AlertDialog dialog;
                Button btNo = (Button) view.findViewById(R.id.btCancel);
                Button btYes = (Button) view.findViewById(R.id.btDone);
                TextView tvConteudo = (TextView) view.findViewById(R.id.conteudo);
                TextView tvTitle = (TextView) view.findViewById(R.id.title);

                tvTitle.setText(getString(R.string.sign_in));
                tvConteudo.setText(getString(R.string.message_sign_out));

                builder.setView(view);
                dialog = builder.create();
                dialog.show();

                btYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent i = new Intent(OpcaoEntradaActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

                btNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }
        });

        fab2.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                AsyncDevolverCheckList task = new AsyncDevolverCheckList();
                task.execute();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsynCasa task = new AsynCasa();
                task.execute();
            }
        });

        //Animations
        fab_open = AnimationUtils.loadAnimation(

                getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(

                getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(

                getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(

                getApplicationContext(), R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });


        AsynCasa task = new AsynCasa();
        task.execute();

    }

    public class AsynCasa extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            List<Casa> lista = CasaService.GetListaCasa(preferences.getInt("idConta", 0));
            return lista;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            popupSelecionarCasa((List<Casa>) o);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    public class AsyncCriaCheckList extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OpcaoEntradaActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.load));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Object[] params) {
            return CheckListService.CriarCheckList(CASA, 1);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Intent i = new Intent(OpcaoEntradaActivity.this, CheckListAmbienteActivity.class);
                i.putExtra("ID_CHECKLIST", (int) o);
                startActivity(i);
                finish();
            }


        }

    }

    private void popupMsg() {
        View v = View.inflate(OpcaoEntradaActivity.this, R.layout.popup_msg, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(OpcaoEntradaActivity.this);
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
                dialog.dismiss();
                AsyncCriaCheckList task = new AsyncCriaCheckList();
                task.execute();

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

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
//            tvChange.setVisibility(View.INVISIBLE);
//            tvSignOut.setVisibility(View.INVISIBLE);
//            tvSync.setVisibility(View.INVISIBLE);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;

        } else {


            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
//            tvSignOut.setVisibility(View.VISIBLE);
//            tvSync.setVisibility(View.VISIBLE);
//            tvChange.setVisibility(View.VISIBLE);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;

        }
    }

    private class AsyncDevolverCheckList extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog2 = new ProgressDialog(OpcaoEntradaActivity.this);
            progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog2.setMessage(getString(R.string.load));
            progressDialog2.setCanceledOnTouchOutside(true);
            progressDialog2.setCancelable(false);
            progressDialog2.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            boolean enviou = false;
            int resultQuestao = CheckListService.SetChecklistQuestao(questaoVoltaDao.listarTodos());
            int resultItem = CheckListService.SetChecklistItem(checkListVoltaDao.listarTodos());

            if (resultQuestao == 1 || resultItem == 1) {
                enviou = true;
            }

            return enviou;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (progressDialog2.isShowing()) {
                progressDialog2.dismiss();
                if ((boolean) o) {

                    checkListVoltaDao.export(checkListVoltaDao.listarTodos());
                    questaoVoltaDao.export(questaoVoltaDao.listarTodos());

                    if (questaoVoltaDao.listarTodos().size() > 0 || checkListVoltaDao.listarTodos().size() > 0) {
                        popupFinishCheckList();
                    } else {
                        Toast.makeText(OpcaoEntradaActivity.this, getString(R.string.no_checklist), Toast.LENGTH_SHORT).show();
                    }


                    checkListVoltaDao.deleter();
                    questaoVoltaDao.deleter();


                    fab2.setCount(checkListVoltaDao.count(CASA));
                }
            }
        }
    }

    public void popupFinishCheckList() {
        View v = View.inflate(OpcaoEntradaActivity.this, R.layout.popup_msg, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(OpcaoEntradaActivity.this);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        TextView tvConteudo = (TextView) v.findViewById(R.id.conteudo);
        TextView tvTitle = (TextView) v.findViewById(R.id.title);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);

        tvTitle.setText(getString(R.string.congrants));

        tvConteudo.setText(getString(R.string.msg_congrants));

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void popupSelecionarCasa(List<Casa> lista) {
        View v = View.inflate(OpcaoEntradaActivity.this, R.layout.popup_select_house, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(OpcaoEntradaActivity.this);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerHouse);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        c = new Casa();

        ArrayAdapter<Casa> adapterCasa = new ArrayAdapter<>(OpcaoEntradaActivity.this, android.R.layout.simple_list_item_1, lista);
        spinner.setAdapter(adapterCasa);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                c = (Casa) parent.getItemAtPosition(position);
                fab2.setCount(checkListVoltaDao.count(c.getId()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CASA = c.getId();
                //delentando todos os epcs cadastrados no sqlite
                epcDao.deletar();
                AsyncGetEpc asyncGetEpc = new AsyncGetEpc();
                asyncGetEpc.execute(CASA);

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class AsyncGetEpc extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            List<EPC> lista = EpcService.GetListaEPC((Integer) params[0]);

            return lista;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            epcDao.incluir((List<EPC>) o);
        }
    }

}

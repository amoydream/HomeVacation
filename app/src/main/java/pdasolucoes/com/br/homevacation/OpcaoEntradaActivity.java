package pdasolucoes.com.br.homevacation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.rscja.deviceapi.RFIDWithUHF;

import pdasolucoes.com.br.homevacation.Dao.CheckListVoltaDao;
import pdasolucoes.com.br.homevacation.Dao.QuestaoVoltaDao;
import pdasolucoes.com.br.homevacation.Model.CheckListVolta;
import pdasolucoes.com.br.homevacation.Service.CheckListService;
import pdasolucoes.com.br.homevacation.Util.ListaAmbienteDao;

import static pdasolucoes.com.br.homevacation.CadastroAmbienteActivity.CASA;

/**
 * Created by PDA on 11/10/2017.
 */

public class OpcaoEntradaActivity extends AbsRuntimePermission {

    private ImageView imageCadastro, imageCheckList;
    public static final int REQUEST_PERMISSION = 10;
    private ProgressDialog progressDialog, progressDialog2;
    private CounterFab fab, fab1, fab2;
    private Boolean isFabOpen = false;
    private CheckListVoltaDao checkListVoltaDao;
    private QuestaoVoltaDao questaoVoltaDao;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    public static RFIDWithUHF mReader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcao_entrada);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        checkListVoltaDao = new CheckListVoltaDao(this);
        questaoVoltaDao = new QuestaoVoltaDao(this);
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

        fab2.setCount(checkListVoltaDao.count());

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncDevolverCheckList task = new AsyncDevolverCheckList();
                task.execute();
            }
        });

        //Animations
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {

            Toast.makeText(OpcaoEntradaActivity.this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (mReader != null) {
            new InitTask().execute();
        }
    }

    @Override
    protected void onDestroy() {
        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
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
                Toast.makeText(OpcaoEntradaActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(OpcaoEntradaActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
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
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {


            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
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
                    //ao invés do Toast, será um popup
                    popupFinishCheckList();

                    fab2.setCount(checkListVoltaDao.count());
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
}

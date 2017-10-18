package pdasolucoes.com.br.homevacation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pdasolucoes.com.br.homevacation.Service.CheckListService;
import pdasolucoes.com.br.homevacation.Util.ListaAmbienteDao;

import static pdasolucoes.com.br.homevacation.CadastroAmbienteActivity.CASA;

/**
 * Created by PDA on 11/10/2017.
 */

public class OpcaoEntradaActivity extends AbsRuntimePermission {

    private ImageView imageCadastro, imageCheckList;
    public static final int REQUEST_PERMISSION = 10;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcao_entrada);

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

        requestAppPermissions(new String[]{Manifest.permission.CAMERA}, R.string.msg, REQUEST_PERMISSION);

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
}

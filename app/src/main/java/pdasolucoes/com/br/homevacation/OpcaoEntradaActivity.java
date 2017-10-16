package pdasolucoes.com.br.homevacation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import pdasolucoes.com.br.homevacation.Service.CheckListService;

import static pdasolucoes.com.br.homevacation.CadastroAmbienteActivity.CASA;

/**
 * Created by PDA on 11/10/2017.
 */

public class OpcaoEntradaActivity extends AppCompatActivity {

    private ImageView imageCadastro,imageCheckList;

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
                AsyncCriaCheckList task = new AsyncCriaCheckList();
                task.execute();
            }
        });

    }

    public class AsyncCriaCheckList extends AsyncTask {


        @Override
        protected Integer doInBackground(Object[] params) {


            return CheckListService.CriarCheckList(CASA, 1);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            Intent i = new Intent(OpcaoEntradaActivity.this, CheckListAmbienteActivity.class);
            i.putExtra("ID_CHECKLIST", (int) o);
            startActivity(i);
        }
    }
}

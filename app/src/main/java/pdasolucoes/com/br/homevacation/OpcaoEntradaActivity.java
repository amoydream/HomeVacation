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

    private LinearLayout llVo, llVo1, llPai, llFilho;
    private ImageView imageCadastro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcao_entrada);

        llPai = (LinearLayout) findViewById(R.id.layoutPai);
        llVo = (LinearLayout) findViewById(R.id.layoutVo);
        llFilho = (LinearLayout) findViewById(R.id.layoutFilho);
        imageCadastro = (ImageView) findViewById(R.id.imageCadastro);

        llVo1 = (LinearLayout) findViewById(R.id.layoutVo1);

        llVo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                lp.weight = (float) 0.9;

                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                lp2.weight = (float) 0.1;


                Animation slideUp = AnimationUtils.loadAnimation(OpcaoEntradaActivity.this, R.anim.slide_up);
                imageCadastro.setLayoutParams(lp2);
                llPai.setLayoutParams(lp);
                llFilho.startAnimation(slideUp);
                llFilho.setVisibility(View.VISIBLE);

                Intent i = new Intent(OpcaoEntradaActivity.this, CadastroAmbienteActivity.class);
                startActivity(i);
            }
        });

        llVo1.setOnClickListener(new View.OnClickListener() {
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

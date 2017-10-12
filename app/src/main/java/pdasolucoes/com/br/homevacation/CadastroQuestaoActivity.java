package pdasolucoes.com.br.homevacation;

import android.accounts.AuthenticatorException;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaQuestaoAdapter;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.Questao;
import pdasolucoes.com.br.homevacation.Service.QuestaoService;

/**
 * Created by PDA on 11/10/2017.
 */

public class CadastroQuestaoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ListaQuestaoAdapter adapter;
    private List<Questao> listaQuestao;
    private Ambiente ambiente;
    private TextView tvTituloBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tvTituloBar = (TextView) findViewById(R.id.tvtTituloToolbar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        ambiente = (Ambiente) getIntent().getSerializableExtra("ambiente");

        tvTituloBar.setText(ambiente.getDescricao());

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupInsereQuestao();
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        AsyncQuestao task = new AsyncQuestao();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ambiente.getId());
    }

    public class AsyncQuestao extends AsyncTask {

        @Override
        protected List<Questao> doInBackground(Object[] params) {

            listaQuestao = QuestaoService.GetListaQuestao((Integer) params[0]);

            return listaQuestao;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            adapter = new ListaQuestaoAdapter(CadastroQuestaoActivity.this, (List<Questao>) o);
            recyclerView.setAdapter(adapter);
        }
    }

    public class AsyncInsereQuestao extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {

            Questao q = QuestaoService.SetQuestao((Questao) params[0]);
            return q;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            listaQuestao.add((Questao) o);
            adapter = new ListaQuestaoAdapter(CadastroQuestaoActivity.this, listaQuestao);
            recyclerView.setAdapter(adapter);
        }
    }

    private void popupInsereQuestao() {
        View v = View.inflate(CadastroQuestaoActivity.this, R.layout.popup_insere_nova_questao, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroQuestaoActivity.this);
        final AlertDialog dialog;
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupEvidence);
        final TextInputEditText editQuestion = (TextInputEditText) v.findViewById(R.id.editQuestion);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        final Questao questao = new Questao();

        builder.setView(v);
        dialog = builder.create();
        dialog.show();

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                questao.setIdAmbiente(ambiente.getId());
                questao.setIdUsuario(1);

                if (!editQuestion.getText().toString().equals("")) {
                    questao.setDescricao(editQuestion.getText().toString());

                    AsyncInsereQuestao task = new AsyncInsereQuestao();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, questao);

                    dialog.dismiss();
                } else {
                    Toast.makeText(CadastroQuestaoActivity.this, getString(R.string.preencha_campo), Toast.LENGTH_SHORT).show();
                }

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton r = (RadioButton) group.findViewById(checkedId);
                String evidence = "";

                if (r.getText().toString().equals("No")) {
                    evidence = "N";
                } else {
                    evidence = "S";
                }

                questao.setEvidencia(evidence);
            }
        });

        editQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}

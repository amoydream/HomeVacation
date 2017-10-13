package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Adapter.ListaChecklistItemAdapter;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.CheckList;
import pdasolucoes.com.br.homevacation.Service.CheckListService;

/**
 * Created by PDA on 13/10/2017.
 */

public class CheckListItemActivity extends AppCompatActivity {

    private TextView tvTitulo;
    List<CheckList> listaCheckList;
    private ListaChecklistItemAdapter adapter;
    private RecyclerView recyclerView;
    private Ambiente ambiente;

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

        ambiente = (Ambiente) getIntent().getSerializableExtra("ambiente");
        tvTitulo.setText(ambiente.getDescricao());

        AsyncChecklistItem task = new AsyncChecklistItem();
        task.execute();

    }

    public class AsyncChecklistItem extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            listaCheckList = CheckListService.GetListaCheckListItens(getIntent().getIntExtra("ID_CHECKLIST", 0));

            return listaCheckList;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            adapter = new ListaChecklistItemAdapter((List<CheckList>) o, CheckListItemActivity.this);
            recyclerView.setAdapter(adapter);

            adapter.ItemClickListener(new ListaChecklistItemAdapter.ItemClick() {
                @Override
                public void onClick(int position) {
                    popupAction(position);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void popupAction(int position) {
        View v = View.inflate(CheckListItemActivity.this, R.layout.popup_action, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemActivity.this);
        ImageView imageRfid, imageCamera, imageEstoque;
        imageRfid = (ImageView) v.findViewById(R.id.imageRfid);
        imageCamera = (ImageView) v.findViewById(R.id.imageCamera);
        imageEstoque = (ImageView) v.findViewById(R.id.imageEstoque);
        final AlertDialog dialog;
        builder.setView(v);

        //Gone(invisivel) = 8
        //Visible = 0

        CheckList c = listaCheckList.get(position);
        if (c.getRfid().equals("S")) {
            imageRfid.setVisibility(View.VISIBLE);
        } else {
            imageRfid.setVisibility(View.GONE);
        }

        if (c.getEvidencia().equals("S")) {
            imageCamera.setVisibility(View.VISIBLE);
        } else {
            imageCamera.setVisibility(View.GONE);
        }

        if (c.getEstoque() > 0) {
            imageEstoque.setVisibility(View.VISIBLE);
        } else {
            imageEstoque.setVisibility(View.GONE);
        }

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });

        imageEstoque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupQuantidade();
            }
        });


        dialog = builder.create();
        dialog.show();


    }

    public void popupQuantidade() {
        View v = View.inflate(CheckListItemActivity.this, R.layout.popup_insere_qtde, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemActivity.this);
        final TextInputEditText editQtde = (TextInputEditText) v.findViewById(R.id.editQtde);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        final AlertDialog dialog;
        builder.setView(v);
        dialog = builder.create();

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editQtde.getText().toString().equals("")) {
                    dialog.dismiss();
                } else {
                    Toast.makeText(CheckListItemActivity.this, getString(R.string.preencha_campo), Toast.LENGTH_SHORT).show();
                }

            }
        });


        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139) {
            Toast.makeText(CheckListItemActivity.this, "Olha o RFID ai hahaha", Toast.LENGTH_SHORT).show();
        }

        return super.onKeyDown(keyCode, event);
    }
}

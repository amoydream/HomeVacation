package pdasolucoes.com.br.homevacation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pdasolucoes.com.br.homevacation.Adapter.ListaAmbienteAdapter;
import pdasolucoes.com.br.homevacation.Dao.FotosAmbienteDao;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.Model.Casa;
import pdasolucoes.com.br.homevacation.Model.FotoAmbiente;
import pdasolucoes.com.br.homevacation.Service.AmbienteService;
import pdasolucoes.com.br.homevacation.Service.FotoAmbienteService;
import pdasolucoes.com.br.homevacation.Util.ImageResizeUtils;
import pdasolucoes.com.br.homevacation.Util.SDCardUtils;

public class CadastroAmbienteActivity extends AppCompatActivity {


    private List<Ambiente> listaAmbiente;
    private ListaAmbienteAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private String descricao = "";
    private TextView tvTituloBar;
    private ProgressDialog progressDialog;
    private FotosAmbienteDao fotosAmbienteDao;
    private File file;
    //    private FotoAmbiente fotoAmbiente;
    private List<FotoAmbiente> listaFotoAmbiente;
    private AlertDialog dialog = null;
    private LinearLayout pictures, newPictures;
    private int flag = 0;
    private SharedPreferences preferences;
    private FotoAmbiente fotoAmbiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        preferences = getSharedPreferences("Login", MODE_PRIVATE);

        fotosAmbienteDao = new FotosAmbienteDao(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tvTituloBar = (TextView) findViewById(R.id.tvtTituloToolbar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //arrumar, pois está chegando a mesma imagem no banco
                listaFotoAmbiente = new ArrayList<>();
                popupInsereAmbiente();


            }
        });

        if (savedInstanceState != null) {
            file = (File) savedInstanceState.getSerializable("file");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("file", file);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AsyncAmbiente task = new AsyncAmbiente();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if (dialog != null && dialog.isShowing()) {
            if (listaFotoAmbiente.size() >= 0) {
                newPictures.setVisibility(View.VISIBLE);

                ImageView imageView = new ImageView(this);

                if (!fotoAmbiente.getCaminhoFoto().equals("") && flag == 0) {
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(128, 256);
                    llp.setMargins(0, 0, 10, 0);
                    imageView.setLayoutParams(llp);

                    Uri uri = Uri.parse(fotoAmbiente.getCaminhoFoto());
                    int w = imageView.getWidth();
                    int h = imageView.getHeight();
                    Bitmap bitmap = ImageResizeUtils.getResizedImage(uri, w, h, false);
                    imageView.setImageBitmap(bitmap);
                    pictures.addView(imageView);
                    flag = 1;
                }
            }
        }


    }

    public class AsyncAmbiente extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CadastroAmbienteActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.load));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            listaAmbiente = AmbienteService.getAmbiente(OpcaoEntradaActivity.CASA);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            try {
                tvTituloBar.setText(listaAmbiente.get(0).getDescricaoCasa());

                adapter = new ListaAmbienteAdapter(listaAmbiente, getApplicationContext());
                recyclerView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }

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

            AsynAddFoto asynAddFoto = new AsynAddFoto();
            asynAddFoto.executeOnExecutor(THREAD_POOL_EXECUTOR, integer);

        }
    }

    private class AsynAddFoto extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {


            FotoAmbienteService.setListaAmbienteItem(listaFotoAmbiente, (Integer) params[0]);

            return null;
        }
    }

    public void popupInsereAmbiente() {
        View v = View.inflate(CadastroAmbienteActivity.this, R.layout.popup_insere_novo_ambiente, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroAmbienteActivity.this);
        final TextInputEditText editText = (TextInputEditText) v.findViewById(R.id.editRoom);
        newPictures = (LinearLayout) v.findViewById(R.id.newPictures);
        pictures = (LinearLayout) v.findViewById(R.id.picture);
        ImageView addImage = (ImageView) v.findViewById(R.id.addImage);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);

        builder.setView(v);
        dialog = builder.create();
        dialog.show();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeImagem = System.currentTimeMillis() + ".jpg";
                file = SDCardUtils.getPrivateFile(getBaseContext(), nomeImagem, Environment.DIRECTORY_PICTURES);
                // Chama a intent informando o arquivo para salvar a foto
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Context context = getBaseContext();
                Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, 0);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                descricao = editText.getText().toString();
                if (listaFotoAmbiente.size() == 0) {
                    Toast.makeText(CadastroAmbienteActivity.this, getString(R.string.take_picture), Toast.LENGTH_SHORT).show();
                } else if (descricao.equals("")) {
                    Toast.makeText(CadastroAmbienteActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
                } else {
                    Ambiente a = new Ambiente();
                    a.setDescricao(descricao);
                    a.setOrdem(0);
                    a.setIdCasa(OpcaoEntradaActivity.CASA);

                    AsyncInsertRoom task = new AsyncInsertRoom();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, a);

                    dialog.dismiss();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                if (file != null && file.exists()) {
                    Log.d("foto", file.getAbsolutePath());

                    Uri imageUri = Uri.fromFile(file);
                    fotoAmbiente = new FotoAmbiente();
                    fotoAmbiente.setIdUsuario(preferences.getInt("idUsuario", 0));
                    fotoAmbiente.setCaminhoFoto(file.getPath());

                    listaFotoAmbiente.add(fotoAmbiente);

                    Intent i = new Intent(CadastroAmbienteActivity.this, PopupImage.class);
                    i.putExtra("imageUri", imageUri);
                    startActivity(i);

                    flag = 0;
                }
            }
        }
    }
}

package pdasolucoes.com.br.homevacation.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import pdasolucoes.com.br.homevacation.CadastroItemActivity;
import pdasolucoes.com.br.homevacation.CadastroQuestaoActivity;
import pdasolucoes.com.br.homevacation.Model.Agenda;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 05/10/2017.
 */

public class ListaAgendaAdapter extends RecyclerView.Adapter<ListaAgendaAdapter.MyViewHolder> {

    private List<Agenda> lista;
    private Context context;
    private LayoutInflater layoutInflater;
    private ItemAgenda itemAgenda;

    public interface ItemAgenda {
        void onItemClick(Agenda a);
    }

    public void ItemAgendaListener(ItemAgenda itemAmbiente) {
        this.itemAgenda = itemAmbiente;
    }

    public ListaAgendaAdapter(List<Agenda> lista, Context context) {
        this.lista = lista;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ListaAgendaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.adapter_list_item_agenda, parent, false);

        MyViewHolder mv = new MyViewHolder(v);

        return mv;
    }

    @Override
    public void onBindViewHolder(final ListaAgendaAdapter.MyViewHolder holder, final int position) {

        final Agenda a = lista.get(position);

        holder.imageCasa.setImageBitmap(a.getImagemBitMap());

        holder.imageCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPicture(a.getImagemBitMap());
            }
        });

        holder.tvComunidade.setText(a.getComunidade());

        holder.tvData.setText(a.getDataAgenda());

        holder.tvNomeCasa.setText(a.getDescricaoCasa());


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvData, tvNomeCasa, tvComunidade;
        public ImageView imageCasa;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageCasa = (CircleImageView) itemView.findViewById(R.id.tvImageCasa);
            tvNomeCasa = (TextView) itemView.findViewById(R.id.tvNomeCasa);
            tvComunidade = (TextView) itemView.findViewById(R.id.tvComunidade);
            tvData = (TextView) itemView.findViewById(R.id.tvData);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemAgenda.onItemClick(lista.get(getAdapterPosition()));
        }
    }

    private void popupPicture(Bitmap bitmap) {
        View v = View.inflate(context, R.layout.popup_picture, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v);
        Button btDone = (Button) v.findViewById(R.id.btDone);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        final AlertDialog dialog;

        dialog = builder.create();

        btDone.setText(context.getString(R.string.ok));

        btCancel.setVisibility(View.GONE);

        ImageView imageView = (ImageView) v.findViewById(R.id.image);

        imageView.setImageBitmap(bitmap);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
}

package pdasolucoes.com.br.homevacation.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.EventListener;
import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Pendente;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 21/12/2017.
 */

public class ListaPendenteAdapter extends RecyclerView.Adapter<ListaPendenteAdapter.MyViewHolder> {

    private Context context;
    private List<Pendente> lista;
    private LayoutInflater layoutInflater;
    private ItemClick itemClick;

    public interface ItemClick {
        void onClick(int position);
    }

    public void ItemClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public ListaPendenteAdapter(Context context, List<Pendente> lista) {
        this.context = context;
        this.lista = lista;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ListaPendenteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.adapter_list_item_pendente, parent, false);

        MyViewHolder mv = new MyViewHolder(v);
        return mv;
    }

    @Override
    public void onBindViewHolder(ListaPendenteAdapter.MyViewHolder holder, int position) {
        Pendente p = lista.get(position);

        holder.tvLetra.setText(p.getDescricao().substring(0, 1));

        holder.tvDescricao.setText(p.getDescricao());

        holder.tvSubdescricao.setText(p.getSubDescricao());

        if (p.getTipo().equals("RFID")) {

            holder.image.setImageResource(R.drawable.ic_rfid_chip_black);

        } else if (p.getTipo().equals("STOCK")) {
            holder.image.setImageResource(R.drawable.ic_warehouse_black);
        } else {
            holder.image.setImageResource(R.drawable.ic_question_black);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvDescricao, tvSubdescricao, tvLetra;
        public ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvDescricao = (TextView) itemView.findViewById(R.id.tvDescricao);
            tvSubdescricao = (TextView) itemView.findViewById(R.id.tvSubDescricao);
            tvLetra = (TextView) itemView.findViewById(R.id.tvLetra);
            image = (ImageView) itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(getAdapterPosition());
        }
    }
}

package pdasolucoes.com.br.homevacation.Adapter;

import android.content.Context;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import pdasolucoes.com.br.homevacation.Model.CheckList;;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 13/10/2017.
 */

public class ListaChecklistItemAdapter extends RecyclerView.Adapter<ListaChecklistItemAdapter.MyViewHolder> {

    private List<CheckList> lista;
    private Context context;
    private LayoutInflater layoutInflater;
    private ItemClick itemClick;

    public interface ItemClick {
        void onClick(int position);
    }

    public void ItemClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public ListaChecklistItemAdapter(List<CheckList> lista, Context context) {
        this.lista = lista;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.adapter_list_checklist_item, parent, false);

        MyViewHolder mv = new MyViewHolder(v);

        return mv;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        CheckList c = lista.get(position);

        holder.tvLetra.setText(c.getItem().substring(0, 1));

        Random r = new Random();

        holder.tvLetra.setBackgroundResource(R.drawable.border_item_lista);

        holder.tvItem.setText(c.getItem());

        holder.tvCategoria.setText(c.getCategoria());

        if (c.getEstoque() > 1) {
            holder.imageEstoque.setImageResource(R.drawable.ic_warehouse_black);

        } else {
            holder.imageEstoque.setVisibility(View.GONE);
        }

        if (c.getRfid().equals("S")) {
            if (c.getAchou() == 0) {
                holder.imageRfid.setImageResource(R.drawable.ic_rfid_chip_red);
                //holder.imageRfid.setBackgroundResource(R.drawable.ic_rfid_chip_red);
            } else {
                holder.imageRfid.setImageResource(R.drawable.ic_rfid_chip_green);
                //holder.imageRfid.setBackgroundResource(R.drawable.ic_rfid_chip_green);
            }

        } else
            holder.imageRfid.setVisibility(View.GONE);

        if (c.getEvidencia().equals("S")) {
            holder.imageCamera.setImageResource(R.drawable.ic_camera_alt_black_24dp);

        } else {
            holder.imageCamera.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvItem, tvCategoria, tvLetra;
        public ImageView imageRfid, imageCamera, imageEstoque;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            tvCategoria = (TextView) itemView.findViewById(R.id.tvCategoria);
            tvLetra = (TextView) itemView.findViewById(R.id.tvLetra);
            imageRfid = (ImageView) itemView.findViewById(R.id.imageRfid);
            imageCamera = (ImageView) itemView.findViewById(R.id.imageCamera);
            imageEstoque = (ImageView) itemView.findViewById(R.id.imageEstoque);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(getAdapterPosition());
        }
    }
}

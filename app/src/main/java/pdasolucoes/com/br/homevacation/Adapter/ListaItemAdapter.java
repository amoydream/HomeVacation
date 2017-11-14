package pdasolucoes.com.br.homevacation.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Random;

import pdasolucoes.com.br.homevacation.Model.Item;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 05/10/2017.
 */

public class ListaItemAdapter extends RecyclerView.Adapter<ListaItemAdapter.MyViewHolder> {

    private List<Item> lista;
    private Context context;
    private LayoutInflater layoutInflater;
    private ItemClick itemClick;

    public interface ItemClick {
        void onClick(int position);
    }

    public void ItemClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public ListaItemAdapter(List<Item> lista, Context context) {
        this.lista = lista;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ListaItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.adapter_list_item_item, parent, false);

        MyViewHolder mv = new MyViewHolder(v);

        return mv;
    }

    @Override
    public void onBindViewHolder(ListaItemAdapter.MyViewHolder holder, int position) {

        Item i = lista.get(position);

        holder.tvLetra.setText(i.getDescricao().substring(0, 1));

        holder.tvLetra.setBackgroundResource(R.drawable.border_item_lista);

        holder.tvItem.setText(i.getDescricao());

        if (i.getEstoque() > 1) holder.imageEstoque.setImageResource(R.drawable.ic_warehouse_black);
        else holder.imageEstoque.setImageResource(R.drawable.ic_warehouse_gray);


        if (i.getRfid().equals("S")) {
            holder.imageRfid.setImageResource(R.drawable.ic_rfid_chip_black);
            if (i.getEpc().equals(" - ")) {
                holder.imageRfid.setImageResource(R.drawable.ic_rfid_chip_red);
            }
        } else {
            holder.imageRfid.setImageResource(R.drawable.ic_rfid_chip_gray);
        }

        if (i.getEvidencia().equals("S"))
            holder.imageCamera.setImageResource(R.drawable.ic_camera_alt_black_24dp);
        else holder.imageCamera.setImageResource(R.drawable.ic_camera_alt_gray_24dp);

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

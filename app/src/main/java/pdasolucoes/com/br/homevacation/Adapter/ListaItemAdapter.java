package pdasolucoes.com.br.homevacation.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Item;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 05/10/2017.
 */

public class ListaItemAdapter extends RecyclerView.Adapter<ListaItemAdapter.MyViewHolder> {

    private List<Item> lista;
    private Context context;
    private LayoutInflater layoutInflater;

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

        holder.tvItem.setText(i.getDescricao());

        holder.tvCategoria.setText(i.getCategoria());

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem, tvCategoria, tvLetra;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            tvCategoria = (TextView) itemView.findViewById(R.id.tvCategoria);
            tvLetra = (TextView) itemView.findViewById(R.id.tvLetra);
        }
    }
}

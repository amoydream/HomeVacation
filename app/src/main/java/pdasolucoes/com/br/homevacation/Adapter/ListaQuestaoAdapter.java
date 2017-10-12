package pdasolucoes.com.br.homevacation.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pdasolucoes.com.br.homevacation.Model.Questao;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 11/10/2017.
 */

public class ListaQuestaoAdapter extends RecyclerView.Adapter<ListaQuestaoAdapter.MyViewHolder> {

    private Context context;
    private List<Questao> lista;
    private LayoutInflater layoutInflater;
    private ItemClick itemClick;

    public interface ItemClick {
        void onClick(int position);
    }

    public void ItemOnClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public ListaQuestaoAdapter(Context context, List<Questao> lista) {
        this.context = context;
        this.lista = lista;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ListaQuestaoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.adapter_list_item_questao, parent, false);

        MyViewHolder mv = new MyViewHolder(v);

        return mv;
    }

    @Override
    public void onBindViewHolder(ListaQuestaoAdapter.MyViewHolder holder, int position) {

        Questao q = lista.get(position);

        holder.tvLetra.setText(q.getDescricao().substring(0, 1));

        holder.tvQuestao.setText(q.getDescricao());

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvLetra, tvTitulo, tvQuestao;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLetra = (TextView) itemView.findViewById(R.id.tvLetra);
            tvQuestao = (TextView) itemView.findViewById(R.id.tvQuestion);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(getAdapterPosition());
        }
    }
}

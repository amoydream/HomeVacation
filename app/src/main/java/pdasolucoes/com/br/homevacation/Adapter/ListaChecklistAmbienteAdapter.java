package pdasolucoes.com.br.homevacation.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 12/10/2017.
 */

public class ListaChecklistAmbienteAdapter extends RecyclerView.Adapter<ListaChecklistAmbienteAdapter.MyViewHolder> {

    private List<Ambiente> lista;
    private Context context;
    private LayoutInflater layoutInflater;
    private ItemClick itemClick;

    public interface ItemClick {
        void onClick(int position);
    }

    public void ItemClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public ListaChecklistAmbienteAdapter(List<Ambiente> lista, Context context) {
        this.lista = lista;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ListaChecklistAmbienteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.adapter_list_item_checklist_ambiente, parent, false);

        MyViewHolder mv = new MyViewHolder(v);

        return mv;
    }

    @Override
    public void onBindViewHolder(ListaChecklistAmbienteAdapter.MyViewHolder holder, int position) {

        final Ambiente a = lista.get(position);

        holder.tvLetra.setText(a.getDescricao().substring(0, 1));

        Random r = new Random();


        if (a.isRespondido()) {
            int color = r.nextInt(3 - 0 + 1) + 0;
            holder.tvLetra.setBackgroundResource(context.getResources().obtainTypedArray(R.array.drawable).getResourceId(color, -1));
        }

        holder.tvItem.setText(a.getDescricao());

        holder.tvqtdeItems.setText(" " + String.format("%d", a.getItens()));

        holder.tvQtdeQuestion.setText(" " + String.format("%d", a.getQuestoes()));

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvLetra, tvItem, tvqtdeItems, tvQtdeQuestion;
        public LinearLayout ll;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvLetra = (TextView) itemView.findViewById(R.id.tvLetra);
            tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            tvqtdeItems = (TextView) itemView.findViewById(R.id.tvItems);
            tvQtdeQuestion = (TextView) itemView.findViewById(R.id.tvQtde);
            ll = (LinearLayout) itemView.findViewById(R.id.buttonLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(getAdapterPosition());
        }
    }
}

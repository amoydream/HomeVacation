package pdasolucoes.com.br.homevacation.Adapter;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;
import java.util.Random;

import pdasolucoes.com.br.homevacation.CadastroItemActivity;
import pdasolucoes.com.br.homevacation.CadastroQuestaoActivity;
import pdasolucoes.com.br.homevacation.Model.Ambiente;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 05/10/2017.
 */

public class ListaAmbienteAdapter extends RecyclerView.Adapter<ListaAmbienteAdapter.MyViewHolder> {

    private List<Ambiente> lista;
    private Context context;
    private LayoutInflater layoutInflater;
    private ItemAmbiente itemAmbiente;

    public interface ItemAmbiente {
        void onItemClick(Ambiente a);
    }

    public void ItemAmbienteListener(ItemAmbiente itemAmbiente) {
        this.itemAmbiente = itemAmbiente;
    }

    public ListaAmbienteAdapter(List<Ambiente> lista, Context context) {
        this.lista = lista;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ListaAmbienteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.adapter_list_item_ambiente, parent, false);

        MyViewHolder mv = new MyViewHolder(v);

        return mv;
    }

    @Override
    public void onBindViewHolder(final ListaAmbienteAdapter.MyViewHolder holder, final int position) {

        final Ambiente a = lista.get(position);

        holder.tvLetra.setText(a.getDescricao().substring(0, 1));

        Random r = new Random();

        holder.tvLetra.setBackgroundResource(R.drawable.border_item_lista);

        holder.tvItem.setText(a.getDescricao());

        holder.tvqtdeItems.setText(" " + String.format("%d", a.getItens()));

        holder.tvQtdeQuestion.setText(" " + String.format("%d", a.getQuestoes()));

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.linearLayout.isShown()) {
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    holder.imageArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                } else {
                    holder.linearLayout.setVisibility(View.GONE);
                    holder.imageArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);

                }
            }
        });

        holder.btInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CadastroItemActivity.class);
                intent.putExtra("ambiente", a);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.btQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CadastroQuestaoActivity.class);
                intent.putExtra("ambiente", a);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
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

        public TextView tvLetra, tvItem, tvqtdeItems, tvQtdeQuestion;
        public LinearLayout ll, linearLayout;
        public ImageView imageArrow;
        public Button btQuestion, btInventory;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLetra = (TextView) itemView.findViewById(R.id.tvLetra);
            tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            tvqtdeItems = (TextView) itemView.findViewById(R.id.tvItems);
            tvQtdeQuestion = (TextView) itemView.findViewById(R.id.tvQtde);
            ll = (LinearLayout) itemView.findViewById(R.id.buttonLayout);
            btQuestion = (Button) itemView.findViewById(R.id.question);
            btInventory = (Button) itemView.findViewById(R.id.inventory);
            imageArrow = (ImageView) itemView.findViewById(R.id.imageArrow);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemAmbiente.onItemClick(lista.get(getAdapterPosition()));
        }
    }

}

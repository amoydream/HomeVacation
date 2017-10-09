package pdasolucoes.com.br.homevacation.Adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.List;

import pdasolucoes.com.br.homevacation.CadastroItemActivity;
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

        holder.tvItem.setText(a.getDescricao());

        holder.tvqtdeItems.setText(String.format("%d", a.getItens()));

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.radioGroup.isShown()) {
                    holder.radioGroup.setVisibility(View.VISIBLE);
                    holder.imageArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                } else {
                    holder.radioGroup.setVisibility(View.GONE);
                    holder.imageArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);

                }
            }
        });


        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton r = (RadioButton) group.findViewById(checkedId);


                if (r.isChecked()) {
                    if (r.getText().toString().equals("Process")) {

                    } else {
                        Intent intent = new Intent(context, CadastroItemActivity.class);
                        intent.putExtra("ambiente", a);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
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

        public TextView tvLetra, tvItem, tvqtdeItems;
        public LinearLayout ll;
        public RadioGroup radioGroup;
        public ImageView imageArrow;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLetra = (TextView) itemView.findViewById(R.id.tvLetra);
            tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            tvqtdeItems = (TextView) itemView.findViewById(R.id.tvItems);
            ll = (LinearLayout) itemView.findViewById(R.id.buttonLayout);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radioGroup);
            imageArrow = (ImageView) itemView.findViewById(R.id.imageArrow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemAmbiente.onItemClick(lista.get(getAdapterPosition()));
        }
    }

}

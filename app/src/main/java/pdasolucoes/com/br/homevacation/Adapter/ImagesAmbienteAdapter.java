package pdasolucoes.com.br.homevacation.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import pdasolucoes.com.br.homevacation.Model.FotoAmbiente;
import pdasolucoes.com.br.homevacation.R;

/**
 * Created by PDA on 07/11/2017.
 */

public class ImagesAmbienteAdapter extends RecyclerView.Adapter<ImagesAmbienteAdapter.MyViewHolder> {
    private List<FotoAmbiente> lista;
    private Context context;
    private LayoutInflater layoutInflater;

    public ImagesAmbienteAdapter(List<FotoAmbiente> lista, Context context) {
        this.lista = lista;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ImagesAmbienteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_image, parent, false);

        MyViewHolder mv = new MyViewHolder(v);

        return mv;
    }

    @Override
    public void onBindViewHolder(ImagesAmbienteAdapter.MyViewHolder holder, int position) {


        holder.imageView.setImageBitmap(lista.get(position).getBitmap());

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}

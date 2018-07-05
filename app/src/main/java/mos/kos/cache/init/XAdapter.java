package mos.kos.cache.init;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月05日 18:06
 * @Email: KosmoSakura@foxmail.com
 */
public abstract class XAdapter<B, VH extends XHolder> extends RecyclerView.Adapter<XHolder> {
    private ArrayList<B> datas = null;

    public XAdapter(ArrayList<B> datas) {
        this.datas = datas;
    }

    protected abstract int layout();

//    protected abstract int logic(VH holder, int position);

    @NonNull
    @Override
    public XHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout(), parent, false);
        return new XHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull XHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

}

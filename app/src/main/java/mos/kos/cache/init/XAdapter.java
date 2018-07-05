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
 * @Event:
 */
public abstract class XAdapter extends RecyclerView.Adapter<XAdapter.XHolder> {
    public ArrayList datas = null;

    public XAdapter(ArrayList datas) {
        this.datas = datas;
    }

    protected abstract int layout();

//    protected abstract int logic(T holder, int position);

    @NonNull
    @Override
    public XAdapter.XHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new XHolder(LayoutInflater.from(parent.getContext()).inflate(layout(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull XHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    protected class XHolder extends RecyclerView.ViewHolder {

        protected XHolder(View view) {
            super(view);
        }
    }
}

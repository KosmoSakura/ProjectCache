package mos.kos.cache.logic;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.data.MainBean;
import mos.kos.cache.init.XAdapter;
import mos.kos.cache.init.XHolder;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月05日 18:12
 * @Email: KosmoSakura@foxmail.com
 */
public class MainAdapter extends XAdapter<MainBean, MainAdapter.AlphaHolder> {
    private ItemClickListener listener;

    public interface ItemClickListener {
        void onItemClick(MainBean bean);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public MainAdapter(ArrayList<MainBean> datas) {
        super(datas);
    }

    @NonNull
    @Override
    public AlphaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlphaHolder(creatView(parent, R.layout.item_main));
    }

    @Override
    protected void logic(AlphaHolder holder, int position) {
        holder.mTextView.setText(list.get(position).getName());

    }


    class AlphaHolder extends XHolder {
        TextView mTextView;

        AlphaHolder(View view) {
            super(view);

            mTextView = getView(R.id.item_main_text);
            getView(R.id.item_main_root).setOnClickListener(view1 -> {
                int position = getLayoutPosition() - 1;
                if (listener != null && position < list.size()) {
                    listener.onItemClick(list.get(position));
                }
            });
        }
    }
}

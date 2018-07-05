package mos.kos.cache.logic;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.data.AlphaBean;
import mos.kos.cache.init.XAdapter;
import mos.kos.cache.init.XHolder;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月05日 18:12
 * @Email: KosmoSakura@foxmail.com
 */
public class AlphaAdapter extends XAdapter<AlphaBean, AlphaAdapter.AlphaHolder> {

    public AlphaAdapter(ArrayList<AlphaBean> datas) {
        super(datas);
    }

    @Override
    protected int layout() {
        return R.layout.item_alpha;
    }

    @Override
    public void onBindViewHolder(@NonNull XHolder holder, int position) {

    }


    class AlphaHolder extends XHolder {
        AlphaHolder(View view) {
            super(view);
        }
    }
}

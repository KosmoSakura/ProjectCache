package mos.kos.cache.data;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月05日 21:16
 * @Email: KosmoSakura@foxmail.com
 */
public class MainBean {
    private int ids;
    private String name;

    public MainBean(String name, int ids) {
        this.ids = ids;
        this.name = name;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

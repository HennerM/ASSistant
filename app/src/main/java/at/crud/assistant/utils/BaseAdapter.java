package at.crud.assistant.utils;

import android.support.v7.widget.RecyclerView;
import java.util.List;


public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder, E> extends RecyclerView.Adapter<VH> {

    protected BaseViewHolder.OnItemClickListener clickListener;

    public BaseAdapter(List<E> list, BaseViewHolder.OnItemClickListener clickListener) {
        super();
    }


}

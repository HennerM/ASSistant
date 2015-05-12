package at.crud.assistant.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import at.crud.assistant.models.RecurringAction;


public abstract class BaseViewHolder<E> extends RecyclerView.ViewHolder {

    protected E element;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void bindObject(E element) {
        this.element = element;
    }

    public BaseViewHolder(View itemView, final BaseViewHolder.OnItemClickListener<E> clickListener) {
        this(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (element != null) {
                    clickListener.onClick(v, element);
                }
            }
        });
    }


    public interface OnItemClickListener<E> {
        public void onClick(View v,E element);
    }
}

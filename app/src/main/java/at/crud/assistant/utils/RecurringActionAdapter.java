package at.crud.assistant.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.crud.assistant.R;
import at.crud.assistant.models.RecurringAction;


public class RecurringActionAdapter extends BaseAdapter<RecurringActionAdapter.RecurringActionViewHolder, RecurringAction> {

    private List<RecurringAction> recurringActions;
    private BaseViewHolder.OnItemClickListener clickListener;

    public RecurringActionAdapter(List<RecurringAction> list, BaseViewHolder.OnItemClickListener clickListener) {
        super(list, clickListener);
        this.recurringActions = list;
        this.clickListener = clickListener;
    }

    @Override
    public RecurringActionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_action, viewGroup, false);
        return new RecurringActionViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(RecurringActionViewHolder holder, int i) {
        RecurringAction recurringAction = recurringActions.get(i);
        holder.bindObject(recurringAction);
    }

    @Override
    public int getItemCount() {
        return recurringActions.size();
    }

    public static class RecurringActionViewHolder extends BaseViewHolder<RecurringAction> {

        private TextView tvTitle;
        private RecurringAction recurringAction;

        public RecurringActionViewHolder(View itemView, OnItemClickListener clickListener) {
            super(itemView, clickListener);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
        }

        public void bindObject(RecurringAction recurringAction) {
            super.bindObject(recurringAction);
            tvTitle.setText(recurringAction.getTitle());
        }

    }
}

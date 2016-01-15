package shashank.mysms.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shashank.mysms.R;
import shashank.mysms.model.Sms;

/**
 * Created by shashankm on 15/01/16.
 */
public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHolder> {
    private List<Sms> allSmsList;

    public SmsAdapter(List<Sms> allSmsList){
        this.allSmsList = new ArrayList<>(allSmsList);
    }

    @Override
    public SmsAdapter.SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                    (R.layout.sms_card, parent, false);

        return new SmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SmsAdapter.SmsViewHolder holder, int position) {
        Sms sms = allSmsList.get(position);
        holder.time.setText(sms.getTime());
        holder.address.setText(sms.getAddress());
        holder.body.setText(sms.getBody());
        holder.contactPic.setImageResource(R.drawable.ic_default_image);
        boolean isSpam = sms.getisSpam();
        if (isSpam) {
            holder.isSpam.setImageResource(R.drawable.ic_spam_selected);
        } else {
            holder.isSpam.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return allSmsList.size();
    }

    public static class SmsViewHolder extends RecyclerView.ViewHolder{
        private TextView time, address, body;
        private ImageView contactPic, isSpam;

        public SmsViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.message_time);
            address = (TextView) itemView.findViewById(R.id.message_address);
            body = (TextView) itemView.findViewById(R.id.message_body);
            contactPic = (ImageView) itemView.findViewById(R.id.contact_pic);
            isSpam = (ImageView) itemView.findViewById(R.id.spam_icon);
        }
    }
}

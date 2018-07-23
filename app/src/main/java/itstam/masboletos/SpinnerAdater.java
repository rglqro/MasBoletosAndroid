package itstam.masboletos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class SpinnerAdater extends ArrayAdapter<String> {
    String[] spinnerTitles,DescFP;
    int[] spinnerImages;
    Context mContext;

    public SpinnerAdater(@NonNull Context context, String[] titles, int[] images, String[] DescFP) {
        super(context, R.layout.spinner_item3);
        this.spinnerTitles = titles;
        this.spinnerImages = images;
        this.DescFP=DescFP;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return spinnerTitles.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_item3, parent, false);
            mViewHolder.IMVFpago = (ImageView) convertView.findViewById(R.id.iconsp);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.txvsp);
            mViewHolder.TXVDescFP=(TextView)convertView.findViewById(R.id.txvdescFP);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.IMVFpago.setImageResource(spinnerImages[position]);
        mViewHolder.mName.setText(spinnerTitles[position]);
        mViewHolder.TXVDescFP.setText(DescFP[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        ImageView IMVFpago;
        TextView mName;
        TextView TXVDescFP;
    }
}

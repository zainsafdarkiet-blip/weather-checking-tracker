package com.example.weathertracker.adapters;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weathertracker.databinding.ItemCheckinBinding;
import com.example.weathertracker.entity.CheckInEntity;
import java.util.ArrayList;
import java.util.List;
public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.ViewHolder> {
    private List<CheckInEntity> checkInList = new ArrayList<>();
    private final OnItemDeleteListener deleteListener;
    public interface OnItemDeleteListener {
        void onDeleteClick(CheckInEntity checkIn, int position);
    }
    public CheckInAdapter(OnItemDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }
    public void setCheckInList(List<CheckInEntity> newList) {
        this.checkInList = newList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCheckinBinding binding = ItemCheckinBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckInEntity checkIn = checkInList.get(position);
        holder.bind(checkIn, deleteListener, position);
    }
    @Override
    public int getItemCount() {
        return checkInList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCheckinBinding binding;
        public ViewHolder(@NonNull ItemCheckinBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(CheckInEntity item, OnItemDeleteListener listener, int position) {
            binding.tvAddress.setText(item.getAddress());
            binding.tvCoordinates.setText(String.format("Lat: %.5f, Lon: %.5f", item.getLatitude(), item.getLongitude()));
            binding.tvDateTime.setText(item.getDateTime());
            binding.tvWeather.setText(String.format("%.1f°C | %s", item.getTemperature(), item.getWeatherDescription()));
            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item, position);
                }
            });
        }
    }
}

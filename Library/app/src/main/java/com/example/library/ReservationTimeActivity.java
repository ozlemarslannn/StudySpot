package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.library.room.RoomType;
import com.example.library.user.Role;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReservationTimeActivity extends BaseActivity {

    public static final String START_TIME = "startTime";
    public static final String ROOM_TYPE = "roomType";
    private Spinner roomTypeSpinner;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roomTypeSpinner = findViewById(R.id.roomTypeSpinner);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        btnShow = findViewById(R.id.btnShow);

        // Spinner değerleri kullanıcı rolüne göre (örnek)
        List<String> roomTypes = new ArrayList<>();
        fillRoomTypesBasedOnRole(roomTypes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomTypeSpinner.setAdapter(adapter);

        // DatePicker minDate = today
        datePicker.setMinDate(System.currentTimeMillis());

        // TimePicker ayarları
        timePicker.setIs24HourView(true);

        // Saat sadece saat başı olsun
        timePicker.setMinute(0);
        timePicker.setHour(timePicker.getHour()+1);
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            if (minute != 0) {
                timePicker.setMinute(0);
            }
        });

        // Kaydet butonu
        btnShow.setOnClickListener(v -> {
            Calendar selected = Calendar.getInstance();
            selected.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                    timePicker.getHour(), timePicker.getMinute(), 0);

            Calendar now = Calendar.getInstance();

            if (selected.before(now)) {
                Toast.makeText(this, "Geçmiş bir zaman seçilemez", Toast.LENGTH_LONG).show();
            } else {
                String selectedRoomType = roomTypeSpinner.getSelectedItem().toString();
                String selectedTime = String.format(Locale.getDefault(), "%02d:00", timePicker.getHour());
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                        datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());

                Toast.makeText(this, selectedRoomType + " - " + selectedDate + " " + selectedTime, Toast.LENGTH_LONG).show();

                // Burada öğrenciye özel oda listeleme sayfasına geçiş yapılabilir
                Intent intent = new Intent(ReservationTimeActivity.this, RoomListActivity.class);
                String selectedDateTime = selectedDate + " " + selectedTime + ":00"; // "yyyy-MM-dd HH:mm:ss" formatına tamamlanıyor
                intent.putExtra(START_TIME, selectedDateTime);
                intent.putExtra(ROOM_TYPE, selectedRoomType);
                startActivity(intent);

                // Buraya rezervasyon kaydetme işlemi yapılabilir
            }
        });
    }

    private void fillRoomTypesBasedOnRole(List<String> roomTypes) {
        Intent intent = getIntent();
        Role role = Role.valueOf(intent.getStringExtra(UserHomeActivity.ROLE));

        if (role.getRole().equalsIgnoreCase(Role.STUDENT.getRole())){
            roomTypes.add(RoomType.INDIVIDUAL.toString());
        }else if (role.getRole().equalsIgnoreCase(Role.ACADEMIC_STAFF.getRole())){

            roomTypes.add(RoomType.ACADEMIC.toString());
        }
        roomTypes.add(RoomType.GROUP.toString());
        roomTypes.add(RoomType.MEETING.toString());

    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reservation_time;
    }
}

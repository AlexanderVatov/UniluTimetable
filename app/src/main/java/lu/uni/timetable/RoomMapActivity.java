package lu.uni.timetable;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//import sysu.mobile.limk.library.indoormapview.MapView;

public class RoomMapActivity extends AppCompatActivity {
    String roomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommap);
        roomCode = getIntent().getStringExtra(EventIntent.room);
        System.err.println(roomCode);

        RoomMapView mapView = findViewById(R.id.mapView);
        try {
            mapView.initialize(roomCode);
            //BaseMapSymbol symbol = new LocationSymbol(getColor(R.color.colorPrimary),
            //        getColor(R.color.colorAccent),16);
            //mapView.setMapSymbols(Collections.singletonList(symbol));

            System.err.println("Successfully initialised mapView!");
        }
        catch(Exception ignored) {}
    }
}

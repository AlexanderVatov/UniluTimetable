package lu.uni.timetable;

import android.content.Context;
import android.util.AttributeSet;

import java.io.IOException;

import sysu.mobile.limk.library.indoormapview.Position;

/**
 * A View for an image map.
 * Extends {@link CustomMapView} with information on the Belval campus.
 */
public class RoomMapView extends CustomMapView {
    private Context c;

    public RoomMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        c = context;
    }

    public RoomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
    }

    public static Position locateRoomFloor2(int roomNumber) {
        switch(roomNumber) {
            case 130: return new Position(90, 463);
            case 140: return new Position(98, 372);
            case 150: return new Position(99, 305);
            case 160: return new Position(89, 215);
            case 540: return new Position(1612, 338);
            case 530: return new Position(1425, 338);
            case 500: return new Position(1038, 338);
            case 510: return new Position(732, 338);
            case 520: return new Position(299, 338);
            case 120: return new Position(172, 482);
            case 110: return new Position(304, 482);
            case 100: return new Position(427, 482);
            case 90:  return new Position(483, 483);
            case 80:  return new Position(515, 483);
            case 70:  return new Position(570, 483);
            case 60:  return new Position(639, 482);
            case 50:  return new Position(706, 482);
            case 40:  return new Position(818, 483);
            case 10:  return new Position(1044, 482);
            case 320: return new Position(1186, 493);
            case 310: return new Position(1212, 493);
            case 300: return new Position(1288, 494);
            case 330: return new Position(1411, 482);
            case 340: return new Position(1639, 483);
            case 350: return new Position(1733, 482);
            case 360: return new Position(1759, 339);
            case 370: return new Position(1732, 196);
            case 380: return new Position(1466, 196);
            case 390: return new Position(1373, 196);
            case 400: return new Position(1277, 196);
            case 240: return new Position(1062, 196);
            case 230: return new Position(907, 196);
            case 220: return new Position(797, 196);
            case 210: return new Position(653, 195);
            case 200: return new Position(554, 196);
            case 190: return new Position(443, 196);
            case 180: return new Position(280, 195);
            case 170: return new Position(173, 196);

            default: return null;
        }
    }

    public static Position locateRoomFloor3(int roomNumber) {
        switch(roomNumber) {
            case 500: return new Position(1024, 339);
            case 510: return new Position(761, 339);
            case 520: return new Position(299, 339);
            case 530: return new Position(1428, 339);
            case 540: return new Position(1610, 339);
            case 120: return new Position(115, 482);
            case 110: return new Position(309, 482);
            case 100: return new Position(444, 482);
            case 70:  return new Position(554, 482);
            case 50:  return new Position(653, 482);
            case 40:  return new Position(823, 480);
            case 10:  return new Position(1040, 480);
            case 330: return new Position(1413, 480);
            case 350: return new Position(1732, 481);
            case 370: return new Position(1732, 195);
            case 380: return new Position(1466, 196);
            case 390: return new Position(1371, 196);
            case 240: return new Position(1051, 196);
            case 230: return new Position(908, 196);
            case 220: return new Position(798, 195);
            case 210: return new Position(653, 195);
            case 200: return new Position(554, 195);
            case 190: return new Position(444, 195);
            case 180: return new Position(345, 195);
            case 170: return new Position(215, 195);
            case 160: return new Position(113, 195);

            default: return null;
        }
    }

    public static Position locateRoomFloor4(int roomNumber) {
        switch(roomNumber) {
            case 120: return new Position(88, 213);
            case 90:  return new Position(90, 450);
            case 370: return new Position(1702, 450);
            case 340: return new Position(1703, 213);
            case 570: return new Position(1504, 330);
            case 550: return new Position(1352, 364);
            case 560: return new Position(1352, 330);
            case 500: return new Position(1068, 332);
            case 510: return new Position(938, 332);
            case 520: return new Position(786, 331);
            case 530: return new Position(643, 331);
            case 540: return new Position(291, 331);
            case 110: return new Position(99, 298);
            case 100: return new Position(99, 365);
            case 80:  return new Position(168, 469);
            case 70:  return new Position(271, 469);
            case 60:  return new Position(429, 469);
            case 50:  return new Position(536, 469);
            case 40:  return new Position(632, 469);
            case 30:  return new Position(770, 469);
            case 20:  return new Position(877, 469);
            case 10:  return new Position(1027, 469);
            case 300: return new Position(1233, 469);
            case 310: return new Position(1325, 469);
            case 320: return new Position(1402, 469);
            case 380: return new Position(1631, 469);
            case 350: return new Position(1691, 364);
            case 360: return new Position(1692, 299);
            case 330: return new Position(1632, 194);
            case 390: return new Position(1415, 194);
            case 400: return new Position(1326, 194);
            case 410: return new Position(1234, 194);
            case 200: return new Position(1028, 194);
            case 190: return new Position(877, 194);
            case 180: return new Position(770, 194);
            case 170: return new Position(631, 194);
            case 160: return new Position(537, 194);
            case 150: return new Position(429, 194);
            case 140: return new Position(272, 195);
            case 130: return new Position(169, 195);

            default: return null;
        }

    }

    public static Position locateRoom(int floorNumber, int roomNumber) {
        switch (floorNumber) {
            case 2: return locateRoomFloor2(roomNumber);
            case 3: return locateRoomFloor3(roomNumber);
            case 4: return locateRoomFloor4(roomNumber);
            default: return null;
        }
    }

    public void initialize(String roomCode) {
        Position position;
        String[] parts;
        try {
            parts = roomCode.split("\\.");
            position = locateRoom(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        catch (Exception e) {
            System.err.println("No map is available for " + roomCode);
            System.err.println("(unable to parse room code)");
            return;
        }

        if(position == null) {
            System.err.println("No map is available for " + roomCode);
            System.err.println("(unrecognised room)");
            return;
        }

        try {
            initNewMap(c.getAssets().open("ms_floor" + parts[0] + ".png"), 1, 0, position);
        } catch (IOException ignored) {}

    }
}

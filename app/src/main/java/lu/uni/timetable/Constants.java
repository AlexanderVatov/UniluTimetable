package lu.uni.timetable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String GoogleMapsPackage = "com.google.android.apps.maps";
    public static final Map<String, String> buildingNames;
    public static final Map<String, String> buildingGoogleMapsUris;
    public static final Map<String, String> buildingFallbackMapUris;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("MSA", "Maison du Savoir (MSA)");
        map.put("MNO", "Maison du Nombre (MNO)");
        map.put("MSH", "Maison des Sciences Humaines (MSH)");
        buildingNames = Collections.unmodifiableMap(map);

        Map<String, String> map2 = new HashMap<>();
        map2.put("MSA", "geo:0,0?q=Maison+du+Savoir,+Esch-sur-Alzette,+Luxembourg");
        map2.put("MNO", "geo:49.5034978,5.9480768?q=Maison+du+Nombre,+Esch-sur-Alzette,+Luxembourg");
        map2.put("MSH", "geo:0,0?q=Maison+des+Sciences+Humaines,+Esch-sur-Alzette,+Luxembourg");
        buildingGoogleMapsUris = Collections.unmodifiableMap(map2);

        Map<String, String> map3 = new HashMap<>();
        map3.put("MSA", "geo:49.5041604,5.9488941");
        map3.put("MNO", "geo:49.5034978,5.9480768");
        map3.put("MSH", "geo:49.5043311,5.9448977");
        buildingFallbackMapUris = Collections.unmodifiableMap(map3);
    }


}

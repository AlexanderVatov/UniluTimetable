package lu.uni.avatov.guichetetudiant;

import java.util.AbstractMap;
import java.util.ArrayList;

public class ParametersMultimap extends ArrayList<ParametersMultimap.Entry> {
    public class Entry extends AbstractMap.SimpleEntry<String, String> {
        public Entry(String key, String value) {
            super(key, value);
        }
    }

    public void put(String key, String value) {
        add(new Entry(key, value));
    }

}

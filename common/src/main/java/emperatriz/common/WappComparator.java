package emperatriz.common;

import java.util.Comparator;

public class WappComparator implements Comparator<WappDto> {
    @Override
    public int compare(WappDto o1, WappDto o2) {
        return o1.name.compareTo(o2.name);
    }
}

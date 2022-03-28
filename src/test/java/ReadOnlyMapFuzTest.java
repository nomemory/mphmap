import net.andreinc.jperhash.ReadOnlyMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.andreinc.mockneat.unit.text.Strings.strings;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

public class ReadOnlyMapFuzTest {

    @Test
    public void testKeys() {
        final Map<String, String> map = new HashMap<>();
        names()
                .full().map(n -> ints().get() + " " + n)
                .set(10_000)
                .get()
                .forEach(s -> map.put(s, strings().size(1024).get()));
        final ReadOnlyMap<String, String> rom = ReadOnlyMap.snapshot(map);
        map.keySet().forEach(key -> {
            Assert.assertTrue(rom.get(key).equals(map.get(key)));
        });
    }

    @Test
    public void simpleTest() {
        final Map<String, String> map = new ConcurrentHashMap<>();
        for(int i = 0; i < 100_000; i++) {
            map.put(100+i+"", "abc"+i);
        }
        final ReadOnlyMap<String, String> rom = ReadOnlyMap.snapshot(
                map,
                String::getBytes,
                1.0,
                5,
                Integer.MAX_VALUE
        );
        for(int i = 0; i < 100_000; i++) {
            String key = 100+i+"";
            Assert.assertEquals(map.get(key), rom.get(key));;
        }
    }
}

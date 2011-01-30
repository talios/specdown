import com.theoryinpractise.specdown.SpecdownProcessor;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestSpec {

    @Test
    public void testSpec() throws IOException {
        new SpecdownProcessor("src/test/resources").processAll();
    }

}
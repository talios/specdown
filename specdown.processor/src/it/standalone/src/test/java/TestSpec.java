import org.testng.annotations.Test;
import com.theoryinpractise.specdown.SpecdownProcessor;
import java.io.IOException;

public class TestSpec {

    @Test
    public void testSpec() throws IOException {
        new SpecdownProcessor("src/test/resources").processAll();
    }

}
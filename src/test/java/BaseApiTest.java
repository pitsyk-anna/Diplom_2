
import static data.ApiEndpoints.BASE_URI;
import io.restassured.RestAssured;
import org.junit.Before;


public class BaseApiTest {
    @Before
    public void setUp() {

        RestAssured.baseURI = BASE_URI;
    }
}

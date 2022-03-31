import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import org.testng.annotations.Test;

import java.io.File;

public class Rest_assured {
        public static final String BASE_URI_GET = "https://jsonplaceholder.typicode.com";
        public static final String BASE_URI_PUT = "https://reqres.in/api";

        @Test(priority = 1)
        public void testGet_call(){
            given()
                    .baseUri(BASE_URI_GET)
                    .header("Content-Type","application/json")
                    .when()
                    .get("posts")
                    .then()
                    .statusCode(200)
                    .body(containsString("title"));
        }

        @Test(priority = 2)
        public void testPut_call(){

            File jsonData = new File("C:\\Users\\anuragkumar5\\Desktop\\Files\\HU_API_TRACK\\Assignment_Ans\\Rest_assurd_mini_Assignment_1\\src\\test\\resources\\Post.json");

            given()
                    .baseUri(BASE_URI_PUT)
                    .body(jsonData)
                    .header("Content-Type","application/json")
                    .when()
                    .put("/users")
                    .then()
                    .statusCode(200)
                    .body("name",equalTo("Arun"))
                    .body("job",equalTo("Manager"));

        }



    }

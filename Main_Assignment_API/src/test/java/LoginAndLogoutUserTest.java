import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class LoginAndLogoutUserTest {
    ResponseSpecification responseSpecificationLogin;
    RequestSpecification requestSpecificationLogin;
    ResponseSpecification responseSpecificationLogout;
    RequestSpecification requestSpecificationLogout;
    String pathOfRegisterUserDetails = "src/main/resources/registerUser.xlsx";

    @BeforeClass
    public void connect(){

//        login spec
        RequestSpecBuilder requestSpecBuilderLogin = new RequestSpecBuilder();
        requestSpecBuilderLogin.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type","application/json");
        requestSpecificationLogin = RestAssured.with().spec(requestSpecBuilderLogin.build());

        ResponseSpecBuilder specBuilderLogin = new ResponseSpecBuilder().
                expectStatusCode(200).expectContentType(ContentType.JSON);
        responseSpecificationLogin = specBuilderLogin.build();

//        logout spec
        RequestSpecBuilder requestSpecBuilderLogout = new RequestSpecBuilder();
        requestSpecBuilderLogout.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type","application/json");
        requestSpecificationLogout = RestAssured.with().spec(requestSpecBuilderLogin.build());

        ResponseSpecBuilder specBuilderLogout = new ResponseSpecBuilder().
                expectStatusCode(200).expectContentType(ContentType.JSON);
        responseSpecificationLogout = specBuilderLogout.build();
    }

    @Test
    public void loginAndLogout() throws IOException {

        FileInputStream fis = new FileInputStream(pathOfRegisterUserDetails);

        XSSFWorkbook wb = new XSSFWorkbook(fis);

        XSSFSheet sheet = wb.getSheetAt(0);
        Row row = null;
        Cell cell = null;
        String name = null;
        String email = null;
        String password = null;
        String _id = null;
        int age = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
//            System.out.println(row.getLastCellNum());
            for (int j = 0; j <= 4; j++) {
                cell = row.getCell(j);
                if (j == 0) {
                    name = cell.getStringCellValue();
                }
                if (j == 1) {
                    email = cell.getStringCellValue();
                }
                if (j == 2) {
                    password = cell.getStringCellValue();
                }
                if (j == 3) {
                    age =(int)cell.getNumericCellValue();
                }
                if(j == 4) {
                    _id = cell.getStringCellValue();
                }
            }
            JSONObject pushObject = new JSONObject();
            pushObject.put("email",email);
            pushObject.put("password",password);

            System.out.println("_______________________________________________________________________");
            System.out.println("Post call : reading the input from excel file ...");
            System.out.println("Email : "+email);
            System.out.println("Password : "+password);


            Response response = given().
                    spec(requestSpecificationLogin).
                    body(pushObject.toString()).
                    when().
                    post("/user/login").
                    then().extract().response();

            if(response.asString().equals("\"Unable to login\"")){
                System.out.println("________________________________________________________________");
                System.out.println("Email id or password is incorrect, please try again");
                System.out.println("----------------------------------------------------------------");
            }
            else{

                System.out.println("Logged In............with "+email);

                JSONObject responseToPost= new JSONObject(response.asString());
                //            System.out.println(responseToPost);
                JSONObject user = new JSONObject(responseToPost.get("user").toString());
                //            System.out.println("user"+user.toString());
                String token = responseToPost.get("token").toString();
                String id = user.get("_id").toString();

                Assert.assertEquals(user.get("name"),name,"The Name passed doesn't match the response");
                Assert.assertEquals(user.get("email"),email,"The email passed doesn't match the response");
                Assert.assertEquals(user.get("age"),age,"The age passed doesn't match the response");
                Assert.assertEquals(id,_id,"The ID of the user didn't match");


                response = given().
                        spec(requestSpecificationLogout).
                        header("Authorization","Bearer "+token).
                        when().
                        post("/user/logout").
                        then().extract().response();

                JSONObject logoutStat = new JSONObject(response.asString());

                if(logoutStat.has("success")){
                    Assert.assertEquals(logoutStat.get("success"),true,"Logout was unsuccessful");
                    System.out.println("Logged out successfully ..............");
                    System.out.println("_______________________________________________________________________");
                }
                else if(logoutStat.has("error")){
                    System.out.println(logoutStat.get("error").toString());
                }
            }
        }
        fis.close();
        wb.close();
    }

    @Test
    public void logoutWithoutToken() {
        Response response = given().
                spec(requestSpecificationLogout).
                when().
                post("/user/logout").
                then().extract().response();

        JSONObject err = new JSONObject(response.asString());
        Assert.assertEquals("Please authenticate.", err.get("error"));
    }
    @Test
    public void logoutWithWrongToken() {
        String token = "wrongToken";
        Response response = given().
                spec(requestSpecificationLogout).
                header("Authorization","Bearer "+token).
                when().
                post("/user/logout").
                then().extract().response();

        JSONObject err = new JSONObject(response.asString());
        Assert.assertEquals("Please authenticate.", err.get("error"));
    }

    @Test
    public void loginWithWrongCredentials() {
        String email = "notregistered";
        String password = "notapassword";
        JSONObject pushObject = new JSONObject();
        pushObject.put("email",email);
        pushObject.put("password",password);

        System.out.println("_______________________________________________________________________");
        System.out.println("Post call : reading the input from excel file ...");
        System.out.println("Email : "+email);
        System.out.println("Password : "+password);


        Response response = given().
                spec(requestSpecificationLogin).
                body(pushObject.toString()).
                when().
                post("/user/login").
                then().extract().response();

        Assert.assertEquals(response.asString(),"\"Unable to login\"","User logged in with wrong credentials!");
    }
}

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

public class GetAllTaskTest {

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    String pathOfRegisterUserDetails = "src/main/resources/registerUser.xlsx";

    @BeforeClass
    public void connect(){

        RequestSpecBuilder requestSpecBuilderLogin = new RequestSpecBuilder();
        requestSpecBuilderLogin.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type","application/json");
        requestSpecification = RestAssured.with().spec(requestSpecBuilderLogin.build());

        ResponseSpecBuilder specBuilderLogin = new ResponseSpecBuilder().
                expectStatusCode(200).expectContentType(ContentType.JSON);
        responseSpecification= specBuilderLogin.build();

    }


    @Test
    public void check20tasks() throws IOException {
        System.out.println("********************check20tasks********************");
        FileInputStream fis = new FileInputStream(pathOfRegisterUserDetails);

        XSSFWorkbook wb = new XSSFWorkbook(fis);

        XSSFSheet sheet = wb.getSheetAt(0);
        Row row = null;
        Cell cell = null;
        String email = null;
        String password = null;

//        int i = new Random().nextInt(sheet.getLastRowNum() - 1 + 1) + 1;
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        for (int i = 1; i <= 1; i++) {
            row = sheet.getRow(i);
            //            System.out.println(row.getLastCellNum());
            for (int j = 0; j <= 4; j++) {
                cell = row.getCell(j);
                if (j == 1) {
                    email = cell.getStringCellValue();
                }
                if (j == 2) {
                    password = cell.getStringCellValue();
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
                    spec(requestSpecification).
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


                //            *******************************************************
                //            Checking task
                //            *******************************************************



                response = given()
                        .spec(requestSpecification)
                        .header("Authorization","Bearer "+token)
                        .when()
                        .get("/task")
                        .then().extract().response();

                JSONObject allTaskResponse = new JSONObject(response.asString());
                Assert.assertEquals(allTaskResponse.get("count"),20, "20 tasks were not added ");



                response = given().
                        spec(requestSpecification).
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
    public void checkPagination2() throws IOException {
        System.out.println("********************Check Pagination for 2********************");
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

//        int i = new Random().nextInt(sheet.getLastRowNum() - 1 + 1) + 1;
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        for (int i = 1; i <= 1; i++) {
            row = sheet.getRow(i);
            //            System.out.println(row.getLastCellNum());
            for (int j = 0; j <= 4; j++) {
                cell = row.getCell(j);
                if (j == 1) {
                    email = cell.getStringCellValue();
                }
                if (j == 2) {
                    password = cell.getStringCellValue();
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
                    spec(requestSpecification).
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


                //            *******************************************************
                //            Checking task
                //            *******************************************************



                response = given()
                        .spec(requestSpecification)
                        .header("Authorization","Bearer "+token)
                        .when()
                        .get("/task?limit=2")
                        .then().extract().response();

                JSONObject allTaskResponse = new JSONObject(response.asString());
                Assert.assertEquals(allTaskResponse.get("count"),2, "Pagination (2) Failed ");



                response = given().
                        spec(requestSpecification).
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
    public void checkPagination5() throws IOException {
        System.out.println("********************Check Pagination for 5********************");
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

//        int i = new Random().nextInt(sheet.getLastRowNum() - 1 + 1) + 1;
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        for (int i = 1; i <= 1; i++) {
            row = sheet.getRow(i);
            //            System.out.println(row.getLastCellNum());
            for (int j = 0; j <= 4; j++) {
                cell = row.getCell(j);
                if (j == 1) {
                    email = cell.getStringCellValue();
                }
                if (j == 2) {
                    password = cell.getStringCellValue();
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
                    spec(requestSpecification).
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


                //            *******************************************************
                //            Checking task
                //            *******************************************************



                response = given()
                        .spec(requestSpecification)
                        .header("Authorization","Bearer "+token)
                        .when()
                        .get("/task?limit=5")
                        .then().extract().response();

                JSONObject allTaskResponse = new JSONObject(response.asString());
                Assert.assertEquals(allTaskResponse.get("count"),5, "Pagination (5) Failed ");



                response = given().
                        spec(requestSpecification).
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
    public void checkPagination10() throws IOException {
        System.out.println("********************Check Pagination for 10********************");
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

//        int i = new Random().nextInt(sheet.getLastRowNum() - 1 + 1) + 1;
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        for (int i = 1; i <= 1; i++) {
            row = sheet.getRow(i);
            //            System.out.println(row.getLastCellNum());
            for (int j = 0; j <= 4; j++) {
                cell = row.getCell(j);
                if (j == 1) {
                    email = cell.getStringCellValue();
                }
                if (j == 2) {
                    password = cell.getStringCellValue();
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
                    spec(requestSpecification).
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


                //            *******************************************************
                //            Checking task
                //            *******************************************************



                response = given()
                        .spec(requestSpecification)
                        .header("Authorization","Bearer "+token)
                        .when()
                        .get("/task?limit=10")
                        .then().extract().response();

                JSONObject allTaskResponse = new JSONObject(response.asString());
                Assert.assertEquals(allTaskResponse.get("count"),10, "Pagination (10) Failed ");



                response = given().
                        spec(requestSpecification).
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





}

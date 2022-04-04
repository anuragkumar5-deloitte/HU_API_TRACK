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

public class AddTaskTest {

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;


    String pathOfRegisterUserDetails = "src/main/resources/registerUser.xlsx";


    public String getTask(int ch){
        switch (ch){
            case 1: return ("working on hashedIn project");
            case 2: return ("reading newspaper");
            case 3: return ("listening to music");
            case 4: return ("sleeping");
            case 5: return ("working");
            case 6: return ("shopping");
            case 7: return ("studying");
            case 8: return ("fishing");
            case 9: return ("cooking");
            case 10: return ("attending Kunakidza");
            case 11: return ("attending tdg");
            case 12: return ("pushing code to git hub");
            case 13: return ("listening to Prem");
            case 14: return ("receiving breakfast from prem");
            case 15: return ("dancing");
            case 16: return ("writing");
            case 17: return ("reading book");
            case 18: return ("coding");
            case 19: return ("walking");
            case 20: return ("driving");
            default: return("Making aeroplane");
        }
    }

    @BeforeClass
    public void connect(){

//        login spec
        RequestSpecBuilder requestSpecBuilderLogin = new RequestSpecBuilder();
        requestSpecBuilderLogin.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type","application/json");
        requestSpecification = RestAssured.with().spec(requestSpecBuilderLogin.build());

        ResponseSpecBuilder specBuilderLogin = new ResponseSpecBuilder().
                expectStatusCode(200).expectContentType(ContentType.JSON);
        responseSpecification= specBuilderLogin.build();

    }

    @Test
    public void addTask() throws IOException {
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

                Assert.assertEquals(user.get("name"),name,"The Name passed doesn't match the response");
                Assert.assertEquals(user.get("email"),email,"The email passed doesn't match the response");
                Assert.assertEquals(user.get("age"),age,"The age passed doesn't match the response");
                Assert.assertEquals(id,_id,"The ID of the user didn't match");

    //            *******************************************************
    //            adding task
    //            *******************************************************


                JSONObject currentTask;
                for(int k = 1; k<=20;k++){
                    System.out.println("Adding task "+k);
                    currentTask = new JSONObject();
                    currentTask.put("description",getTask(k));
                    response = given()
                            .spec(requestSpecification)
                            .header("Authorization","Bearer "+token)
                            .body(currentTask.toString())
                            .when()
                            .post("/task")
                            .then().extract().response();

//                    check if the user id and the owner id are equal
                    JSONObject responseToPushTask = new JSONObject(response.asString());
                    JSONObject data =  new JSONObject(responseToPushTask.get("data").toString());

//                    to check if task was added successfully
                    Assert.assertEquals(responseToPushTask.get("success"),true,"The task wasn't added");
//                    to check if the owner of the task and the _id of the user match
                    Assert.assertEquals(data.get("owner"),_id);
//                    to check if the description in response matches the description sent
                    Assert.assertEquals(data.get("description"),getTask(k));
                }


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
    public void invalidBody() throws IOException {
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
                    age = (int) cell.getNumericCellValue();
                }
                if (j == 4) {
                    _id = cell.getStringCellValue();
                }
            }
            JSONObject pushObject = new JSONObject();
            pushObject.put("email", email);
            pushObject.put("password", password);

            System.out.println("_______________________________________________________________________");
            System.out.println("Post call : reading the input from excel file ...");
            System.out.println("Email : " + email);
            System.out.println("Password : " + password);


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
                //            adding task
                //            *******************************************************


                JSONObject currentTask;

                currentTask = new JSONObject();
                currentTask.put("description","");
                    response = given()
                            .spec(requestSpecification)
                            .header("Authorization","Bearer "+token)
                            .body(currentTask.toString())
                            .when()
                            .post("/task")
                            .then().extract().response();

                    Assert.assertEquals(response.asString(),"\"Task validation failed: description: Path `description` is required.\"");

                response = given().
                        spec(requestSpecification).
                        header("Authorization","Bearer "+token).
                        when().
                        post("/user/logout").
                        then().extract().response();

                JSONObject logoutStat = new JSONObject(response.asString());


            }
        fis.close();
        wb.close();
        }

        }
}
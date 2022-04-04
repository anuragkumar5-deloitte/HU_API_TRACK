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
import java.io.FileOutputStream;
import java.io.IOException;


import static io.restassured.RestAssured.given;

public class RegisterUserTest {
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    String pathOfRegisterUserDetails = "src/main/resources/registerUser.xlsx";

    @BeforeClass
    public void connect() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type","application/json");
        requestSpecification = RestAssured.with().spec(requestSpecBuilder.build());

        ResponseSpecBuilder specBuilder = new ResponseSpecBuilder().
                expectStatusCode(201).expectContentType(ContentType.JSON);
        responseSpecification = specBuilder.build();
    }

    @Test (priority = 1)
    public void registerUser() throws IOException {

        FileInputStream fis = new FileInputStream(pathOfRegisterUserDetails);

        XSSFWorkbook wb = new XSSFWorkbook(fis);

        XSSFSheet sheet = wb.getSheetAt(0);
        Row row = null;
        Cell cell = null;
        String name = null;
        String email = null;
        String password = null;
        int age = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
//            System.out.println(row.getLastCellNum());
            for (int j = 0; j <= 3; j++) {
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
            }
            JSONObject pushObject = new JSONObject();
            pushObject.put("name",name);
            pushObject.put("email",email);
            pushObject.put("password",password);
            pushObject.put("age",age);

            System.out.println("Post call : reading the input from excel file ...");
            System.out.println("Name : "+name);
            System.out.println("Email : "+email);
            System.out.println("Password : "+password);
            System.out.println("Age : "+age);


            Response response = given().
                    spec(requestSpecification).
                    body(pushObject.toString()).
                    when().
                    post("/user/register").
                    then().extract().response();

            if(response.asString().equals("\"E11000 duplicate key error collection: todo-list.users index: email_1 dup key: { email: \\\""+email+"\\\" }\"")){
                System.out.println("________________________________________________________________");
                System.out.println("Duplicate record");
                System.out.println("----------------------------------------------------------------");
            }
            else{

                System.out.println("Posted");
                System.out.println("Response - \n");
                System.out.println("\n");
                System.out.println(response.asString());
                JSONObject responseToPost= new JSONObject(response.asString());
    //            System.out.println(responseToPost);
                JSONObject user = new JSONObject(responseToPost.get("user").toString());
    //            System.out.println("user"+user.toString());
                String token = responseToPost.get("token").toString();
                String id = user.get("_id").toString();
                Assert.assertEquals(user.get("name"),name,"The Name passed doesn't match the response");
                Assert.assertEquals(user.get("email"),email,"The email passed doesn't match the response");
                Assert.assertEquals(user.get("age"),age,"The age passed doesn't match the response");



                sheet = wb.getSheetAt(0);
                FileOutputStream fos = new FileOutputStream(pathOfRegisterUserDetails);
                row = sheet.getRow(i);
                int idIndex = row.getLastCellNum();
                int tokenIndex = idIndex+1;

                cell = row.createCell(idIndex);
                cell.setCellValue(id);
                cell = row.createCell(tokenIndex);
                cell.setCellValue(token);
                wb.write(fos);

                fos.close();
            }
        }
            fis.close();
            wb.close();
    }

    @Test
    public void duplicateRegister() {
        String name = "Muhammad Nur Ali";
        String email = "muh.nurali43@gmail.com";
        String password = "12345678";
        int age = 20;

        JSONObject pushObject = new JSONObject();
        pushObject.put("name",name);
        pushObject.put("email",email);
        pushObject.put("password",password);
        pushObject.put("age",age);

        System.out.println("___________________________________________________");
        System.out.println("Post call : Registering with a duplicate email id :");
        System.out.println("Name : "+name);
        System.out.println("Email : "+email);
        System.out.println("Password : "+password);
        System.out.println("Age : "+age);


        Response response = given().
                spec(requestSpecification).
                body(pushObject.toString()).
                when().
                post("/user/register").
                then().extract().response();
        Assert.assertEquals(response.asString(),"\"E11000 duplicate key error collection: todo-list.users index: email_1 dup key: { email: \\\""+email+"\\\" }\"","The duplicate key error was not shown");
    }

    @Test
    public void blankName() {
        String name = "";
        String email = "muh.nuralifd43@gmail.com";
        String password = "12345678";
        int age = 20;

        JSONObject pushObject = new JSONObject();
        pushObject.put("name",name);
        pushObject.put("email",email);
        pushObject.put("password",password);
        pushObject.put("age",age);

        System.out.println("___________________________________________________");
        System.out.println("Post call : Registering with a duplicate email id :");
        System.out.println("Name : "+name);
        System.out.println("Email : "+email);
        System.out.println("Password : "+password);
        System.out.println("Age : "+age);


        Response response = given().
                spec(requestSpecification).
                body(pushObject.toString()).
                when().
                post("/user/register").
                then().extract().response();

        Assert.assertEquals(response.asString(),"\"User validation failed: name: Path `name` is required.\"","The Name field shouldn't be allowed to be left blank");

    }

    @Test
    public void shortPassword() {
        String name = "Muhammad Nur Ali";
        String email = "muh.nuralifd43@gmail.com";
        String password = "12";
        int age = 20;

        JSONObject pushObject = new JSONObject();
        pushObject.put("name",name);
        pushObject.put("email",email);
        pushObject.put("password",password);
        pushObject.put("age",age);

        System.out.println("___________________________________________________________________");
        System.out.println("Post call : Registering with a Password of less than 7 characters :");
        System.out.println("Name : "+name);
        System.out.println("Email : "+email);
        System.out.println("Password : "+password);
        System.out.println("Age : "+age);


        Response response = given().
                spec(requestSpecification).
                body(pushObject.toString()).
                when().
                post("/user/register").
                then().extract().response();

        Assert.assertEquals(response.asString(),"\"User validation failed: password: Path `password` (`"+password+"`) is shorter than the minimum allowed length (7).\"","The password field should be atleast contain 7 characters");

    }
    @Test
    public void negativeAge() {
        String name = "Muhammad Nur Ali";
        String email = "muh.nuralifd43@gmail.com";
        String password = "12345678";
        int age = -1;

        JSONObject pushObject = new JSONObject();
        pushObject.put("name",name);
        pushObject.put("email",email);
        pushObject.put("password",password);
        pushObject.put("age",age);

        System.out.println("_____________________________________________");
        System.out.println("Post call : Registering with a negative age :");
        System.out.println("Name : "+name);
        System.out.println("Email : "+email);
        System.out.println("Password : "+password);
        System.out.println("Age : "+age);


        Response response = given().
                spec(requestSpecification).
                body(pushObject.toString()).
                when().
                post("/user/register").
                then().extract().response();

        Assert.assertEquals(response.asString(),"\"User validation failed: age: Age must be a positive number\"","The negative age should not be accepted");

    }
}

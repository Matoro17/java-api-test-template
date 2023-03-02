import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiCEPTest {
    @Test
    void testCEPApi_WithStandartValue() {
        String CEP = "91060-900";
        Response response = getCEP(CEP);
        String responseBody = response.asString();

        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        String statusLine = response.statusLine();
        Assert.assertEquals(statusLine, "HTTP/1.1 200 OK");

        Gson gson = new Gson();
        Address address = gson.fromJson(responseBody, Address.class);

        Assert.assertEquals(address.cep, CEP);
        Assert.assertEquals(address.logradouro, "Avenida Assis Brasil");
        Assert.assertEquals(address.complemento, "3940");
        Assert.assertEquals(address.bairro, "São Sebastião");
        Assert.assertEquals(address.localidade, "Porto Alegre");
        Assert.assertEquals(address.uf, "RS");
        Assert.assertEquals(address.ibge, "4314902");
        Assert.assertEquals(address.gia, "");
        Assert.assertEquals(address.ddd, "51");
        Assert.assertEquals(address.siafi, "8801");
    }

    @Test
    void testCEPApi_WithNonValuableValue() {
        String CEP = "0000000";
        Response response = getCEP(CEP);

        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 400);
        String statusLine = response.statusLine();
        Assert.assertEquals(statusLine, "HTTP/1.1 400 Bad Request");

    }

    @Test
    void testCEPApi_WithNonExistentValue() {
        String CEP = "44000000";
        Response response = getCEP(CEP);
        String responseBody = response.asString();

        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        String statusLine = response.statusLine();
        Assert.assertEquals(statusLine, "HTTP/1.1 200 OK");

        Gson gson = new Gson();
        Address address = gson.fromJson(responseBody, Address.class);

        Assert.assertEquals(address.erro, "true");
    }

    @Test
    void testCEPApi_WithAddressValue() {
        String CEP = "RS/Gravatai/Barroso";
        Response response = getCEP(CEP);
        String responseBody = response.asString();

        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        String statusLine = response.statusLine();
        Assert.assertEquals(statusLine, "HTTP/1.1 200 OK");

        Gson gson = new Gson();
        JsonArray a = gson.fromJson(responseBody, JsonArray.class);
        Address address = gson.fromJson(a.get(0), Address.class);

        Assert.assertEquals(address.cep, "94085-170");
        Assert.assertEquals(address.logradouro, "Rua Ari Barroso");
        Assert.assertEquals(address.complemento, "");
        Assert.assertEquals(address.bairro, "Morada do Vale I");
        Assert.assertEquals(address.localidade, "Gravataí");
        Assert.assertEquals(address.uf, "RS");
        Assert.assertEquals(address.ibge, "4309209");
        Assert.assertEquals(address.gia, "");
        Assert.assertEquals(address.ddd, "51");
        Assert.assertEquals(address.siafi, "8683");
    }

    Response getCEP(String CEP) {
        RestAssured.baseURI = "https://viacep.com.br/ws/" + CEP + "/json/";
        RequestSpecification httpResquest = RestAssured.given();
        return httpResquest.request(Method.GET, "");
    }
}

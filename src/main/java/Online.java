import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;

import static io.restassured.RestAssured.given;

public class Online {

    @Test
    public void getResp(){
        Response response = given().
                param("external_id","295").
                when().
                get("http://epg.megogo.net/channel/now");

        System.out.println(response.asString());
    }

 //   @Test
    public void getProgramOnline() {

        String s = given().param("external_id", "295").when().get("http://epg.megogo.net/channel/now").thenReturn().asString();
        System.out.println(s);

        JsonPath json = new JsonPath(s);
        String programName = json.getString("data[0].programs[0].title");
        String year = json.getString("data[0].programs[0].year");
        String programStartTime = json.getString("data[0].programs[0].start");
        String programEndTime = json.getString("data[0].programs[0].end");
        String genre_id = json.getString("data[0].programs[0].genre.id");
        System.out.println("programName "+programName +" "+year+ " "+ programStartTime+ " "+ programEndTime + " "+ genre_id);

    }

    @Test
    public void liveProgramComparisonTest() throws ParserConfigurationException, IOException, SAXException, ParseException {

        String date = Helper.getCurrentDate();
        String title;
        String production_year;

        String s = given().param("external_id", "295").when().get("http://epg.megogo.net/channel/now").thenReturn().asString();
     //   System.out.println(s);

        JsonPath json = new JsonPath(s);
        String programName = json.getString("data[0].programs[0].title");
        String programProdYear = json.getString("data[0].programs[0].year");
        String programStartTime = json.getString("data[0].programs[0].start");
        String programEndTime = json.getString("data[0].programs[0].end");
        String programGenreId = json.getString("data[0].programs[0].genre.id");
        System.out.println("Live Program Info \nProgram title: " + programName + "\nProgram start time: " + programStartTime +
                "\nProgram end time: " + programEndTime + "\nProgram genre id: " + programGenreId +
                "\nProgram prod.year: " + programProdYear);


        Response response = given().
                when().
                get("http://www.vsetv.com/export/megogo/epg/3.xml");
//        XmlPath xml = new XmlPath(response.asString()).using(new XmlPathConfig().disableLoadingOfExternalDtd());

        String resp = response.asString();
        resp = resp.trim().substring(71, resp.length());

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(resp));

        Document document = documentBuilder.parse(is);

        NodeList programmeList = document.getElementsByTagName("programme");
        for (int i=0; i< programmeList.getLength(); i++){
            Node p = programmeList.item(i);
            if(p.getNodeType() ==Node.ELEMENT_NODE){
                Element program = (Element) p;

                if(date.compareTo(program.getAttribute("start")) >= 0 &&
                        date.compareTo(program.getAttribute("stop")) < 0){

                String start = program.getAttribute("start");
                String end = program.getAttribute("stop");
                String genreId = program.getAttribute("genre_id");

                Assert.assertTrue(date.compareTo(start) >= 0 && date.compareTo(end) < 0);
                    if(date.compareTo(start) >= 0 && date.compareTo(end) < 0) {

                        System.out.println("ProgramStartTime comparison");
                        Assert.assertEquals(Helper.convertToNewFormat(programStartTime), start);

                        System.out.println("ProgramEndTime comparison");
                        Assert.assertEquals(Helper.convertToNewFormat(programEndTime), end);

                        System.out.println("ProgramGenreId comparison");
                        if (programGenreId != null && genreId != "") {
                            Assert.assertEquals(programGenreId, genreId);
                        }else System.out.println("Genre ID is missing");

                        NodeList programChildNodeList = program.getChildNodes();
                        for (int j = 0; j < programChildNodeList.getLength(); j++) {
                            Node child = programChildNodeList.item(j);
                            if (child.getNodeName().compareTo("title") == 0) {
                                title = child.getTextContent();
                                System.out.println("ProgramTitle comparison");
                                Assert.assertEquals(programName, title);
                            }else if (child.getNodeName().compareTo("production_year") == 0){
                                production_year = child.getTextContent();
                                if( production_year != null && programProdYear != "") {
                                    System.out.println("ProgramProdYear comparison");
                                    Assert.assertEquals(programProdYear, production_year);
                                }System.out.println("Program prod year is missing");
                            }
                        }
                    } else System.out.println("Can't find time matches");
                }
            }
        }
    }
}
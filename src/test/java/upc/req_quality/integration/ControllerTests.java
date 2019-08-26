package upc.req_quality.integration;

import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import upc.req_quality.db.SQLiteDAO;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    int port = 9409;

    private String path = "./testing/integration/";

    @Before
    public void createTestDB() throws Exception {
        SQLiteDAO.setPath("./testing/test_database/");
        SQLiteDAO db = new SQLiteDAO();
        db.clearDatabase();
    }

    @After
    public void deleteTestDB() throws Exception {
        SQLiteDAO db = new SQLiteDAO();
        db.clearDatabase();
        db.deleteDataFiles("db");
    }


    /*
    Main operations
     */

    @Test
    public void InTemplates() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"InTemplates/input_InTemplates_simple.json")))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void InTemplatesCycle() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"InTemplates/input_InTemplates_cycle.json")))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string(read_file_raw(path+"InTemplates/output_InTemplates_cycle.json")));
    }

    @Test
    public void InTemplatesPKOk() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"InTemplates/input_InTemplates_simple.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"InTemplates/input_InTemplates_simple.json")))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void OutTemplates() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"OutTemplates/input_template_1.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"OutTemplates/input_template_2.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"OutTemplates/input_template_3.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=Test"))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file_raw(path+"OutTemplates/output_OutTemplates_simple.json")));
    }

    @Test
    public void OutTemplatesNotExists() throws Exception {
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=Test"))
                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(read_file_raw(path+"OutTemplates/output_OutTemplates_notfound.json")));
    }

    @Test
    public void ConformanceOK() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"Conformance/input_model_rupp.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/Conformance").param("organization", "Test").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"Conformance/input_conformance_OK_simple.json")))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file(path+"Conformance/output_conformance_Ok_simple.json")));
    }

    @Test
    public void ConformanceBadInput() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"Conformance/input_model_rupp.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/Conformance").param("organization", "Test").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"Conformance/input_conformance_badInput_1.json")))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string(read_file_raw(path+"Conformance/output_conformance_badInput_1.json")));
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/Conformance").param("organization", "Test").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"Conformance/input_conformance_badInput_2.json")))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string(read_file_raw(path+"Conformance/output_conformance_badInput_2.json")));
    }

    @Test
    public void ConformanceNOTOK() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"Conformance/input_model_rupp.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/Conformance").param("organization", "Test").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"Conformance/input_conformance_NOTOK_simple.json")))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file_raw(path+"Conformance/output_conformance_NOTOk_simple.json")));
    }


    /*
    Auxiliary operations
     */

    @Test
    public void deleteOrganizationTemplates() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"ClearOrganizationTemplates/input_template_1.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"ClearOrganizationTemplates/input_template_2.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"ClearOrganizationTemplates/input_template_3.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(delete("/upc/reqquality/check-conformance-to-templates/DeleteOrganizationTemplates?organization=Test1"))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=Test1"))
                .andDo(print()).andExpect(status().isNotFound());
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=Test2"))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file_raw(path+"ClearOrganizationTemplates/output_test2.json")));
    }

    @Test
    public void DeleteOrganizationTemplatesNotExists() throws Exception {
        this.mockMvc.perform(delete("/upc/reqquality/check-conformance-to-templates/DeleteOrganizationTemplates?organization=Test"))
                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(read_file_raw(path+"ClearOrganizationTemplates/output_notfound.json")));
    }

    @Test
    public void ClearDatabase() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"ClearDatabase/input_template_1.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"ClearDatabase/input_template_2.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"ClearDatabase/input_template_3.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(delete("/upc/reqquality/check-conformance-to-templates/ClearDatabase"))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=Test1"))
                .andDo(print()).andExpect(status().isNotFound());
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=Test2"))
                .andDo(print()).andExpect(status().isNotFound());
    }






















    /*
    auxiliary methods
     */

    private String read_file(String path) throws Exception {
        try(FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String result = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result = result.concat(line);
            }
            JSONObject aux = new JSONObject(result);
            return aux.toString();
        }
    }

    private String read_file_raw(String path) throws Exception {
        try(FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String result = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result = result.concat(line);
            }
            bufferedReader.close();
            return result;
        }
    }


}
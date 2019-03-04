package upc.req_quality.test;

import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerIntegration {

    @Autowired
    private MockMvc mockMvc;

    private String path = "./testing/integration/";

    /*
    Simple endpoints
     */

    @Test
    public void aAuxiliary() throws Exception {
        this.mockMvc.perform(delete("/upc/reqquality/check-conformance-to-templates/DeleteTemplates?organization=_---Test---_"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void bTestInTemplates() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"input_InTemplates_simple.json")))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void cTestOutTemplates() throws Exception {
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=_---Test---_"))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file_raw(path+"output_outTemplates_simple.json")));
    }

    @Test
    public void dTestDeleteTemplates() throws Exception {
        this.mockMvc.perform(delete("/upc/reqquality/check-conformance-to-templates/DeleteTemplates?organization=_---Test---_"))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/upc/reqquality/check-conformance-to-templates/OutTemplates?organization=_---Test---_"))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file(path+"output_deleteTemplates_simple.json")));
    }

    @Test
    public void eTestConformanceOK() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"templates/input_model_rupp.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/Conformance").param("organization", "_---Test---_").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"input_conformance_OK_simple.json")))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file(path+"output_conformance_Ok_simple.json")));
        this.mockMvc.perform(delete("/upc/reqquality/check-conformance-to-templates/DeleteTemplates?organization=_---Test---_"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void fTestConformanceNOTOK() throws Exception {
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/InTemplates").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"templates/input_model_rupp.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/upc/reqquality/check-conformance-to-templates/Conformance").param("organization", "_---Test---_").contentType(MediaType.APPLICATION_JSON).content(read_file(path+"input_conformance_NOTOK_simple.json")))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(read_file_raw(path+"output_conformance_NOTOk_simple.json")));
        this.mockMvc.perform(delete("/upc/reqquality/check-conformance-to-templates/DeleteTemplates?organization=_---Test---_"))
                .andDo(print()).andExpect(status().isOk());
    }



    /*
    Exceptions
     */






















    /*
    auxiliary methods
     */

    private String read_file(String path) throws Exception {
        String result = "";
        String line = "";
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                result = result.concat(line);
            }
            bufferedReader.close();
            JSONObject aux = new JSONObject(result);
            return aux.toString();
        } finally {
            if (fileReader != null) fileReader.close();
            if (bufferedReader != null) bufferedReader.close();
        }
    }

    private String read_file_raw(String path) throws Exception {
        String result = "";
        String line = "";
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                result = result.concat(line);
            }
            bufferedReader.close();
            return result;
        } finally {
            if (fileReader != null) fileReader.close();
            if (bufferedReader != null) bufferedReader.close();
        }
    }


}
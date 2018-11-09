import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.adapter.Generic_template;
import upc.req_quality.adapter.Rupp_teplate_java;

public class DriverGenericTemplate {
    public static void main(String[] args) {
        AdapterTemplate adapterTemplate = new Rupp_teplate_java();
        boolean meh = adapterTemplate.check_template(null,null);
        System.out.println(meh);
    }
}

import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.adapter.Generic_template;

public class DriverGenericTemplate {
    public static void main(String[] args) {
        AdapterTemplate adapterTemplate = new Generic_template();
        boolean meh = adapterTemplate.check_template(null,null);
        System.out.println(meh);
    }
}

package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;
import java.util.regex.*;
import java.util.List;

public class Rupp_teplate_java implements AdapterTemplate {

    @Override
    public boolean check_template(List<SpanOut> spans, List<String> words) {
        String text = "NP NP VP xd";

        String patternString = "(NP )+VP (meh|xd)";

        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(text);
        boolean matches = matcher.matches();
        return matches;
    }
}

package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;

import java.util.List;

public class Rupp_template implements AdapterTemplate {

    private static int options = 0;

    @Override
    public boolean check_template(List<SpanOut> spans, List<String> words) {

        if (check_opt1(spans, words) || check_opt2(spans, words) || check_opt3()) return true;
        else return false;
    }

    private boolean check_opt1(List<SpanOut> spans, List<String> words) {
        boolean ok = false;
        if (spans.size() >= 4) {
            String opt_condition = words.get(0);
            List<String> vp_with_modal = words.subList(spans.get(1).getStart(), spans.get(1).getEnd());
            List<String> opt_details = words.subList(spans.get(3).getStart(), spans.get(3).getEnd()); //hasta el final de la frase no el final del span
            if ((check_opt_condition(opt_condition)) && (spans.get(0).getValue().equals("NP")) && (spans.get(1).getValue().equals("VP")) && (check_vp_with_modal(vp_with_modal)) &&
                    (spans.get(2).getValue().equals("NP")) && check_opt_details(opt_details)) {
                ok = true;
            }
            /*if (check_opt_condition(opt_condition)) System.out.println("b0");
            if (spans.get(0).getValue().equals("NP")) System.out.println("b1");
            if (spans.get(1).getValue().equals("VP")) System.out.println("b2");
            if (check_vp_with_modal(vp_with_modal)) System.out.println("b3");
            if (spans.get(2).getValue().equals("NP")) System.out.println("b4");
            if (check_opt_details(opt_details)) System.out.println("b5");*/
        }
        System.out.println("OPT1: "+ ok);
        return ok;
    }
    private boolean check_opt2(List<SpanOut> spans, List<String>words) {
        boolean ok = false;
        if (spans.size() >= 3) {
            String opt_condition = words.get(0);
            List<String> modal_exists = words.subList(spans.get(0).getStart(), spans.get(0).getEnd());
            List<String> provide = modal_exists;
            List<String> ability = words.subList(spans.get(2).getStart(), spans.get(2).getEnd());
            List<String> opt_details = words.subList(spans.get(2).getStart(), spans.get(2).getEnd()); //hasta el final de la frase no el final del span
            if ((check_opt_condition(opt_condition)) && (spans.get(0).getValue().equals("NP")) && (spans.get(1).getValue().equals("NP")) && (check_modal_exists(modal_exists)) &&
                    (check_provide_ability(provide)) && (check_ability(ability)) && (spans.get(3).getValue().equals("VP")) && check_opt_details(opt_details)) {
                ok = true;
            }
        }
        System.out.println("OPT2: "+ ok);
        return ok;
    }

    private boolean check_opt3() {
        return false;
    }

    private boolean check_opt_condition(String condition) {
        return true;
    }

    private boolean check_vp_with_modal(List<String> vp) {
        return true;
    }

    private boolean check_opt_details(List<String> details) {
        return true;
    }

    private boolean check_modal_exists(List<String> words) {
        return true;
    }

    private boolean check_provide_ability(List<String> words) {
        return true;
    }

    private boolean check_ability(List<String> words) {
        return true;
    }
}

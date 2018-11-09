package upc.req_quality.entity;

public class SpanOut {

    private int start;
    private int end;
    private String value;

    public SpanOut(int start, int end, String value) {
        this.start = start;
        this.end = end;
        this.value = value;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Span{" +
                "start/end= " + start + "/"+ end + "  value= " + value +"}";
    }
}

package co.celloscope.billingsystemdemo;


import java.util.Vector;
import java.util.ListIterator;
import java.net.URLEncoder;

public class Form {

    private String no;
    private String name;
    private String submitTo;
    public Vector<Field> fields;


    public Form()
    {
        this.fields = new Vector<Field>();
        no = "";
        name = "";
        submitTo = "loopback"; // do nothing but display the results
    }
    // getters & setters
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSubmitTo() {
        return submitTo;
    }

    public void setSubmitTo(String submitTo) {
        this.submitTo = submitTo;
    }

    public Vector<Field> getFields() {
        return fields;
    }

    public void setFields(Vector<Field> fields) {
        this.fields = fields;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Form:\n");
        sb.append("Form Number: " + this.no + "\n");
        sb.append("Form Name: " + this.name + "\n");
        sb.append("Submit To: " + this.submitTo + "\n");
        if (this.fields == null) return sb.toString();
        ListIterator<Field> li = this.fields.listIterator();
        while (li.hasNext()) {
            sb.append(li.next().toString());
        }

        return sb.toString();
    }

    public String getFormattedResults()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Results:\n");
        if (this.fields == null) return sb.toString();
        ListIterator<Field> li = this.fields.listIterator();
        while (li.hasNext()) {
            sb.append(li.next().getFormattedResult() + "\n");
        }

        return sb.toString();
    }

    public String getFormEncodedData()
    {
        try {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            sb.append("Results:\n");
            if (this.fields == null) return sb.toString();
            ListIterator<Field> li = this.fields.listIterator();
            while (li.hasNext()) {
                if (i != 0) sb.append("&");
                Field thisField = li.next();
                sb.append(thisField.name + "=");
                String encstring = new String();
                URLEncoder.encode((String) thisField.getData(),encstring);
                sb.append(encstring);
            }

            return sb.toString();
        }
        catch (Exception e) {
            return "ErrorEncoding";
        }
    }
}
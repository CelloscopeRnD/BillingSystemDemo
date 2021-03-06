package co.celloscope.billingsystemdemo;

/**
 * Represent a form field
 */
public class Field {
    String name;
    String label;
    String type;
    boolean required;
    String options;
    Object obj;   // holds the ui implementation

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public String getOptions() {
        return options;
    }
    public void setOptions(String options) {
        this.options = options;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Field Name: " + this.name + "\n");
        sb.append("Field Label: " + this.label + "\n");
        sb.append("Field Type: " + this.type + "\n");
        sb.append("Required? : " + this.required + "\n");
        sb.append("Options : " + this.options + "\n");
        sb.append("Value : " + (String) this.getData() + "\n");

        return sb.toString();
    }
    public String getFormattedResult()
    {
        return this.name + "= [" + (String) this.getData() + "]";

    }

    public Object getData()
    {
        if (type.equals("text") || type.equals("numeric"))
        {
            if (obj != null) {
                EditBox b = (EditBox) obj;
                return b.getValue();
            }
        }
        if (type.equals("choice")) {
            if (obj != null) {
                PickOne po = (PickOne) obj;
                return po.getValue();
            }
        }

        // You could add logic for other UI elements here
        return null;
    }

}
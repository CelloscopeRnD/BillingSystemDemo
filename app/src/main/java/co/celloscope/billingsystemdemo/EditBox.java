package co.celloscope.billingsystemdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.text.method.DigitsKeyListener;

/**
 * Represents a set of TextView and EditText in a LinearLayout
 */
public class EditBox extends LinearLayout {
    TextView textView;
    EditText editText;

    public EditBox(Context context, String labelText, String initialText) {
        super(context);
        textView = new TextView(context);
        textView.setText(labelText);
        editText = new EditText(context);
        editText.setText(initialText);
        editText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(textView);
        this.addView(editText);
    }

    public EditBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void makeNumeric() {
        editText.setKeyListener(new DigitsKeyListener(true, true));
    }

    public String getValue() {
        return editText.getText().toString();
    }

    public void setValue(String v) {
        editText.setText(v);
    }
}
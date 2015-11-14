package co.celloscope.billingsystemdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;

public class FormGenerator extends Activity {
    String Tag = FormGenerator.class.getName();
    Form theForm;
    ProgressDialog progressDialog;
    Handler progressHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String formNumber = "";
        Intent mainIntent = getIntent();
        if (mainIntent == null) {
            Log.e(Tag, "No Intent?  We're not supposed to be here...");
            finish();
            return;
        }
        formNumber = mainIntent.getStringExtra("formNumber");
        Log.i(Tag, "Running Form [" + formNumber + "]");
        if (GetFormData(formNumber)) {
            DisplayForm();
        } else {
            Log.e(Tag, "Couldn't parse the Form.");
            AlertDialog.Builder bd = new AlertDialog.Builder(this);
            AlertDialog ad = bd.create();
            ad.setTitle("Error");
            ad.setMessage("Could not parse the Form data");
            ad.show();
        }
    }

    /**
     * Get XML metadata which represents a form
     *
     * @param formNumber form number
     * @return
     */
    private boolean GetFormData(String formNumber) {
        try {
//            URL url = new URL("http://www.w3schools.com/xml/note.xml");
//            InputStream is = url.openConnection().getInputStream();
            Element root = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(getAssets().open("xmlgui1.xml"))
                    .getDocumentElement();

            NodeList forms = root.getElementsByTagName("form");
            if (forms.getLength() < 1) {
                // nothing here??
                Log.e(Tag, "No form, let's bail");
                return false;
            } else {
                Node form = forms.item(0);
                theForm = new Form();

                // process form level
                NamedNodeMap map = form.getAttributes();
                theForm.setNo(map.getNamedItem("id").getNodeValue());
                theForm.setName(map.getNamedItem("name").getNodeValue());
                if (map.getNamedItem("submitTo") != null)
                    theForm.setSubmitTo(map.getNamedItem("submitTo").getNodeValue());
                else
                    theForm.setSubmitTo("loopback");

                // now process the fields
                NodeList fields = root.getElementsByTagName("field");
                for (int i = 0; i < fields.getLength(); i++) {
                    Node fieldNode = fields.item(i);
                    NamedNodeMap attr = fieldNode.getAttributes();
                    Field tempField = new Field();
                    tempField.setName(attr.getNamedItem("name").getNodeValue());
                    tempField.setLabel(attr.getNamedItem("label").getNodeValue());
                    tempField.setType(attr.getNamedItem("type").getNodeValue());
                    if (attr.getNamedItem("required").getNodeValue().equals("Y"))
                        tempField.setRequired(true);
                    else
                        tempField.setRequired(false);
                    tempField.setOptions(attr.getNamedItem("options").getNodeValue());
                    theForm.getFields().add(tempField);
                }

                Log.i(Tag, theForm.toString());
                return true;
            }

        } catch (Exception e) {
            Log.e(Tag, "Error occurred in ProcessForm:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean DisplayForm() {

        try {
            ScrollView sv = new ScrollView(this);

            final LinearLayout ll = new LinearLayout(this);
            sv.addView(ll);
            ll.setOrientation(android.widget.LinearLayout.VERTICAL);

            // walk through the form elements and dynamically create them,
            // leveraging the mini library of tools.
            int i;
            for (i = 0; i < theForm.fields.size(); i++) {
                if (theForm.fields.elementAt(i).getType().equals("text")) {
                    theForm.fields.elementAt(i).obj = new
                            XmlGuiEditBox(this, (theForm.fields.elementAt(i).isRequired()
                            ? "*" : "") + theForm.fields.elementAt(i).getLabel(), "");
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
                if (theForm.fields.elementAt(i).getType().equals("numeric")) {
                    theForm.fields.elementAt(i).obj = new
                            XmlGuiEditBox(this, (theForm.fields.elementAt(i).isRequired()
                            ? "*" : "") + theForm.fields.elementAt(i).getLabel(), "");
                    ((XmlGuiEditBox) theForm.fields.elementAt(i).obj).makeNumeric();
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
                if (theForm.fields.elementAt(i).getType().equals("choice")) {
                    theForm.fields.elementAt(i).obj = new
                            XmlGuiPickOne(this, (theForm.fields.elementAt(i).isRequired()
                            ? "*" : "") + theForm.fields.elementAt(i).getLabel(),
                            theForm.fields.elementAt(i).getOptions());
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
            }


            Button btn = new Button(this);
            // cha nge import for layoutparams if error
            btn.setLayoutParams(new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.
                            WRAP_CONTENT));

            ll.addView(btn);

            btn.setText("Submit");
            btn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    // check if this form is Valid
                    if (!CheckForm()) {
                        AlertDialog.Builder bd = new AlertDialog.Builder(ll.getContext());
                        AlertDialog ad = bd.create();
                        ad.setTitle("Error");
                        ad.setMessage("Please enter all required (*) fields");
                        ad.show();
                        return;

                    }
                    if (theForm.getSubmitTo().equals("loopback")) {
                        // just display the results to the screen
                        String formResults = theForm.getFormattedResults();
                        Log.i(Tag, formResults);
                        AlertDialog.Builder bd = new AlertDialog.Builder(ll.getContext());
                        AlertDialog ad = bd.create();
                        ad.setTitle("Results");
                        ad.setMessage(formResults);
                        ad.show();
                        return;

                    } else {
                        if (!SubmitForm()) {
                            AlertDialog.Builder bd = new AlertDialog.Builder(ll.getContext());
                            AlertDialog ad = bd.create();
                            ad.setTitle("Error");
                            ad.setMessage("Error submitting form");
                            ad.show();
                            return;
                        }
                    }

                }
            });

            setContentView(sv);
            setTitle(theForm.getName());

            return true;

        } catch (Exception e) {
            Log.e(Tag, "Error Displaying Form");
            return false;
        }
    }

    private boolean CheckForm() {
        try {
            int i;
            boolean good = true;


            for (i = 0; i < theForm.fields.size(); i++) {
                String fieldValue = (String)
                        theForm.fields.elementAt(i).getData();
                Log.i(Tag, theForm.fields.elementAt(i)
                        .getName() + " is [" + fieldValue + "]");
                if (theForm.fields.elementAt(i).isRequired()) {
                    if (fieldValue == null) {
                        good = false;
                    } else {
                        if (fieldValue.trim().length() == 0) {
                            good = false;
                        }
                    }

                }
            }
            return good;
        } catch (Exception e) {
            Log.e(Tag, "Error in CheckForm()::" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean SubmitForm() {
        try {
            boolean ok = true;
            this.progressDialog = ProgressDialog.show(this,
                    theForm.getName(), "Saving Form Data", true, false);
            this.progressHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    // process incoming messages here
                    switch (msg.what) {
                        case 0:
                            // update progress bar
                            progressDialog.setMessage("" + (String) msg.obj);
                            break;
                        case 1:
                            progressDialog.cancel();
                            finish();
                            break;
                        case 2:
                            progressDialog.cancel();
                            break;
                    }
                    super.handleMessage(msg);
                }

            };

            Thread workthread = new Thread(new TransmitFormData(theForm));

            workthread.start();

            return ok;
        } catch (Exception e) {
            Log.e(Tag, "Error in SubmitForm()::" + e.getMessage());
            e.printStackTrace();
            // tell user that the submission failed....
            Message msg = new Message();
            msg.what = 1;
            this.progressHandler.sendMessage(msg);

            return false;
        }

    }

    private class TransmitFormData implements Runnable {
        Form _form;
        Message msg;

        TransmitFormData(Form form) {
            this._form = form;
        }

        public void run() {

            try {
                msg = new Message();
                msg.what = 0;
                msg.obj = ("Connecting to Server");
                progressHandler.sendMessage(msg);

                URL url = new URL(_form.getSubmitTo());
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                BufferedOutputStream wr = new BufferedOutputStream
                        (conn.getOutputStream());
                String data = _form.getFormEncodedData();
                wr.write(data.getBytes());
                wr.flush();
                wr.close();

                msg = new Message();
                msg.what = 0;
                msg.obj = ("Data Sent");
                progressHandler.sendMessage(msg);

                // Get the response
                BufferedReader rd = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));
                String line = "";
                Boolean bSuccess = false;
                while ((line = rd.readLine()) != null) {
                    if (line.indexOf("SUCCESS") != -1) {
                        bSuccess = true;
                    }
                    // Process line...
                    Log.v(Tag, line);
                }
                wr.close();
                rd.close();

                if (bSuccess) {
                    msg = new Message();
                    msg.what = 0;
                    msg.obj = ("Form Submitted Successfully");
                    progressHandler.sendMessage(msg);

                    msg = new Message();
                    msg.what = 1;
                    progressHandler.sendMessage(msg);
                    return;

                }
            } catch (Exception e) {
                Log.d(Tag, "Failed to send form data: " + e.getMessage());
                msg = new Message();
                msg.what = 0;
                msg.obj = ("Error Sending Form Data");
                progressHandler.sendMessage(msg);
            }
            msg = new Message();
            msg.what = 2;
            progressHandler.sendMessage(msg);
        }

    }
}
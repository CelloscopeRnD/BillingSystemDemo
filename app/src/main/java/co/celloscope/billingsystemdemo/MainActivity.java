package co.celloscope.billingsystemdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    final String tag = MainActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnRunForm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText formNumber = (EditText) findViewById(R.id.formNumber);
                Log.i(tag, "Attempting to process Form # [" + formNumber.getText() + "]");
                Intent intent = new Intent(MainActivity.this, FormGenerator.class);
                intent.putExtra("formNumber", formNumber.getText().toString());
                startActivity(intent);
            }
        });
    }
}

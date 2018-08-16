package cz.ktweb.randomlistgenerator;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import static android.app.PendingIntent.getActivity;

public class MainWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        Load();
        setupListeners();
    }

    @Override
    protected void onDestroy() {
        Save();
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        Save();
        super.onStop();
    }

    private void setupListeners(){
        final Context c = this.getApplicationContext();
        View.OnKeyListener listener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        };
        findViewById(R.id.textViewLab).setOnKeyListener(listener);
        findViewById(R.id.textViewExpr).setOnKeyListener(listener);
        findViewById(R.id.textViewQ).setOnKeyListener(listener);
    }

    public int intFromTV(View view, int id) {
        TextView tv = view.getRootView().findViewById(id);
        String text = tv.getText().toString();
        return Integer.parseInt(text);
    }

    public boolean boolFromTV(View view, int id) {
        CheckBox tv = view.getRootView().findViewById(id);
        return tv.isChecked();
    }

    public String stringFromTV(View view, int id) {
        TextView tv = view.getRootView().findViewById(id);
        return tv.getText().toString();
    }

    public Generator generatorFromValues(View view) {
        return new Generator(
                Utils.normalizeExpression(stringFromTV(view, R.id.textViewExpr)),
                intFromTV(view, R.id.textViewQ),
                boolFromTV(view, R.id.checkBoxSort),
                boolFromTV(view, R.id.checkBoxUnique),
                stringFromTV(view, R.id.textViewLab)
                );
    }

    public void addGenerator(View view) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.generatorLayoutBox);

        View v = new GeneratorComponent(view.getContext(), null, generatorFromValues(view));
        ll.addView(v, 0);
    }

    public void showHelp(View view)
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help);
        TextView txt = (TextView)dialog.findViewById(R.id.textbox);
        Spanned sp = Html.fromHtml( getString(R.string.help_intro));
        txt.setText(sp);
        dialog.show();
    }


    public void addGeneratorClicked(View view) {
        addGenerator(view);
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        //ScrollView sc = view.getRootView().findViewById(R.id.scrollBox);
        //sc.fullScroll(View.FOCUS_DOWN);
    }

    public void generateAll(View view) {
        LinearLayout ll = view.getRootView().findViewById(R.id.generatorLayoutBox);
        for(int i = 0; i < ll.getChildCount(); i++) {
            View vg = ll.getChildAt(i);
            if(vg instanceof GeneratorComponent)
            {
                GeneratorComponent gc = (GeneratorComponent)vg;
                gc.Generate();
            }
        }
    }

    public void Generate(View view)
    {
        ((GeneratorComponent)view.getTag()).Generate();
    }

    public void Delete(View view)
    {
        LinearLayout ll = (LinearLayout) findViewById(R.id.generatorLayoutBox);
        View v = (GeneratorComponent)(view.getTag());
        ll.removeView(v);
    }

    public void MoveDown(View view)
    {
        LinearLayout ll = (LinearLayout) findViewById(R.id.generatorLayoutBox);
        View v = (GeneratorComponent)(view.getTag());
        int idx = ll.indexOfChild(v);
        idx = idx + 1 == ll.getChildCount() ? idx : idx + 1;
        ll.removeView(v);
        ll.addView(v, idx);
    }

    public void Save()
    {
        String filename = "cfg";
        String fileContents = "";

        LinearLayout ll = this.findViewById(R.id.generatorLayoutBox);
        for(int i = 0; i < ll.getChildCount(); i++) {
            View vg = ll.getChildAt(i);
            if(vg instanceof GeneratorComponent)
            {
                GeneratorComponent gc = (GeneratorComponent)vg;
                fileContents += gc.getGenerator().toString() + "\n";
            }
        }

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            Log.i("rnd", "Successfully saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Load()
    {
        String filename = "cfg";
        File directory = this.getApplicationContext().getFilesDir();
        File file = new File(directory, filename);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                Generator g = Generator.fromString(line);
                LinearLayout ll = (LinearLayout) findViewById(R.id.generatorLayoutBox);
                View v = new GeneratorComponent(this.getBaseContext(), null, g);
                ll.addView(v);
            }
            br.close();
            Log.i("rnd", "Successfully loaded!");
        }
        catch (IOException e) {
        }
    }


}

package cz.ktweb.randomlistgenerator;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GeneratorComponent extends ConstraintLayout {
    private Generator g;

    private void init()
    {
        this.findViewById(R.id.generateButton).setTag(this);
        this.findViewById(R.id.deleteButton).setTag(this);
        this.findViewById(R.id.downButton).setTag(this);
        Refresh();
    }

    public GeneratorComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        g = new Generator();
        init();
    }

    public GeneratorComponent(Context context, AttributeSet attrs, Generator g) {
        super(context, attrs);
        this.g = g;
        Log.d("myTag", "Component Created");
        View v; // Creating an instance for View Object
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.generator_component, this);
        init();
    }

    public void Refresh()
    {
        TextView tv = (TextView)this.findViewById(R.id.valueList);
        tv.setText(g.viewString());
    }

    public void Generate()
    {
        g.Generate();
        Refresh();
    }

    public Generator getGenerator()
    {
        return g;
    }
}

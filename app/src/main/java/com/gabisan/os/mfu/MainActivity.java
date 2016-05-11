package com.gabisan.os.mfu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gabisan.os.mfu.SettingsActivity.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_ref_string_input)
    TextView tvRefStrInput;
    static TextView tvPages;

    private String TAG = MainActivity.class.getSimpleName();
    protected String references[];
    int buffSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        tvPages = (TextView) findViewById(R.id.tv_pages);

        ButterKnife.bind(this);

        Bundle intent = getIntent().getExtras();
        references = intent.getStringArray("INPUT_REF");
        buffSize = intent.getInt("INPUT_BUFFSIZE");

        tvRefStrInput.setText("REFERENCES: ");
        for (int i = 0; i < references.length; i++) {
            tvRefStrInput.append(references[i] + " ");
        }
        tvRefStrInput.append("\nBuffer Size: " + buffSize);

        new Simulate().execute(new SimulateTaskParams(buffSize, references));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class SimulateTaskParams {
        int buffer;
        String[] references;

        public SimulateTaskParams(int buffer, String[] references) {
            this.buffer = buffer;
            this.references = references;
        }
    }

    static class Simulate extends AsyncTask<SimulateTaskParams, Void, Void> {
        private String TAG = Simulate.class.getSimpleName();

        int buffer;
        int[] refOccurrence;
        int victimFrameIndex;
        String[] references;
        String[] pages, pages2;

        @Override
        protected Void doInBackground(SimulateTaskParams... params) {
            buffer = params[0].buffer;
            refOccurrence = new int[buffer];
            references = params[0].references;
            pages = new String[buffer];
            pages2 = new String[buffer];
//            Log.d(TAG, "Buffer Size: " + buffer +
//                    "\nRef Size: " + references.length);

            for (int i = 0; i < buffer; i++) {
                pages[i] = ""; //default value (not yet paged)
                refOccurrence[i] = 0; //default values of occurrence counter
            }

            int j = 0;
            for (int i = 0; i < buffer; i++) {
                if (j < references.length) {
                    for (int k = 0; k < pages.length; k++) {
//                        if (references[j].equals(pages[k])) {
//                            refOccurrence[k] += 1;
//                        }
                        if (pages[k].equals(pages2[k])){
                            refOccurrence[k] += 1;
                        }
                    }
                    if (pages[i].equals("")) {
                        pages[i] = references[j];
                        pages2[i] = references[j];
                        refOccurrence[i] += 1;
                        try {
                            publishProgress();
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (pages[i] != "") {

                    } else {
                        victimFrameIndex = getMostFrequentRefIndex();
                        pages[victimFrameIndex] = references[j];
                        pages2[victimFrameIndex] = references[j];
                        refOccurrence[victimFrameIndex] = 1;
                        try {
                            publishProgress();
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int m = 0; m < refOccurrence.length; m++) {
                        Log.d(TAG, "Occurence[" + m + "] = " + refOccurrence[m]);
                    }
                    j++;
                }
                if (i == buffer - 1)
                    i = 0;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < pages.length; i++) {
                tvPages.append("| " + pages[i] + " |");
//                tvPages.append("\n");
            }
            tvPages.append("\n");
//            tvPages.append("\t\t");
        }

        public int getMostFrequentRefIndex() {
            int index = 0;
            int max = refOccurrence[0];
            for (int i = 0; i < refOccurrence.length; i++) {
                if (refOccurrence[i] > max) {
                    index = i;
                }
            }

            return index;
        }
    }

}

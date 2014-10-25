package co.edu.eafit.dis.numerical.views;

import co.edu.eafit.dis.numerical.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends Activity {
  
  private TextView methodTitle;
  private TextView results;
  private Button showResultsTable;
  private String methodName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_results);
    
    //Activar el bot�n de ir atr�s en el action bar
    getActionBar().setDisplayHomeAsUpEnabled(true);
    
    methodTitle = (TextView) findViewById(R.id.method_title);
    results = (TextView) findViewById(R.id.results_text_view);
    showResultsTable = (Button) findViewById(R.id.show_table_button);
    
    methodName = getIntent().getExtras().getString(getResources()
          .getString(R.string.text_key_method_name));
    String resultsText = getIntent().getExtras().getString(getResources()
          .getString(R.string.text_key_results));
    setUpResults(methodName, resultsText);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  public void showResultsTable(View v) {
    Intent resultsTableIntent = new Intent(ResultsActivity.this,
        ResultsTableActivity.class);
    String methodNameKey = getResources()
        .getString(R.string.text_key_method_name);
    resultsTableIntent.putExtra(methodNameKey, methodName);
    ResultsActivity.this.startActivity(resultsTableIntent);
  }
  
  public void setUpResults(String methodName, String resultsText) {
    methodTitle.setText(methodName);
    results.setText(resultsText);
  }
}
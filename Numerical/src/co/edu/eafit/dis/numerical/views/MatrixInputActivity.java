package co.edu.eafit.dis.numerical.views;

import java.util.Iterator;
import java.util.Vector;

import co.edu.eafit.dis.numerical.R;
import co.edu.eafit.dis.numerical.R.id;
import co.edu.eafit.dis.numerical.R.layout;
import co.edu.eafit.dis.numerical.methods.GaussianElimination;
import co.edu.eafit.dis.numerical.methods.LUFactorization;
import co.edu.eafit.dis.numerical.methods.VariableSolver;
import co.edu.eafit.dis.numerical.utils.InputChecker;
import co.edu.eafit.dis.numerical.utils.PreferencesManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MatrixInputActivity extends Activity {

  private static final String TAG = MatrixInputActivity.class.getSimpleName();

  private TextView subsectionTextView;

  /* Vistas */
  private Spinner methodSpinner;
  private TableLayout inputMatrix;
  private TableLayout inputVector;

  /* Variables */
  private String subsectionName;
  private int methodSelection;
  private int methodType;

  /* M�todos */
  private GaussianElimination gaussianElimination;
  private LUFactorization luFactorization;

  /* Contenidos */
  private int matrixSize;
  private EditText[][] inputMatrixEdits;
  private EditText[] inputVectorEdits;
  private double[][] matrix;
  private double[] vector;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_matrix_input);

    // Activar el bot�n de ir atr�s en el action bar
    getActionBar().setDisplayHomeAsUpEnabled(true);

    subsectionTextView = (TextView) findViewById(R.id.subsection_title);
    methodSpinner = (Spinner) findViewById(R.id.method_spinner);

    subsectionName = getIntent().getExtras().getString(
        getResources().getString(R.string.text_key_subsection_name));
    matrixSize = getIntent().getExtras().getInt(
        getResources().getString(R.string.text_key_matrix_size));
    methodType = getIntent().getExtras().getInt(
        getResources().getString(R.string.text_key_method_type));

    ArrayAdapter<CharSequence> adapter;

    switch (methodType) {
      case ResultsMatrixActivity.ELIMINATION_TYPE:
        adapter = ArrayAdapter.createFromResource(this, R.array.gaussian_array,
            android.R.layout.simple_spinner_item);
        break;
      case ResultsMatrixActivity.FACTORIZATION_TYPE:
        adapter = ArrayAdapter.createFromResource(this,
            R.array.lu_factorization_array,
            android.R.layout.simple_spinner_item);
        break;
      default:
        adapter = ArrayAdapter.createFromResource(this, R.array.gaussian_array,
            android.R.layout.simple_spinner_item);
    }

    // Specify the layout to use when the list of choices appears
    adapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    methodSpinner.setAdapter(adapter);
    methodSpinner
        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          /**
           * Called when a new item is selected (in the Spinner)
           */
          public void onItemSelected(AdapterView<?> parent, View view, int pos,
              long id) {
            // An spinnerItem was selected.
            // You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            methodSelection = pos;
          }

          public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing, just another required interface callback
          }
        }); // (optional)

    inputMatrix = (TableLayout) findViewById(R.id.input_matrix_table);
    inputVector = (TableLayout) findViewById(R.id.input_vector_table);

    inputMatrixEdits = new EditText[matrixSize][matrixSize];
    inputVectorEdits = new EditText[matrixSize];
    // matrix = new double[matrixSize][matrixSize + 1];
    setUpViewWithSubsectionName(subsectionName);
    setUpViewWithInputMatrix(matrixSize);
    setUpViewWithInputVector(matrixSize);

    try {
      // Carga los datos
      fillFieldsWithStoredData();
    } catch (NullPointerException e) {
      // TODO: handle exception
    }
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

  public void setUpViewWithSubsectionName(String subsectionName) {
    subsectionTextView.setText(subsectionName);
  }

  private void setUpViewWithInputMatrix(int matrixSize) {
    inputMatrix.removeAllViews();
    for (int i = 0; i < matrixSize; ++i) {
      TableRow row = new TableRow(MatrixInputActivity.this);
      row.setLayoutParams(new TableRow.LayoutParams(
          TableRow.LayoutParams.WRAP_CONTENT,
          TableRow.LayoutParams.WRAP_CONTENT));
      for (int j = 0; j < matrixSize; ++j) {
        inputMatrixEdits[i][j] = new EditText(MatrixInputActivity.this);
        inputMatrixEdits[i][j].setInputType(InputType.TYPE_CLASS_NUMBER
            | InputType.TYPE_NUMBER_FLAG_DECIMAL
            | InputType.TYPE_NUMBER_FLAG_SIGNED);
        inputMatrixEdits[i][j].setLayoutParams(new TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT));
        inputMatrixEdits[i][j].setGravity(Gravity.CENTER);
        inputMatrixEdits[i][j].setHint(getResources().getString(
            R.string.text_hint_input_matrix_index, i, j));
        if (j == 0) { // Si es la columna inicial entonces se a�ade una barra
          EditText separator = new EditText(MatrixInputActivity.this);
          separator.setLayoutParams(new TableRow.LayoutParams(1,
              LayoutParams.WRAP_CONTENT));
          separator.setEnabled(false);
          separator.setBackgroundColor(Color.BLACK);
          separator.setFocusable(false);
          row.addView(separator);
        }
        inputMatrixEdits[i][j].setTextColor(Color.BLACK);
        row.addView(inputMatrixEdits[i][j]);
        EditText separator = new EditText(MatrixInputActivity.this);
        separator.setLayoutParams(new TableRow.LayoutParams(1,
            LayoutParams.WRAP_CONTENT));
        separator.setEnabled(false);
        separator.setBackgroundColor(Color.BLACK);
        separator.setFocusable(false);
        row.addView(separator);
      }
      inputMatrix.addView(row);
    }
  }

  private void setUpViewWithInputVector(int matrixSize) {
    inputVector.removeAllViews();
    TableRow row = new TableRow(MatrixInputActivity.this);
    row.setLayoutParams(new TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
    for (int j = 0; j < matrixSize; ++j) {
      inputVectorEdits[j] = new EditText(MatrixInputActivity.this);
      inputVectorEdits[j].setInputType(InputType.TYPE_CLASS_NUMBER
          | InputType.TYPE_NUMBER_FLAG_DECIMAL
          | InputType.TYPE_NUMBER_FLAG_SIGNED);
      inputVectorEdits[j].setLayoutParams(new TableRow.LayoutParams(
          TableRow.LayoutParams.WRAP_CONTENT,
          TableRow.LayoutParams.WRAP_CONTENT));
      inputVectorEdits[j].setGravity(Gravity.CENTER);
      inputVectorEdits[j].setHint(getResources().getString(
          R.string.text_hint_input_vector_index, j));
      if (j == 0) { // Si es la columna inicial entonces se a�ade una barra
        EditText separator = new EditText(MatrixInputActivity.this);
        separator.setLayoutParams(new TableRow.LayoutParams(1,
            LayoutParams.WRAP_CONTENT));
        separator.setEnabled(false);
        separator.setBackgroundColor(Color.BLACK);
        separator.setFocusable(false);
        row.addView(separator);
      }
      inputVectorEdits[j].setTextColor(Color.BLACK);
      row.addView(inputVectorEdits[j]);
      EditText separator = new EditText(MatrixInputActivity.this);
      separator.setLayoutParams(new TableRow.LayoutParams(1,
          LayoutParams.WRAP_CONTENT));
      separator.setEnabled(false);
      separator.setBackgroundColor(Color.BLACK);
      separator.setFocusable(false);
      row.addView(separator);
    }
    inputVector.addView(row);
  }

  // Llena los espacios de los editText con los datos de la matriz guardada
  private void fillFieldsWithStoredData() {
    PreferencesManager.setup(this);
    String[][] tempMatrix = PreferencesManager.getMatrix();

    if (inputMatrixEdits.length < tempMatrix.length) {
      for (int i = 0; i < inputMatrixEdits.length; i++) {
        for (int j = 0; j < inputMatrixEdits[0].length; j++) {
          inputMatrixEdits[i][j].setText(tempMatrix[i][j]);
        }
      }
    } else {
      for (int i = 0; i < tempMatrix.length; i++) {
        for (int j = 0; j < tempMatrix[0].length; j++) {
          inputMatrixEdits[i][j].setText(tempMatrix[i][j]);
        }
      }
    }
    // Lo mismo para los vectores!!
    String[] tempVector = PreferencesManager.getVector();

    if (inputVectorEdits.length < tempVector.length) {
      for (int i = 0; i < inputVectorEdits.length; i++) {
        inputVectorEdits[i].setText(tempVector[i]);
      }
    } else {
      for (int i = 0; i < tempVector.length; i++) {
        inputVectorEdits[i].setText(tempVector[i]);
      }
    }
  }

  // Guarda los datos de la matriz en SharedPreferences
  private void storeDataFromFields() {
    String[][] tempMatrix = new String[inputMatrixEdits.length][inputMatrixEdits[0].length];
    for (int i = 0; i < tempMatrix.length; i++) {
      for (int j = 0; j < tempMatrix[0].length; j++) {
        tempMatrix[i][j] = inputMatrixEdits[i][j].getText().toString().trim();
      }
    }
    PreferencesManager.setMatrix(tempMatrix);
    // Lo mismo para los vectores
    String[] tempVector = new String[inputVectorEdits.length];
    for (int i = 0; i < tempVector.length; i++) {
      tempVector[i] = inputVectorEdits[i].getText().toString().trim();
    }
    PreferencesManager.setVector(tempVector);
  }

  private boolean isInputValid() {
    switch (methodType) {
      case ResultsMatrixActivity.ELIMINATION_TYPE:
        matrix = new double[matrixSize][matrixSize + 1];
        break;
      case ResultsMatrixActivity.FACTORIZATION_TYPE:
        matrix = new double[matrixSize][matrixSize];
        vector = new double[matrixSize];
        break;
    }
    boolean allCorrect = true;
    for (int i = 0; i < matrixSize; ++i) {
      for (int j = 0; j < matrixSize; ++j) {
        String inputText = inputMatrixEdits[i][j].getText().toString().trim();
        if (inputText.isEmpty()) {
          inputMatrixEdits[i][j].setError(getResources().getString(
              R.string.input_required_error));
          allCorrect = false;
        } else if (!InputChecker.isDouble(inputText)) {
          inputMatrixEdits[i][j].setError(getResources().getString(
              R.string.not_a_number_error));
          allCorrect = false;
        } else {
          matrix[i][j] = Double.valueOf(inputText);
        }
      }
    }
    for (int j = 0; j < matrixSize; ++j) {
      String inputText = inputVectorEdits[j].getText().toString().trim();
      if (inputText.isEmpty()) {
        inputVectorEdits[j].setError(getResources().getString(
            R.string.input_required_error));
        allCorrect = false;
      } else if (!InputChecker.isDouble(inputText)) {
        inputVectorEdits[j].setError(getResources().getString(
            R.string.not_a_number_error));
        allCorrect = false;
      } else {
        if (methodType == ResultsMatrixActivity.ELIMINATION_TYPE)
          matrix[j][matrixSize] = Double.valueOf(inputText);
        if (methodType == ResultsMatrixActivity.FACTORIZATION_TYPE)
          vector[j] = Double.valueOf(inputText);
      }
    }
    return allCorrect;
  }

  public void calculate(View view) {
    if (!isInputValid())
      return;

    Intent resultsIntent;
    String methodName, resultText;
    String methodNameKey = getResources().getString(
        R.string.text_key_method_name);
    String methodTypeKey = getResources().getString(
        R.string.text_key_method_type);
    String methodResult = getResources().getString(R.string.text_key_results);
    double[] solution;
    if (methodType == ResultsMatrixActivity.ELIMINATION_TYPE) {
      gaussianElimination = new GaussianElimination(this);
      switch (methodSelection) {
        case 0:
          methodName = getResources().getString(
              R.string.title_activity_gaussian_elim_without);
          try {
            solution = gaussianElimination.calculateSimpleGaussianElimiation(
                matrixSize, matrix);
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultText = VariableSolver.getStringSolution(solution);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            resultsIntent.putExtra(methodResult, resultText);
          } catch (Exception e) {
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            // TODO Cambiar este error por String de XML
            resultsIntent.putExtra(methodResult, "ERROR TEMPORAL");
          }
          break;
        case 1:
          methodName = getResources().getString(
              R.string.title_activity_gaussian_elim_partial);
          try {
            solution = gaussianElimination
                .calculateGaussianEliminationPartialPivoting(matrixSize, matrix);
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultText = VariableSolver.getStringSolution(solution);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            resultsIntent.putExtra(methodResult, resultText);
          } catch (Exception e) {
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            // TODO Cambiar este error por String de XML
            resultsIntent.putExtra(methodResult, "ERROR TEMPORAL");
          }
          break;
        case 2:
          methodName = getResources().getString(
              R.string.title_activity_gaussian_elim_total);
          try {
            solution = gaussianElimination
                .calculateGaussianEliminationTotalPivoting(matrixSize, matrix);
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultText = VariableSolver.getStringSolution(solution);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            resultsIntent.putExtra(methodResult, resultText);
          } catch (Exception e) {
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            // TODO Cambiar este error por String de XML
            resultsIntent.putExtra(methodResult, "ERROR TEMPORAL");
          }
          break;
        default:
          resultsIntent = new Intent();
          break;
      }
    }

    else if (methodType == ResultsMatrixActivity.FACTORIZATION_TYPE) {
      luFactorization = new LUFactorization(this);
      switch (methodSelection) {
        case 0:
          methodName = getResources().getString(R.string.text_crout);
          try {
            solution = luFactorization.calculateCrout(matrixSize, matrix,
                vector);
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultText = VariableSolver.getStringSolution(solution);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            resultsIntent.putExtra(methodResult, resultText);
          } catch (Exception e) {
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            // TODO Cambiar este error por String de XML
            resultsIntent.putExtra(methodResult, "ERROR TEMPORAL");
          }
          break;
        case 1:
          methodName = getResources().getString(R.string.text_doolittle);
          try {
            solution = luFactorization.calculateDoolittle(matrixSize, matrix,
                vector);
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultText = VariableSolver.getStringSolution(solution);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            resultsIntent.putExtra(methodResult, resultText);
          } catch (Exception e) {
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            // TODO Cambiar este error por String de XML
            resultsIntent.putExtra(methodResult, "ERROR TEMPORAL");
          }
          break;
        case 2:
          methodName = getResources().getString(R.string.text_cholesky);
          try {
            solution = luFactorization.calculateCholesky(matrixSize, matrix,
                vector);
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultText = VariableSolver.getStringSolution(solution);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            resultsIntent.putExtra(methodResult, resultText);
          } catch (Exception e) {
            resultsIntent = new Intent(MatrixInputActivity.this,
                ResultsActivity.class);
            resultsIntent.putExtra(methodNameKey, methodName);
            resultsIntent.putExtra(methodTypeKey,
                ResultsActivity.SYSTEMS_OF_EQUATIONS);
            // TODO Cambiar este error por String de XML
            resultsIntent.putExtra(methodResult, "ERROR TEMPORAL");
          }
          break;
        default:
          resultsIntent = new Intent();
          break;
      }
    }
    
    else {
      resultsIntent = new Intent();
    }
    // Datos correctos, guardarlos en PreferencesManager
    storeDataFromFields();

    MatrixInputActivity.this.startActivity(resultsIntent);
  }
}
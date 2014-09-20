package co.edu.eafit.dis.numerical.methods;


import android.content.Context;
import co.edu.eafit.dis.numerical.R;
import co.edu.eafit.dis.numerical.utils.FunctionsEvaluator;

public class FixedPoint {
  
  private FunctionsEvaluator functionEvaluator = null;
  private FunctionsEvaluator gFunctionEvaluator = null;
  private Context c = null;
  
  public FixedPoint (Context c){
    functionEvaluator = FunctionsEvaluator.getInstance(c);
    gFunctionEvaluator = FunctionsEvaluator.getInstance(c);
    this.c = c;
  }
  
  public FixedPoint(Context c, String function, String gFunction) throws Exception {
    functionEvaluator = FunctionsEvaluator.getInstance(c);
    gFunctionEvaluator = FunctionsEvaluator.getInstance(c);
    this.c = c;
    try {
      this.setFunction(function);
      this.setGfunction(gFunction);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
  
  /**
   * Retorna una arreglo de doubles, donde el primer valor es la ra�z
   * y el segundo valor indica si fue una aproximaci�n con una tolerancia
   * o fue una ra�z exacta, es decir, si el segundo valor es -1 quiere
   * decir que se encontr� la ra�z exactca, de lo contrario dicha posici�n
   * contendr� el valor de la tolerancia.
   */
  public double[] evaluate(double xa, double tol, double niter)
    throws Exception {
    double fx = functionEvaluator.calculate(xa);
    double[] result = new double[2];
    if (fx == 0) {
      result[0] = xa;
      result[1] = -1;
      return result;   //xi es ra�z
    }
    int cont=0;
    double error = tol + 1.0;
    while((fx!=0)&&(error>tol)&&(cont<niter)){
    	double xn = gFunctionEvaluator.calculate(xa);
    	fx = functionEvaluator.calculate(xn);
    	error = Math.abs(xn - xa);
    	xa = xn;
    	cont++;
    }
    if(fx==0){
    	result[0] = xa;//xa es raiz exacta
    	result[1] = -1;//Indica que es exacta
    	return result;
    }else if(error < tol){
    	result[0] = xa;//xa es aproximacion a raiz
    	result[1] = tol; //Se indica el valor de la tolerancia
    	return result;
    }
    else{
        String methodName = c.getResources()
                .getString(R.string.title_activity_fixed_point);
        String errorMessage = c.getString(
                  R.string.error_exceeded_iterations,
                  methodName); 
        throw new Exception(errorMessage);
    }
  }
  
  
  
  public void setFunction(String function) throws Exception {
    try {
      functionEvaluator.setFunction(function);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
  
  public void setGfunction(String function) throws Exception {
	    try {
	      gFunctionEvaluator.setFunction(function);
	    } catch (Exception e) {
	      throw new Exception(e.getMessage());
	    }
  }

}
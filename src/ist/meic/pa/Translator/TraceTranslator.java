package ist.meic.pa.Translator;

import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import ist.meic.pa.Trace;


/**
 * The Class TraceTranslator.
 * Implementation of a translator to each class loaded that will inject code
 * for making possible to trace objects
 */
public class TraceTranslator implements Translator {

	@Override
	public void onLoad(ClassPool arg0, String arg1) throws NotFoundException,
	CannotCompileException {
		CtClass cc = arg0.get(arg1);
		cc.setModifiers(Modifier.PUBLIC);
		makeTraceable(cc);
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
	CannotCompileException {

	}

	
	/**
	 * method for injecting, to each class and each method of it, code for 
	 * store information about arguments used in each method call, constructors called 
	 * and returned objects in method calls
	 * @param cc as the CtClass for which we want to inject code
	 * @throws CannotCompileException
	 */
	public static void makeTraceable(final CtClass cc) throws CannotCompileException{
		for(CtMethod ctMethod : cc.getDeclaredMethods()){
			if(cc.getSimpleName().equals("Trace") && !ctMethod.getName().equals("print"))
				continue;
			ctMethod.instrument(new ExprEditor(){
				public void edit(MethodCall m) throws CannotCompileException{
					if(!m.getClassName().equals("ist.meic.pa.History")){
					String behaviour = null;
					try {
						behaviour = m.getMethod().getLongName();
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
					m.replace("{ist.meic.pa.Trace.getArgs($args, \"" + behaviour + "\", \"" + m.getFileName() + "\", " + m.getLineNumber() + ");" + 
							"  $_ = $proceed($$);" + 
							"  ist.meic.pa.Trace.getReturn(($w)$_, \"" + behaviour + "\", \"" + m.getFileName() + "\", " + m.getLineNumber() + ");}");
					}
				}
				public void edit(NewExpr m) throws CannotCompileException{
					if(!m.getClassName().equals("ist.meic.pa.History")){
					String behaviour = null;
					try {
						behaviour = m.getConstructor().getLongName();
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
					m.replace("{$_ = $proceed($$);" +
							"	ist.meic.pa.Trace.clear(($w)$_);" + 
							"  ist.meic.pa.Trace.getReturn(($w)$_, \"" + behaviour + "\", \"" + m.getFileName() + "\", " + m.getLineNumber() + ");}");
					}
				}
			});
		}
	}

}

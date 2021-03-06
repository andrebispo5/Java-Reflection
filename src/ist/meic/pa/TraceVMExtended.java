package ist.meic.pa;

import ist.meic.pa.Translator.ExtensionTranslator;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

/**
 * The Class TraceVMExtended.
 * Class for associating an translator to every class that is loaded. Is responsible for
 * receiving in its main method a program name and its arguments and start its execution.
 * It's our entry class for this project, starting all the trace process.
 * Used for implementing extensions to the project. Supports exception handling detection, access fields detection
 * and cast detection
 */
public class TraceVMExtended {

private static ClassPool cp;
	

	
	public static void main(String[] args){
	    Translator t = new ExtensionTranslator();
	    cp = ClassPool.getDefault();
	    Loader cl=new Loader();
	    
	    try {
	    	cl.addTranslator(cp, t);
			if(args.length==0){
				System.err.println("Class name not provided!");
				return;
			}
			if(args.length>0){
				int argsSize = args.length-1;
				String[] arguments = new String[argsSize];
				System.arraycopy(args,1,arguments,0,argsSize);
				cl.run(args[0],arguments );
			}

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}  
	}
		
		
}



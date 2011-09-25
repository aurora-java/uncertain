/*
 * Created on 2007-6-25
 */
package uncertain.proc;

import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.OCManager;

public class Catch extends Procedure implements IExceptionHandle {
    
    String    _exception;
    String    _destination;
    String    _next_step;
    
    boolean   handle_any_type = false;
    Class     _exception_class;
    
    public Catch(){
        super();
    }
    
    public Catch(OCManager om){
        super(om);
    }    
    
    /**
     * @return XPath that caught exception will be put
     */
    public String getDestination() {
        return _destination;
    }

    /**
     * @param _destination XPath that caught exception will be put
     */
    public void setDestination(String _destination) {
        this._destination = _destination;
    }
    

    /**
     * @return the nextStep
     */
    public String getNextStep() {
        return _next_step;
    }

    /**
     * @param nextStep the nextStep to set
     */
    public void setNextStep(String nextStep) {
        _next_step = nextStep;
    }    

    /**
     * Set which type of exception will be handled
     * @param type Sub class of Throwable
     */
    public void setException(String type){
        _exception = type;
        if("*".equals(_exception)){
            handle_any_type = true;
            return;
        }
        try{
            _exception_class = Class.forName(_exception);
        }catch(ClassNotFoundException ex){
            throw BuiltinExceptionFactory.createClassNotFoundException(this, type);
        }
    }
    
    public String getException(){
        return _exception;
    }
    
    void locateRunner(ProcedureRunner runner){
        if(_next_step!=null){
            runner.locateTo(_next_step);
            runner.mResumeAfterException = true;
        }
    }
    /*
    public Catch(){
        System.out.println("created "+this);
    }
*/
    public boolean handleException(ProcedureRunner runner, Throwable exception) {
        boolean match_exception = handle_any_type;
        if(!handle_any_type){
            if(_exception_class==null)
                throw BuiltinExceptionFactory.createAttributeMissing(this, "exception");
            match_exception = _exception_class.isInstance(exception);
        }
        if(match_exception){
            /*
            // if 'destination' properties has been set, put the exception into specified path
            // in context
            if(_destination!=null){
                runner.getContext().putObject(_destination, exception, true);
                returnHandled(runner);
                return true;
            }
            try{
                run(runner);
                if(exception!=runner.getException())
                   return false;
                returnHandled(runner);
                return true;
            }catch(Exception ex){
                runner.throwException(ex);
                return false;
            }
            */
            if(_destination!=null)
                runner.getContext().putObject(_destination, exception, true);
            runner.run(this);
            Throwable thr = runner.getException();
            if(thr!=exception){                
                return false;
            }
            locateRunner(runner);
            return true;
/*
                try{

        }catch(Exception ex){
                runner.throwException(ex);
                return false;                
            }
*/            
            
        }else
            return false;

    }


}

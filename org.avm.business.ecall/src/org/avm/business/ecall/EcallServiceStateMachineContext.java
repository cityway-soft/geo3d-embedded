/*
 * ex: set ro:
 * DO NOT EDIT.
 * generated by smc (http://smc.sourceforge.net/)
 * from file : EcallServiceStateMachineContext.sm
 */

package org.avm.business.ecall;


public class EcallServiceStateMachineContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public EcallServiceStateMachineContext(EcallServiceStateMachine owner)
    {
        super (EcallMap.NoAlert);

        _owner = owner;
    }

    public EcallServiceStateMachineContext(EcallServiceStateMachine owner, EcallServiceStateMachineState initState)
    {
        super (initState);

        _owner = owner;
    }

    public void enterStartState()
    {
        getState().Entry(this);
        return;
    }

    public void ack(String phone)
    {
        _transition = "ack";
        getState().ack(this, phone);
        _transition = "";
        return;
    }

    public void endEcall()
    {
        _transition = "endEcall";
        getState().endEcall(this);
        _transition = "";
        return;
    }

    public void startEcall()
    {
        _transition = "startEcall";
        getState().startEcall(this);
        _transition = "";
        return;
    }

    public EcallServiceStateMachineState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((EcallServiceStateMachineState) _state);
    }

    protected EcallServiceStateMachine getOwner()
    {
        return (_owner);
    }

    public void setOwner(EcallServiceStateMachine owner)
    {
        if (owner == null)
        {
            throw (
                new NullPointerException(
                    "null owner"));
        }
        else
        {
            _owner = owner;
        }

        return;
    }

//---------------------------------------------------------------
// Member data.
//

    transient private EcallServiceStateMachine _owner;

    public static abstract class EcallServiceStateMachineState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EcallServiceStateMachineState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EcallServiceStateMachineContext context) {}
        protected void Exit(EcallServiceStateMachineContext context) {}

        protected void ack(EcallServiceStateMachineContext context, String phone)
        {
            Default(context);
        }

        protected void endEcall(EcallServiceStateMachineContext context)
        {
            Default(context);
        }

        protected void startEcall(EcallServiceStateMachineContext context)
        {
            Default(context);
        }

        protected void Default(EcallServiceStateMachineContext context)
        {
            throw (
                new statemap.TransitionUndefinedException(
                    "State: " +
                    context.getState().getName() +
                    ", Transition: " +
                    context.getTransition()));
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class EcallMap
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final EcallMap_NoAlert NoAlert =
            new EcallMap_NoAlert("EcallMap.NoAlert", 0);
        public static final EcallMap_WaitAck WaitAck =
            new EcallMap_WaitAck("EcallMap.WaitAck", 1);
        public static final EcallMap_ListenMode ListenMode =
            new EcallMap_ListenMode("EcallMap.ListenMode", 2);
        private static final EcallMap_Default Default =
            new EcallMap_Default("EcallMap.Default", -1);

    }

    protected static class EcallMap_Default
        extends EcallServiceStateMachineState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EcallMap_Default(String name, int id)
        {
            super (name, id);
        }
    //-----------------------------------------------------------
    // Member data.
    //
    }

    private static final class EcallMap_NoAlert
        extends EcallMap_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private EcallMap_NoAlert(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EcallServiceStateMachineContext context)
            {
                EcallServiceStateMachine ctxt = context.getOwner();

            ctxt.entryNoAlert();
            return;
        }

        protected void startEcall(EcallServiceStateMachineContext context)
        {


            (context.getState()).Exit(context);
            context.setState(EcallMap.WaitAck);
            (context.getState()).Entry(context);
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    private static final class EcallMap_WaitAck
        extends EcallMap_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private EcallMap_WaitAck(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EcallServiceStateMachineContext context)
            {
                EcallServiceStateMachine ctxt = context.getOwner();

            ctxt.entryWaitAck();
            return;
        }

        protected void ack(EcallServiceStateMachineContext context, String phone)
        {
            EcallServiceStateMachine ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.call(phone);
            }
            finally
            {
                context.setState(EcallMap.ListenMode);
                (context.getState()).Entry(context);
            }
            return;
        }

        protected void endEcall(EcallServiceStateMachineContext context)
        {


            (context.getState()).Exit(context);
            context.setState(EcallMap.NoAlert);
            (context.getState()).Entry(context);
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    private static final class EcallMap_ListenMode
        extends EcallMap_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private EcallMap_ListenMode(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EcallServiceStateMachineContext context)
            {
                EcallServiceStateMachine ctxt = context.getOwner();

            ctxt.entryListenMode();
            return;
        }

        protected void ack(EcallServiceStateMachineContext context, String phone)
        {
            EcallServiceStateMachine ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.call(phone);
            }
            finally
            {
                context.setState(EcallMap.ListenMode);
                (context.getState()).Entry(context);
            }
            return;
        }

        protected void endEcall(EcallServiceStateMachineContext context)
        {


            (context.getState()).Exit(context);
            context.setState(EcallMap.NoAlert);
            (context.getState()).Entry(context);
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }
}

/*
 * Local variables:
 *  buffer-read-only: t
 * End:
 */

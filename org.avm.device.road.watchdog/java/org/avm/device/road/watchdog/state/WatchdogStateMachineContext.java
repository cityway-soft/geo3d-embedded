package org.avm.device.road.watchdog.state;

import java.util.*;

public final class WatchdogStateMachineContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public WatchdogStateMachineContext(WatchdogStateMachine owner)
    {
        super();

        _owner = owner;
        setState(WatchdogMap.APC);
        WatchdogMap.APC.Entry(this);
    }

    public WatchdogStateMachineContext(WatchdogStateMachine owner, WatchdogStateMachineState initState)
    {
        super();
        _owner = owner;
        setState(initState);
        initState.Entry(this);
    }

    public synchronized void powerDown()
    {
        _transition = "powerDown";
        getState().powerDown(this);
        _transition = "";
        return;
    }

    public synchronized void powerUp()
    {
        _transition = "powerUp";
        getState().powerUp(this);
        _transition = "";
        return;
    }

    public synchronized void timeout()
    {
        _transition = "timeout";
        getState().timeout(this);
        _transition = "";
        return;
    }

    public WatchdogStateMachineState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((WatchdogStateMachineState) _state);
    }

    protected WatchdogStateMachine getOwner()
    {
        return (_owner);
    }

    public void setOwner(WatchdogStateMachine owner)
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

    transient private WatchdogStateMachine _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class WatchdogStateMachineState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected WatchdogStateMachineState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(WatchdogStateMachineContext context) {}
        protected void Exit(WatchdogStateMachineContext context) {}

        protected void powerDown(WatchdogStateMachineContext context)
        {
            Default(context);
        }

        protected void powerUp(WatchdogStateMachineContext context)
        {
            Default(context);
        }

        protected void timeout(WatchdogStateMachineContext context)
        {
            Default(context);
        }

        protected void Default(WatchdogStateMachineContext context)
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

    /* package */ static abstract class WatchdogMap
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
        public static final WatchdogMap_Default.WatchdogMap_APC APC =
            new WatchdogMap_Default.WatchdogMap_APC("WatchdogMap.APC", 0);
        public static final WatchdogMap_Default.WatchdogMap_NAPC NAPC =
            new WatchdogMap_Default.WatchdogMap_NAPC("WatchdogMap.NAPC", 1);
        public static final WatchdogMap_Default.WatchdogMap_Sleep Sleep =
            new WatchdogMap_Default.WatchdogMap_Sleep("WatchdogMap.Sleep", 2);
        public static final WatchdogMap_Default.WatchdogMap_Shutdown Shutdown =
            new WatchdogMap_Default.WatchdogMap_Shutdown("WatchdogMap.Shutdown", 3);
        private static final WatchdogMap_Default Default =
            new WatchdogMap_Default("WatchdogMap.Default", -1);

    }

    protected static class WatchdogMap_Default
        extends WatchdogStateMachineState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected WatchdogMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void powerDown(WatchdogStateMachineContext context)
        {


            return;
        }

        protected void powerUp(WatchdogStateMachineContext context)
        {


            return;
        }

        protected void timeout(WatchdogStateMachineContext context)
        {


            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class WatchdogMap_APC
            extends WatchdogMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private WatchdogMap_APC(String name, int id)
            {
                super (name, id);
            }

            protected void powerDown(WatchdogStateMachineContext context)
            {


                (context.getState()).Exit(context);
                context.setState(WatchdogMap.NAPC);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class WatchdogMap_NAPC
            extends WatchdogMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private WatchdogMap_NAPC(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(WatchdogStateMachineContext context)
            {
                WatchdogStateMachine ctxt = context.getOwner();

                ctxt.startTimer();
                return;
            }

            protected void Exit(WatchdogStateMachineContext context)
            {
                WatchdogStateMachine ctxt = context.getOwner();

                ctxt.stopTimer();
                return;
            }

            protected void powerUp(WatchdogStateMachineContext context)
            {


                (context.getState()).Exit(context);
                context.setState(WatchdogMap.APC);
                (context.getState()).Entry(context);
                return;
            }

            protected void timeout(WatchdogStateMachineContext context)
            {


                (context.getState()).Exit(context);
                context.setState(WatchdogMap.Sleep);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class WatchdogMap_Sleep
            extends WatchdogMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private WatchdogMap_Sleep(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(WatchdogStateMachineContext context)
            {
                WatchdogStateMachine ctxt = context.getOwner();

                ctxt.sleep();
                return;
            }

            protected void powerDown(WatchdogStateMachineContext context)
            {
                WatchdogStateMachine ctxt = context.getOwner();

                if (ctxt.wakeUpRTC() == true )
                {

                    (context.getState()).Exit(context);
                    // No actions.
                    context.setState(WatchdogMap.Shutdown);
                    (context.getState()).Entry(context);
                }
                else
                {
                    super.powerDown(context);
                }

                return;
            }

            protected void powerUp(WatchdogStateMachineContext context)
            {
                WatchdogStateMachine ctxt = context.getOwner();

                if (ctxt.wakeUpRTC() == false )
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.wakeUp();
                    }
                    finally
                    {
                        context.setState(WatchdogMap.APC);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    super.powerUp(context);
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class WatchdogMap_Shutdown
            extends WatchdogMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private WatchdogMap_Shutdown(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(WatchdogStateMachineContext context)
            {
                WatchdogStateMachine ctxt = context.getOwner();

                ctxt.shutdown();
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
